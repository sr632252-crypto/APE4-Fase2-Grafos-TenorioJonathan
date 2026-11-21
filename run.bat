@echo off
REM Compila y ejecuta el APE 4 - Fase 2 en Windows.
REM Uso: run.bat          -> secuencia de la guía
REM      run.bat menu     -> menú interactivo
REM      run.bat pruebas  -> pruebas automatizadas
chcp 65001 > nul
if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java test\*.java || exit /b 1
if /I "%1"=="pruebas" (
    java -Dstdout.encoding=UTF-8 -cp out PruebasRedSocial
) else (
    java -Dstdout.encoding=UTF-8 -cp out Main %1
)
