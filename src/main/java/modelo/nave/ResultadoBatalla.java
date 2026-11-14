package modelo.nave;

/**
 * Resultado de una batalla entre la nave propia y una nave enemiga (Enunciado E2, punto A), siempre desde el
 * punto de vista de la nave propia. Lo devuelve el Double Dispatch de {@link Nave#enfrentarseCon(Nave)}.
 *
 * Cada constante sabe si destruye a la nave propia: así quien aplica el resultado no necesita condicionales
 * sobre la constante (Enunciado E2: si la nave del jugador pierde, es destruida junto con su tripulación).
 */
public enum ResultadoBatalla {
    GANA("Victoria en batalla", false),
    PIERDE("Derrota en batalla", true),
    PACTO_DE_PAZ("Pacto de paz acordado", false);

    private final String descripcion;
    private final boolean destruyeNavePropia;

    ResultadoBatalla(String descripcion, boolean destruyeNavePropia) {
        this.descripcion = descripcion;
        this.destruyeNavePropia = destruyeNavePropia;
    }

    /**
     * @post El valor retornado no es nulo ni vacío.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @post Retorna true sólo para PIERDE.
     */
    public boolean destruyeNavePropia() {
        return destruyeNavePropia;
    }
}
