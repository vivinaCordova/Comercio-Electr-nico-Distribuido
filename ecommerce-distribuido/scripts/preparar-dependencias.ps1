#Requires -Version 5.1
<#
Ejecutar desde cualquier ubicacion:
 powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\preparar-dependencias.ps1
Requiere JDK 21, Node.js 22 y Maven instalado para la primera configuracion.
No instala software del sistema ni utiliza Docker.
#>
[CmdletBinding()]
param([switch]$SoloVerificar)
$ErrorActionPreference = 'Stop'
$raiz = Split-Path -Parent $PSScriptRoot
$frontend = Join-Path $raiz 'frontend'
$servicios = @('api-gateway','ms-catalogo','ms-ordenes','ms-carrito','ms-notificaciones')
$versiones = [ordered]@{
  Java = '21'
  Node = '22.16.0'
  MavenWrapper = '3.9.9'
  MavenWrapperPlugin = '3.3.2'
  SpringBoot = '3.3.7'
  SpringCloud = '2023.0.5'
  React = '18.3.1'
  ReactDOM = '18.3.1'
  ReactRouterDOM = '6.28.0'
  Axios = '1.7.9'
  Vite = '6.0.5'
  ViteReactPlugin = '4.3.4'
}
function Comando([string]$nombre) {
  if (-not (Get-Command $nombre -ErrorAction SilentlyContinue)) {
    throw "No se encontro '$nombre' en PATH. Instalalo antes de continuar (consulta README.md)."
  }
}
function Ejecutar([string]$exe,[string[]]$argumentos) {
  & $exe @argumentos
  if ($LASTEXITCODE -ne 0) { throw "Error ejecutando: $exe $($argumentos -join ' ') (codigo $LASTEXITCODE)" }
}
Write-Host '=== Versiones centralizadas para ecommerce-distribuido ===' -ForegroundColor Cyan
$versiones.GetEnumerator() | ForEach-Object { Write-Host ("{0,-20} {1}" -f $_.Key,$_.Value) }
Comando java
Comando node
Comando npm
Comando mvn
$javaTexto = (& java -version 2>&1 | Out-String)
$nodeActual = (& node --version).Trim().TrimStart('v')
$mavenTexto = (& mvn -version | Out-String)
if ($javaTexto -notmatch 'version "21\.|openjdk 21[\.\s]') { throw "Se requiere Java 21. Detectado: $javaTexto" }
if ($nodeActual -ne $versiones.Node) { throw "Node debe ser $($versiones.Node) para reproducibilidad. Detectado: $nodeActual" }
if ($mavenTexto -notmatch 'Apache Maven') { throw 'No se pudo detectar una instalacion valida de Maven.' }
if ($SoloVerificar) {
  Write-Host 'Requisitos generales comprobados. No se instalaron dependencias.' -ForegroundColor Green
  exit 0
}
# Instala dependencias npm y genera el lockfile reproducible.
$packageJson = Join-Path $frontend 'package.json'
if (-not (Test-Path $packageJson)) { throw "No existe $packageJson" }
$package = Get-Content $packageJson -Raw | ConvertFrom-Json
$package.dependencies.react = $versiones.React
$package.dependencies.'react-dom' = $versiones.ReactDOM
$package.dependencies.'react-router-dom' = $versiones.ReactRouterDOM
$package.dependencies.axios = $versiones.Axios
$package.devDependencies.vite = $versiones.Vite
$package.devDependencies.'@vitejs/plugin-react' = $versiones.ViteReactPlugin
$package | Add-Member -NotePropertyName engines -NotePropertyValue @{node="=$($versiones.Node)"} -Force
$package | ConvertTo-Json -Depth 30 | Set-Content -Path $packageJson -Encoding UTF8
Set-Content -Path (Join-Path $raiz '.nvmrc') -Value $versiones.Node -Encoding ASCII
Push-Location $frontend
try {
  Write-Host '[Frontend] Generando package-lock.json...' -ForegroundColor Cyan
  Ejecutar npm @('install','--package-lock-only','--no-audit','--no-fund')
  Write-Host '[Frontend] Instalando dependencias exactas...' -ForegroundColor Cyan
  Ejecutar npm @('ci','--no-audit','--no-fund')
} finally { Pop-Location }
# Maven Wrapper oficial por cada proyecto; utiliza Maven solo durante bootstrap.
foreach ($servicio in $servicios) {
  $directorio = Join-Path (Join-Path $raiz 'backend') $servicio
  if (-not (Test-Path (Join-Path $directorio 'pom.xml'))) { throw "Falta pom.xml en $servicio" }
  Push-Location $directorio
  try {
    Write-Host "[Backend] Preparando Maven Wrapper: $servicio" -ForegroundColor Cyan
    Ejecutar mvn @('-N',"org.apache.maven.plugins:maven-wrapper-plugin:$($versiones.MavenWrapperPlugin):wrapper", "-Dmaven=$($versiones.MavenWrapper)",'-Dtype=only-script')
    if (-not (Test-Path '.mvn/wrapper/maven-wrapper.properties')) { throw "Wrapper incompleto en $servicio" }
    Write-Host "[Backend] Descargando dependencias Maven: $servicio" -ForegroundColor Cyan
    Ejecutar '.\mvnw.cmd' @('-B','dependency:go-offline','-DskipTests')
  } finally { Pop-Location }
}
Write-Host 'Preparacion completada. Conserva package-lock.json, .nvmrc y los archivos .mvn/wrapper en Git.' -ForegroundColor Green
Write-Host 'No subas frontend/node_modules ni backend/**/target.'
