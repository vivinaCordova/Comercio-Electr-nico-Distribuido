$Raiz = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$Ruta = Join-Path $Raiz 'frontend'
Push-Location $Ruta
try {
    if (-not (Test-Path 'node_modules')) { npm install }
    npm run dev
} finally { Pop-Location }
