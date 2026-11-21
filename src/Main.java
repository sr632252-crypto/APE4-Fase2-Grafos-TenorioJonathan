import java.util.Scanner;

/*
 * APE 4 - Fase 2: Conexion Ambato
 * Ejecuta los pasos de la guia (Tabla 3). Con "menu" abre el menu.
 *   javac -encoding UTF-8 -d out src/*.java
 *   java -cp out Main        o   java -cp out Main menu
 * Autor: Jonathan Tenorio
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("menu")) {
            menu();
        } else {
            secuenciaGuia();
        }
    }

    // red inicial de la Fase 1: seis usuarios y cinco amistades
    static RedSocial cargarRedInicial(boolean imprimir) {
        RedSocial red = new RedSocial();
        String[][] personas = {
            {"A01", "Sofía"}, {"A02", "Mateo"}, {"A03", "Valeria"},
            {"A04", "Andrés"}, {"A05", "Camila"}, {"A06", "Diego"}
        };
        for (String[] p : personas) {
            boolean ok = red.registrarPersona(p[0], p[1]);
            if (imprimir) System.out.printf("registrarPersona(%s, %s) -> %b%n", p[0], p[1], ok);
        }
        String[][] amistades = {
            {"A01", "A02"}, {"A02", "A03"}, {"A03", "A04"}, {"A04", "A01"}, {"A04", "A05"}
        };
        for (String[] a : amistades) {
            boolean ok = red.agregarAmistad(a[0], a[1]);
            if (imprimir) System.out.printf("agregarAmistad(%s, %s) -> %b%n", a[0], a[1], ok);
        }
        return red;
    }

    static void titulo(String texto) {
        System.out.println();
        System.out.println("==============================================================");
        System.out.println(" " + texto);
        System.out.println("==============================================================");
    }

    static String mostrar(String lista) {
        if (lista == null) return "null (usuario inexistente)";
        return lista.isEmpty() ? "(ninguno)" : lista;
    }

    static void secuenciaGuia() {
        titulo("PASO 1. Registrar A01-A06 y las cinco amistades");
        RedSocial red = cargarRedInicial(true);
        System.out.println();
        System.out.print(red.mostrarRed());
        System.out.println("Usuarios: " + red.contarPersonas()
                + " | Amistades: " + red.contarAmistades()
                + " | Aristas internas: " + red.contarAristas());
        System.out.print("Grados (A01..A06): ");
        String[] ids = {"A01", "A02", "A03", "A04", "A05", "A06"};
        for (int i = 0; i < ids.length; i++) {
            System.out.print(red.contarAmigos(ids[i]) + (i < ids.length - 1 ? ", " : "\n"));
        }

        titulo("PASOS 2 y 3. Relaciones dirigidas SEGUIR");
        System.out.println("seguir(A02, A01) -> " + red.seguir("A02", "A01"));
        System.out.println("seguir(A06, A01) -> " + red.seguir("A06", "A01"));
        System.out.println("seguir(A02, A01) otra vez -> " + red.seguir("A02", "A01") + "  (duplicado)");
        System.out.println("Seguidores de Sofía (A01): " + mostrar(red.listarSeguidores("A01")));
        System.out.println("Seguidos por Diego (A06):  " + mostrar(red.listarSeguidos("A06")));
        System.out.println("Amigos de Diego (A06):     " + mostrar(red.listarAmigos("A06")));
        System.out.println();
        System.out.print(red.mostrarRed());

        titulo("PASO 4. Consultas sobre Andrés (A04)");
        System.out.println("listarAmigos(A04)      -> " + mostrar(red.listarAmigos("A04")));
        System.out.println("contarAmigos(A04)      -> " + red.contarAmigos("A04"));
        System.out.println("listarSeguidos(A02)    -> " + mostrar(red.listarSeguidos("A02")));
        System.out.println("listarSeguidores(A01)  -> " + mostrar(red.listarSeguidores("A01")));
        System.out.println("listarAmigos(a01)      -> " + mostrar(red.listarAmigos("a01")) + "  (minúsculas)");
        System.out.println("listarAmigos(Z99)      -> " + mostrar(red.listarAmigos("Z99")));
        System.out.println("contarAmigos(Z99)      -> " + red.contarAmigos("Z99"));

        titulo("PASO 5. eliminarAmistad(A04, A05)");
        System.out.println("eliminarAmistad(A04, A05) -> " + red.eliminarAmistad("A04", "A05"));
        System.out.println("Amigos de Andrés (A04): " + mostrar(red.listarAmigos("A04")));
        System.out.println("Amigos de Camila (A05): " + mostrar(red.listarAmigos("A05")));
        System.out.println("eliminarAmistad(A04, A05) otra vez -> " + red.eliminarAmistad("A04", "A05"));

        titulo("PASO 6. eliminarPersona(A01)");
        System.out.println("eliminarPersona(A01) -> " + red.eliminarPersona("A01"));
        System.out.println("Amigos de Mateo (A02):    " + mostrar(red.listarAmigos("A02")));
        System.out.println("Amigos de Andrés (A04):   " + mostrar(red.listarAmigos("A04")));
        System.out.println("Seguidos por Mateo (A02): " + mostrar(red.listarSeguidos("A02")));
        System.out.println("Seguidos por Diego (A06): " + mostrar(red.listarSeguidos("A06")));
        System.out.println("listarAmigos(A01)        -> " + mostrar(red.listarAmigos("A01")));
        System.out.println();
        System.out.print(red.mostrarRed());

        titulo("PASOS 7 y 8. Validaciones");
        System.out.println("registrarPersona(A02, Martín)   -> " + red.registrarPersona("A02", "Martín") + "  (duplicado)");
        System.out.println("agregarAmistad(A03, A03)        -> " + red.agregarAmistad("A03", "A03") + "  (lazo)");
        System.out.println("registrarPersona(\"\", Ana)       -> " + red.registrarPersona("", "Ana") + "  (vacío)");
        System.out.println("registrarPersona(A 07, Ana)     -> " + red.registrarPersona("A 07", "Ana") + "  (espacio)");
        System.out.println("registrarPersona(A,07, Ana)     -> " + red.registrarPersona("A,07", "Ana") + "  (coma)");
        System.out.println("agregarAmistad(A03, A02)        -> " + red.agregarAmistad("A03", "A02") + "  (ya son amigos)");
        System.out.println("seguir(A05, Z99)                -> " + red.seguir("A05", "Z99") + "  (no existe)");
        System.out.println("dejarDeSeguir(A06, A04)         -> " + red.dejarDeSeguir("A06", "A04") + "  (no lo sigue)");
        System.out.println();
        System.out.println("Estado final: " + red.contarPersonas() + " usuarios, "
                + red.contarAmistades() + " amistades, "
                + red.contarSeguimientos() + " seguimientos, "
                + red.contarAristas() + " aristas internas.");
    }

    // menú interactivo opcional
    static void menu() {
        RedSocial red = cargarRedInicial(false);
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- CONEXIÓN AMBATO ---");
            System.out.println(" 1. Registrar persona        7. Listar amigos");
            System.out.println(" 2. Eliminar persona         8. Listar seguidos");
            System.out.println(" 3. Agregar amistad          9. Listar seguidores");
            System.out.println(" 4. Seguir                  10. Contar amigos");
            System.out.println(" 5. Eliminar amistad        11. Mostrar red");
            System.out.println(" 6. Dejar de seguir          0. Salir");
            System.out.print("Opción: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "0": System.out.println("Hasta luego."); return;
                case "1": System.out.println(red.registrarPersona(leer(sc, "Usuario"), leer(sc, "Nombre")) ? "Registrado." : "No se pudo registrar."); break;
                case "2": System.out.println(red.eliminarPersona(leer(sc, "Usuario")) ? "Eliminado." : "No existe."); break;
                case "3": System.out.println(red.agregarAmistad(leer(sc, "Usuario A"), leer(sc, "Usuario B")) ? "Amistad creada." : "No se pudo crear."); break;
                case "4": System.out.println(red.seguir(leer(sc, "Quién sigue"), leer(sc, "A quién")) ? "Ahora lo sigue." : "No se pudo seguir."); break;
                case "5": System.out.println(red.eliminarAmistad(leer(sc, "Usuario A"), leer(sc, "Usuario B")) ? "Amistad eliminada." : "No eran amigos."); break;
                case "6": System.out.println(red.dejarDeSeguir(leer(sc, "Quién sigue"), leer(sc, "A quién")) ? "Dejó de seguir." : "No lo seguía."); break;
                case "7": System.out.println(mostrar(red.listarAmigos(leer(sc, "Usuario")))); break;
                case "8": System.out.println(mostrar(red.listarSeguidos(leer(sc, "Usuario")))); break;
                case "9": System.out.println(mostrar(red.listarSeguidores(leer(sc, "Usuario")))); break;
                case "10": System.out.println("Amigos: " + red.contarAmigos(leer(sc, "Usuario"))); break;
                case "11": System.out.print(red.mostrarRed()); break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    static String leer(Scanner sc, String etiqueta) {
        System.out.print(etiqueta + ": ");
        return sc.nextLine();
    }
}
