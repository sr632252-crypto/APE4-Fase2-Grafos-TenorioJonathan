# APE 4 · Fase 2 — Conexión Ambato (Grafos)

**Universidad Técnica de Ambato** · FISEI · Carrera de Software
**Asignatura:** Estructura de Datos · **Docente:** Ing. José Caiza, Mg.
**Nivel:** Tercero "B" · **Ciclo:** julio – diciembre 2026
**Estudiante:** Tenorio Ronquillo Jonathan Sebastian

Representación dinámica de una red social con una **multilista** (lista de adyacencia
con nodos propios) y operaciones sobre personas y relaciones. No se usan `HashMap`,
`HashSet`, `ArrayList` ni `LinkedList`.

- `AMISTAD`: relación recíproca, se guarda como **dos** aristas.
- `SEGUIR`: relación dirigida, se guarda como **una** arista (quien sigue → seguido).
- No se permiten lazos ni relaciones duplicadas del mismo tipo.
- Los identificadores se normalizan a minúsculas (`A01` y `a01` son la misma cuenta).

## Estructura

```
src/RedSocial.java          Modelo: TipoRelacion, Persona, Vertice, Arista y operaciones
src/Main.java               Secuencia de comprobación de la guía (Tabla 3) y menú interactivo
test/PruebasRedSocial.java  40 pruebas automatizadas sin librerías externas
run.bat / run.sh            Scripts para compilar y ejecutar
docs/Informe_APE4_Fase2.pdf Informe final (PDF)
docs/Informe_APE4_Fase2.docx Informe final editable (Word)
docs/img/                   Diagrama de la red y capturas de ejecución
```

## Compilar y ejecutar

Con los scripts:

```bash
run.bat            # Windows: secuencia de la guía (run.bat menu | run.bat pruebas)
./run.sh           # Linux/macOS: secuencia de la guía (./run.sh menu | ./run.sh pruebas)
```

Manualmente:

```bash
javac -encoding UTF-8 -d out src/*.java test/*.java
java -cp out Main              # secuencia de la guía
java -cp out Main menu         # menú interactivo
java -cp out PruebasRedSocial  # pruebas automatizadas
```

En la terminal de Windows, si las tildes no se ven bien, ejecute antes `chcp 65001`.

## Operaciones

| Operación | Retorno | Costo |
|---|---|---|
| `registrarPersona(u, n)` | true/false | O(V) |
| `eliminarPersona(u)` | true/false | O(V + A) |
| `agregarAmistad(a, b)` / `seguir(a, b)` | true/false | O(V + grado) |
| `eliminarAmistad(a, b)` / `dejarDeSeguir(a, b)` | true/false | O(V + grado) |
| `listarAmigos(u)` / `listarSeguidos(u)` | texto o null | O(V + grado) |
| `listarSeguidores(u)` | texto o null | O(V + A) |
| `contarAmigos(u)` | entero (−1 si no existe) | O(V + grado) |
| `mostrarRed()` | texto | O(V + A) |

## Resultados

Los ocho pasos de la secuencia de comprobación de la guía coinciden con lo esperado y
las 40 pruebas automatizadas pasan:

| Etapa | Personas | Amistades | Seguimientos | Aristas |
|---|---|---|---|---|
| Paso 1: red inicial | 6 | 5 | 0 | 10 |
| Pasos 2 y 3: seguir | 6 | 5 | 2 | 12 |
| Paso 5: eliminarAmistad(A04, A05) | 6 | 4 | 2 | 10 |
| Paso 6: eliminarPersona(A01) | 5 | 2 | 0 | 4 |

![Red Conexión Ambato](docs/img/red.png)
