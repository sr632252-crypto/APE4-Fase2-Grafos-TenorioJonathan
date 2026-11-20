/**
 * APE 4 - Fase 2: Conexión Ambato
 * Red social dinámica representada con una multilista (lista de adyacencia
 * construida con nodos propios). No se usan HashMap, HashSet, ArrayList ni
 * LinkedList.
 *
 * - Cada persona es un Vertice enlazado en la lista principal.
 * - Cada Vertice tiene su propia lista de Arista.
 * - AMISTAD se guarda en ambos sentidos (dos aristas).
 * - SEGUIR se guarda solo desde quien sigue hacia el seguido (una arista).
 *
 * @author Jonathan Tenorio
 */
public class RedSocial {

    /** Tipos de relación permitidos entre dos personas. */
    public enum TipoRelacion { AMISTAD, SEGUIR }

    /** Datos de una persona de la red. */
    static class Persona {
        String usuario; // identificador normalizado (minúsculas)
        String nombre;

        Persona(String usuario, String nombre) {
            this.usuario = usuario;
            this.nombre = nombre;
        }
    }

    /** Nodo de la lista principal: una persona y su lista de aristas. */
    static class Vertice {
        Persona persona;
        Arista aristas; // cabeza de la lista de adyacencia
        Vertice sig;    // siguiente persona de la red

        Vertice(Persona persona) {
            this.persona = persona;
        }
    }

    /** Nodo de la lista de adyacencia: una relación dirigida. */
    static class Arista {
        Vertice destino;
        TipoRelacion tipo;
        Arista sig;

        Arista(Vertice destino, TipoRelacion tipo) {
            this.destino = destino;
            this.tipo = tipo;
        }
    }

    private Vertice cabeza;   // primera persona de la red
    private int numPersonas;  // V
    private int numAristas;   // A (cada amistad aporta 2)

    // ------------------------------------------------------------------
    // Auxiliares privados
    // ------------------------------------------------------------------

    /** Normaliza un identificador: sin espacios externos y en minúsculas. */
    private static String normalizar(String usuario) {
        return usuario == null ? null : usuario.trim().toLowerCase();
    }

    /** Busca un vértice por su identificador normalizado. O(V) */
    private Vertice buscar(String usuario) {
        String clave = normalizar(usuario);
        if (clave == null || clave.isEmpty()) return null;
        for (Vertice v = cabeza; v != null; v = v.sig) {
            if (v.persona.usuario.equals(clave)) return v;
        }
        return null;
    }

    /** Indica si existe una arista origen -> destino del tipo dado. O(grado) */
    private boolean existe(Vertice origen, Vertice destino, TipoRelacion tipo) {
        for (Arista a = origen.aristas; a != null; a = a.sig) {
            if (a.destino == destino && a.tipo == tipo) return true;
        }
        return false;
    }

    /**
     * Inserta una arista al final de la lista de origen para conservar el
     * orden en que se registraron las relaciones. O(grado)
     */
    private void insertar(Vertice origen, Vertice destino, TipoRelacion tipo) {
        Arista nueva = new Arista(destino, tipo);
        if (origen.aristas == null) {
            origen.aristas = nueva;
        } else {
            Arista ultima = origen.aristas;
            while (ultima.sig != null) ultima = ultima.sig;
            ultima.sig = nueva;
        }
        numAristas++;
    }

    /** Quita la arista origen -> destino del tipo dado. O(grado) */
    private boolean quitar(Vertice origen, Vertice destino, TipoRelacion tipo) {
        Arista anterior = null;
        for (Arista a = origen.aristas; a != null; anterior = a, a = a.sig) {
            if (a.destino == destino && a.tipo == tipo) {
                if (anterior == null) origen.aristas = a.sig;
                else anterior.sig = a.sig;
                numAristas--;
                return true;
            }
        }
        return false;
    }

    /** Retira de la lista de origen toda arista (de cualquier tipo) hacia destino. */
    private void quitarHacia(Vertice origen, Vertice destino) {
        while (origen.aristas != null && origen.aristas.destino == destino) {
            origen.aristas = origen.aristas.sig;
            numAristas--;
        }
        Arista actual = origen.aristas;
        while (actual != null && actual.sig != null) {
            if (actual.sig.destino == destino) {
                actual.sig = actual.sig.sig;
                numAristas--;
            } else {
                actual = actual.sig;
            }
        }
    }

