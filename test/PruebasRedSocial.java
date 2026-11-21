/*
 * Pruebas de RedSocial (sin JUnit).
 *   javac -encoding UTF-8 -d out src/*.java test/*.java
 *   java -cp out PruebasRedSocial
 * Autor: Jonathan Tenorio
 */
public class PruebasRedSocial {

    private static int pasadas = 0;
    private static int fallidas = 0;

    private static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            pasadas++;
            System.out.println("[PASA]  " + nombre);
        } else {
            fallidas++;
            System.out.println("[FALLA] " + nombre);
        }
    }

    private static RedSocial redInicial() {
        return Main.cargarRedInicial(false);
    }

    public static void main(String[] args) {
        System.out.println("Pruebas automatizadas - Conexión Ambato (APE 4, Fase 2)\n");

        // Paso 1: estado inicial
        RedSocial red = redInicial();
        verificar("P01 Red inicial con 6 usuarios", red.contarPersonas() == 6);
        verificar("P02 Red inicial con 5 amistades", red.contarAmistades() == 5);
        verificar("P03 Cinco amistades = 10 aristas internas", red.contarAristas() == 10);
        verificar("P04 Grados 2,2,2,3,1,0",
                red.contarAmigos("A01") == 2 && red.contarAmigos("A02") == 2
                && red.contarAmigos("A03") == 2 && red.contarAmigos("A04") == 3
                && red.contarAmigos("A05") == 1 && red.contarAmigos("A06") == 0);

        // Búsqueda normalizada
        verificar("P05 A01 y a01 son la misma cuenta",
                red.existePersona("a01") && red.contarAmigos(" a01 ") == 2);

        // Pasos 2 y 3: seguir
        verificar("P06 seguir(A02,A01) devuelve true", red.seguir("A02", "A01"));
        verificar("P07 seguir(A06,A01) sin tener amigos", red.seguir("A06", "A01"));
        verificar("P08 Seguidores de Sofía: Mateo, Diego",
                "Mateo, Diego".equals(red.listarSeguidores("A01")));
        verificar("P09 Seguir es dirigido (Sofía no sigue a Mateo)",
                "".equals(red.listarSeguidos("A01")));
        verificar("P10 Seguir duplicado devuelve false", !red.seguir("A02", "A01"));
        verificar("P11 Seguir crea una sola arista", red.contarAristas() == 12);

        // Paso 4: listarAmigos en orden de inserción
        verificar("P12 listarAmigos(A04) = Valeria, Sofía, Camila",
                "Valeria, Sofía, Camila".equals(red.listarAmigos("A04")));

        // Paso 5: eliminar amistad
        verificar("P13 eliminarAmistad(A04,A05) devuelve true", red.eliminarAmistad("A04", "A05"));
        verificar("P14 Andrés pierde a Camila", red.contarAmigos("A04") == 2);
        verificar("P15 Camila pierde a Andrés (reciprocidad)", red.contarAmigos("A05") == 0);
        verificar("P16 Eliminar amistad inexistente devuelve false", !red.eliminarAmistad("A04", "A05"));

        // dejarDeSeguir
        RedSocial r2 = redInicial();
        r2.seguir("A03", "A05");
        r2.agregarAmistad("A03", "A05");
        verificar("P17 dejarDeSeguir quita solo SEGUIR",
                r2.dejarDeSeguir("A03", "A05") && r2.contarAmigos("A03") == 3
                && "".equals(r2.listarSeguidos("A03")));
        verificar("P18 dejarDeSeguir inexistente devuelve false", !r2.dejarDeSeguir("A03", "A05"));

        // Paso 6: eliminar persona
        verificar("P19 eliminarPersona(A01) devuelve true", red.eliminarPersona("A01"));
        verificar("P20 Sofía ya no existe", !red.existePersona("A01") && red.listarAmigos("A01") == null);
        verificar("P21 Mateo ya no la tiene como amiga", "Valeria".equals(red.listarAmigos("A02")));
        verificar("P22 Seguimientos hacia Sofía eliminados",
                "".equals(red.listarSeguidos("A02")) && "".equals(red.listarSeguidos("A06")));
        verificar("P23 Conteos tras eliminar: 5 usuarios, 2 amistades, 4 aristas",
                red.contarPersonas() == 5 && red.contarAmistades() == 2 && red.contarAristas() == 4);

        // Pasos 7 y 8: validaciones
        verificar("P24 Usuario duplicado rechazado", !red.registrarPersona("A02", "Otro nombre"));
        verificar("P25 Duplicado sin importar mayúsculas", !red.registrarPersona("a02", "Otro"));
        verificar("P26 Lazo rechazado", !red.agregarAmistad("A03", "A03"));
        verificar("P27 Usuario vacío rechazado", !red.registrarPersona("   ", "Ana"));
        verificar("P28 Usuario con espacio rechazado", !red.registrarPersona("A 07", "Ana"));
        verificar("P29 Usuario con coma rechazado", !red.registrarPersona("A,07", "Ana"));
        verificar("P30 Amistad duplicada rechazada", !red.agregarAmistad("A02", "A03"));
        verificar("P31 Relación con usuario inexistente rechazada",
                !red.agregarAmistad("A02", "Z99") && !red.seguir("Z99", "A02"));
        verificar("P32 Consultas de usuario inexistente: null y -1",
                red.listarSeguidores("Z99") == null && red.contarAmigos("Z99") == -1);
        verificar("P33 Eliminar persona inexistente devuelve false", !red.eliminarPersona("Z99"));

        // Eliminar la cabeza y el último vértice
        RedSocial r3 = redInicial();
        verificar("P34 Eliminar último vértice (A06)", r3.eliminarPersona("A06") && r3.contarPersonas() == 5);
        verificar("P35 Registrar nuevamente tras eliminar", r3.registrarPersona("A06", "Diego"));

        // Casos adicionales
        RedSocial vacia = new RedSocial();
        verificar("P36 Red vacía: 0 usuarios y mostrarRed sin errores",
                vacia.contarPersonas() == 0 && vacia.mostrarRed().contains("red vacía"));
        RedSocial r4 = redInicial();
        r4.seguir("A02", "A01");
        verificar("P37 AMISTAD y SEGUIR conviven entre las mismas personas",
                r4.contarAmigos("A02") == 2 && "Sofía".equals(r4.listarSeguidos("A02")));
        r4.eliminarAmistad("A01", "A02");
        verificar("P38 Eliminar la amistad no borra el seguimiento",
                "Sofía".equals(r4.listarSeguidos("A02")) && r4.contarAmigos("A02") == 1);
        verificar("P39 Eliminar la cabeza (A01) deja a A02 como primer vértice",
                r4.eliminarPersona("A01") && r4.mostrarRed().split("\n")[1].contains("A02"));
        verificar("P40 Conteo de seguimientos tras eliminar al seguido", r4.contarSeguimientos() == 0);

        System.out.println("\nResultado: " + pasadas + " pasadas, " + fallidas + " fallidas, "
                + (pasadas + fallidas) + " en total.");
        if (fallidas > 0) System.exit(1);
    }
}
