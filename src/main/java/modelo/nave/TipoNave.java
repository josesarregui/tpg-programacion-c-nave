package modelo.nave;

/**
 * Tipos de nave admitidos (E1-01). El cliente los usa para pedirle a {@link NaveFactory}
 * la nave que necesita, sin conocer las clases concretas (E1-07).
 * Al ser un enum, un tipo inexistente no compila: no hace falta validar textos.
 *
 * Cada tipo guarda también el símbolo con el que la vista dibuja la nave en el tablero (E2), de modo que
 * {@link Nave#getSimbolo()} no necesita condicionales de tipo.
 */
public enum TipoNave {
    EXPLORADORA("Exploradora", "EX"),
    CARGUERO("Carguero", "CA"),
    COMBATE("Combate", "CO");

    private final String descripcion;
    private final String simbolo;

    TipoNave(String descripcion, String simbolo) {
        this.descripcion = descripcion;
        this.simbolo = simbolo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @post El valor retornado no es nulo ni vacío y es distinto para cada tipo.
     */
    public String getSimbolo() {
        return simbolo;
    }
}