    /** Valida el formato del identificador: no vacío, sin espacios ni comas. */
    private static boolean usuarioValido(String usuario) {
        if (usuario == null) return false;
        String u = usuario.trim();
        if (u.isEmpty()) return false;
        for (int i = 0; i < u.length(); i++) {
            char c = u.charAt(i);
            if (Character.isWhitespace(c) || c == ',') return false;
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Operaciones sobre personas
    // ------------------------------------------------------------------

    /** Registra una persona. Rechaza usuario vacío, repetido, con espacios o comas. */
    public boolean registrarPersona(String usuario, String nombre) {
        if (!usuarioValido(usuario) || nombre == null || nombre.trim().isEmpty()) return false;
        String clave = normalizar(usuario);
        Vertice ultimo = null;
        for (Vertice v = cabeza; v != null; v = v.sig) {
            if (v.persona.usuario.equals(clave)) return false; // duplicado
            ultimo = v;
        }
        Vertice nuevo = new Vertice(new Persona(clave, nombre.trim()));
        if (ultimo == null) cabeza = nuevo;
        else ultimo.sig = nuevo;
        numPersonas++;
        return true;
    }

    /** Elimina el vértice y todas las aristas que llegan a él. O(V + A) */
    public boolean eliminarPersona(String usuario) {
        Vertice objetivo = buscar(usuario);
        if (objetivo == null) return false;

        // 1. Limpiar referencias entrantes en toda la red.
        for (Vertice w = cabeza; w != null; w = w.sig) {
            if (w != objetivo) quitarHacia(w, objetivo);
        }
        // 2. Descontar las aristas salientes del propio objetivo.
        for (Arista a = objetivo.aristas; a != null; a = a.sig) numAristas--;
        objetivo.aristas = null;

        // 3. Desenlazar el vértice de la lista principal.
        Vertice anterior = null, actual = cabeza;
        while (actual != objetivo) {
            anterior = actual;
            actual = actual.sig;
        }
        if (anterior == null) cabeza = objetivo.sig;
        else anterior.sig = objetivo.sig;
        numPersonas--;
        return true;
    }

    // ------------------------------------------------------------------
    // Operaciones sobre relaciones
    // ------------------------------------------------------------------

    /** La amistad es recíproca: se crean dos aristas AMISTAD. */
    public boolean agregarAmistad(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb
                || existe(va, vb, TipoRelacion.AMISTAD)) return false;
        insertar(va, vb, TipoRelacion.AMISTAD);
        insertar(vb, va, TipoRelacion.AMISTAD);
        return true;
    }

    /** Seguir es dirigido: se crea una sola arista SEGUIR (a -> b). */
    public boolean seguir(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb
                || existe(va, vb, TipoRelacion.SEGUIR)) return false;
        insertar(va, vb, TipoRelacion.SEGUIR);
        return true;
    }

    /** Quita las dos aristas AMISTAD entre a y b. */
    public boolean eliminarAmistad(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb) return false;
        if (!quitar(va, vb, TipoRelacion.AMISTAD)) return false;
        quitar(vb, va, TipoRelacion.AMISTAD);
        return true;
    }

    /** Quita solo la arista SEGUIR a -> b. */
    public boolean dejarDeSeguir(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb) return false;
        return quitar(va, vb, TipoRelacion.SEGUIR);
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /** Nombres de los destinos de las aristas del tipo dado, separados por coma. */
    private String listarSalientes(Vertice v, TipoRelacion tipo) {
        StringBuilder sb = new StringBuilder();
        for (Arista a = v.aristas; a != null; a = a.sig) {
            if (a.tipo == tipo) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(a.destino.persona.nombre);
            }
        }
        return sb.toString();
    }

    /** Amigos del usuario; null si no existe, "" si no tiene amigos. O(V + grado) */
    public String listarAmigos(String usuario) {
        Vertice v = buscar(usuario);
        return v == null ? null : listarSalientes(v, TipoRelacion.AMISTAD);
    }

    /** Personas a las que sigue el usuario. O(V + grado) */
    public String listarSeguidos(String usuario) {
        Vertice v = buscar(usuario);
        return v == null ? null : listarSalientes(v, TipoRelacion.SEGUIR);
    }

    /** Personas que siguen al usuario: se revisa toda la red. O(V + A) */
    public String listarSeguidores(String usuario) {
        Vertice objetivo = buscar(usuario);
        if (objetivo == null) return null;
        StringBuilder sb = new StringBuilder();
        for (Vertice w = cabeza; w != null; w = w.sig) {
            if (w != objetivo && existe(w, objetivo, TipoRelacion.SEGUIR)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(w.persona.nombre);
            }
        }
        return sb.toString();
    }

    /** Número de amigos; -1 si el usuario no existe. O(V + grado) */
    public int contarAmigos(String usuario) {
        Vertice v = buscar(usuario);
        if (v == null) return -1;
        int total = 0;
        for (Arista a = v.aristas; a != null; a = a.sig) {
            if (a.tipo == TipoRelacion.AMISTAD) total++;
        }
        return total;
    }

    /** Indica si existe el usuario. */
    public boolean existePersona(String usuario) {
        return buscar(usuario) != null;
    }

    public int contarPersonas() { return numPersonas; }

    public int contarAristas() { return numAristas; }

    /** Número de amistades (cada amistad son dos aristas internas). O(V + A) */
    public int contarAmistades() {
        int aristasAmistad = 0;
        for (Vertice v = cabeza; v != null; v = v.sig) {
            for (Arista a = v.aristas; a != null; a = a.sig) {
                if (a.tipo == TipoRelacion.AMISTAD) aristasAmistad++;
            }
        }
        return aristasAmistad / 2;
    }

    /** Número de relaciones SEGUIR. O(V + A) */
    public int contarSeguimientos() {
        int total = 0;
        for (Vertice v = cabeza; v != null; v = v.sig) {
            for (Arista a = v.aristas; a != null; a = a.sig) {
                if (a.tipo == TipoRelacion.SEGUIR) total++;
            }
        }
        return total;
    }

    /** Representación textual de la multilista completa. O(V + A) */
    public String mostrarRed() {
        StringBuilder sb = new StringBuilder();
        sb.append("Red Conexión Ambato  (V = ").append(numPersonas)
          .append(", A = ").append(numAristas).append(")\n");
        if (cabeza == null) return sb.append("  (red vacía)\n").toString();
        for (Vertice v = cabeza; v != null; v = v.sig) {
            sb.append(String.format("  [%s | %-8s]", v.persona.usuario.toUpperCase(), v.persona.nombre));
            if (v.aristas == null) sb.append(" -> null");
            for (Arista a = v.aristas; a != null; a = a.sig) {
                sb.append(" -> ")
                  .append(a.tipo == TipoRelacion.AMISTAD ? "AMISTAD:" : "SEGUIR:")
                  .append(a.destino.persona.nombre);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
