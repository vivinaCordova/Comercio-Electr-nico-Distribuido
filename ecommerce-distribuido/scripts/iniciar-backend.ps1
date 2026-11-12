param(
    [ValidateSet('api-gateway','ms-catalogo','ms-ordenes','ms-carrito','ms-notificaciones')]
    [string]$Servicio='api-gateway'
)
$Raiz = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$EnvPath = Join-Path $Raiz '.env'
if (Test-Path $EnvPath) {
    foreach ($Linea in Get-Content $EnvPath) {
        $Linea = $Linea.Trim()
        if (-not $Linea -or $Linea.StartsWith('#')) { continue }
        $Par = $Linea -split '=',2
        if ($Par.Length -eq 2) { Set-Item -Path ('Env:' + $Par[0].Trim()) -Value $Par[1].Trim() }
    }
}
$Ruta = Join-Path $Raiz ('backend/' + $Servicio)
Push-Location $Ruta
try { mvn spring-boot:run } finally { Pop-Location }
