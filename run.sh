#!/usr/bin/env sh
# Compila y ejecuta el APE 4 - Fase 2 en Linux/macOS.
# Uso: ./run.sh | ./run.sh menu | ./run.sh pruebas
set -e
mkdir -p out
javac -encoding UTF-8 -d out src/*.java test/*.java
if [ "$1" = "pruebas" ]; then
    java -Dstdout.encoding=UTF-8 -cp out PruebasRedSocial
else
    java -Dstdout.encoding=UTF-8 -cp out Main "$@"
fi
