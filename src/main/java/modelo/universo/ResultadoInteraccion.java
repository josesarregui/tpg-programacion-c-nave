package modelo.universo;

/**
 * Representa el balance y consecuencias de una interacción de la nave
 * con un elemento del universo en el tablero.
 */
public class ResultadoInteraccion {
    private final String mensaje;
    private final boolean naveDestruida;
    private final boolean misionFallida;
    private final int combustibleModificado;
    private final int energiaModificada;
    private final int desgasteModificado;

    public ResultadoInteraccion(String mensaje, boolean naveDestruida, boolean misionFallida, int combustibleModificado,
            int energiaModificada, int desgasteModificado) {
        this.mensaje = mensaje; /// Mensaje del evento
        this.naveDestruida = naveDestruida; /// Indica si la nave fue destruida
        this.misionFallida = misionFallida; /// Indica si la mision fallo
        this.combustibleModificado = combustibleModificado; /// Combustible modificado
        this.energiaModificada = energiaModificada; /// Energia modificada
        this.desgasteModificado = desgasteModificado; /// Desgaste modificado
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isNaveDestruida() {
        return naveDestruida;
    }

    public boolean isMisionFallida() {
        return misionFallida;
    }

    public int getCombustibleModificado() {
        return combustibleModificado;
    }

    public int getEnergiaModificada() {
        return energiaModificada;
    }

    public int getDesgasteModificado() {
        return desgasteModificado;
    }

    @Override
    public String toString() {
        return mensaje;
    }
}
