@echo off
where mvn >nul 2>nul
if errorlevel 1 (echo Maven 3.9+ no encontrado en PATH. & exit /b 1)
mvn %*
