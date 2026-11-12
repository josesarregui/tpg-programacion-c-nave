package modelo.universo;

/**
 * Resultado de un enfrentamiento espacial entre dos naves.
 */
public enum ResultadoCombate {
    GANA("Victoria en combate"),
    PIERDE("Derrota en combate"),
    PACTO_DE_PAZ("Pacto de paz acordado");

    private final String descripcion;

    ResultadoCombate(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

