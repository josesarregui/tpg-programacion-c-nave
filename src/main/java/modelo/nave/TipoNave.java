package modelo.nave;

/**
 * Tipos de nave admitidos (E1-01). El cliente los usa para pedirle a {@link NaveFactory}
 * la nave que necesita, sin conocer las clases concretas (E1-07).
 * Al ser un enum, un tipo inexistente no compila: no hace falta validar textos.
 */
public enum TipoNave {
    EXPLORADORA("Exploradora"),
    CARGUERO("Carguero"),
    COMBATE("Combate");

    private final String descripcion;

    TipoNave(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
