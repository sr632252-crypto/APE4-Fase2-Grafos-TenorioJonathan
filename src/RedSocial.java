/*
 * APE 4 - Fase 2: Conexion Ambato
 * Red social con lista de adyacencia (multilista) usando nodos propios.
 * AMISTAD va en los dos sentidos, SEGUIR solo de quien sigue al seguido.
 * Autor: Jonathan Tenorio
 */
public class RedSocial {

    // tipos de relacion
    public enum TipoRelacion { AMISTAD, SEGUIR }

    // datos de la persona
    static class Persona {
        String usuario; // en minusculas
        String nombre;

        Persona(String usuario, String nombre) {
            this.usuario = usuario;
            this.nombre = nombre;
        }
    }

    // nodo de la lista principal (una persona)
    static class Vertice {
        Persona persona;
        Arista aristas; // lista de relaciones
        Vertice sig;

        Vertice(Persona persona) {
            this.persona = persona;
        }
    }

    // nodo de la lista de relaciones
    static class Arista {
        Vertice destino;
        TipoRelacion tipo;
        Arista sig;

        Arista(Vertice destino, TipoRelacion tipo) {
            this.destino = destino;
            this.tipo = tipo;
        }
    }

    private Vertice cabeza;
    private int numPersonas;  // V
    private int numAristas;   // A (cada amistad cuenta 2)

    // ===== Auxiliares privados =====

    // quita espacios y pasa a minusculas
    private static String normalizar(String usuario) {
        return usuario == null ? null : usuario.trim().toLowerCase();
    }

    // busca la persona por usuario, O(V)
    private Vertice buscar(String usuario) {
        String clave = normalizar(usuario);
        if (clave == null || clave.isEmpty()) return null;
        for (Vertice v = cabeza; v != null; v = v.sig) {
            if (v.persona.usuario.equals(clave)) return v;
        }
        return null;
    }

    // true si ya hay una arista origen -> destino de ese tipo
    private boolean existe(Vertice origen, Vertice destino, TipoRelacion tipo) {
        for (Arista a = origen.aristas; a != null; a = a.sig) {
            if (a.destino == destino && a.tipo == tipo) return true;
        }
        return false;
    }

    // inserta al final para que quede en el orden en que se agrego
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

    // quita una arista de ese tipo
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

    // quita todas las aristas que van hacia destino
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

    // el usuario no puede ser vacio ni tener espacios o comas
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

    // ===== Operaciones sobre personas =====

    public boolean registrarPersona(String usuario, String nombre) {
        if (!usuarioValido(usuario) || nombre == null || nombre.trim().isEmpty()) return false;
        String clave = normalizar(usuario);
        Vertice ultimo = null;
        for (Vertice v = cabeza; v != null; v = v.sig) {
            if (v.persona.usuario.equals(clave)) return false; // ya existe
            ultimo = v;
        }
        Vertice nuevo = new Vertice(new Persona(clave, nombre.trim()));
        if (ultimo == null) cabeza = nuevo;
        else ultimo.sig = nuevo;
        numPersonas++;
        return true;
    }

    // primero se borran las aristas que llegan a la persona, O(V + A)
    public boolean eliminarPersona(String usuario) {
        Vertice objetivo = buscar(usuario);
        if (objetivo == null) return false;

        for (Vertice w = cabeza; w != null; w = w.sig) {
            if (w != objetivo) quitarHacia(w, objetivo);
        }
        // sus propias aristas
        for (Arista a = objetivo.aristas; a != null; a = a.sig) numAristas--;
        objetivo.aristas = null;

        // sacarlo de la lista principal
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

    // ===== Operaciones sobre relaciones =====

    // amistad: dos aristas
    public boolean agregarAmistad(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb
                || existe(va, vb, TipoRelacion.AMISTAD)) return false;
        insertar(va, vb, TipoRelacion.AMISTAD);
        insertar(vb, va, TipoRelacion.AMISTAD);
        return true;
    }

    // seguir: una sola arista a -> b
    public boolean seguir(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb
                || existe(va, vb, TipoRelacion.SEGUIR)) return false;
        insertar(va, vb, TipoRelacion.SEGUIR);
        return true;
    }

    public boolean eliminarAmistad(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb) return false;
        if (!quitar(va, vb, TipoRelacion.AMISTAD)) return false;
        quitar(vb, va, TipoRelacion.AMISTAD);
        return true;
    }

    public boolean dejarDeSeguir(String a, String b) {
        Vertice va = buscar(a), vb = buscar(b);
        if (va == null || vb == null || va == vb) return false;
        return quitar(va, vb, TipoRelacion.SEGUIR);
    }

    // ===== Consultas =====

    // arma el texto con los nombres separados por coma
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

    // null si no existe el usuario
    public String listarAmigos(String usuario) {
        Vertice v = buscar(usuario);
        return v == null ? null : listarSalientes(v, TipoRelacion.AMISTAD);
    }

    public String listarSeguidos(String usuario) {
        Vertice v = buscar(usuario);
        return v == null ? null : listarSalientes(v, TipoRelacion.SEGUIR);
    }

    // hay que revisar toda la red, O(V + A)
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

    // -1 si no existe
    public int contarAmigos(String usuario) {
        Vertice v = buscar(usuario);
        if (v == null) return -1;
        int total = 0;
        for (Arista a = v.aristas; a != null; a = a.sig) {
            if (a.tipo == TipoRelacion.AMISTAD) total++;
        }
        return total;
    }

    public boolean existePersona(String usuario) {
        return buscar(usuario) != null;
    }

    public int contarPersonas() { return numPersonas; }

    public int contarAristas() { return numAristas; }

    public int contarAmistades() {
        int aristasAmistad = 0;
        for (Vertice v = cabeza; v != null; v = v.sig) {
            for (Arista a = v.aristas; a != null; a = a.sig) {
                if (a.tipo == TipoRelacion.AMISTAD) aristasAmistad++;
            }
        }
        return aristasAmistad / 2;
    }

    public int contarSeguimientos() {
        int total = 0;
        for (Vertice v = cabeza; v != null; v = v.sig) {
            for (Arista a = v.aristas; a != null; a = a.sig) {
                if (a.tipo == TipoRelacion.SEGUIR) total++;
            }
        }
        return total;
    }

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
