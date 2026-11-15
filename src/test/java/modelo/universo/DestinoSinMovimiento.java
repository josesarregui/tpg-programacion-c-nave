package modelo.universo;

/**
 * Doble de prueba de {@link Destino}: su turno no mueve ningún elemento, para que el recorrido de una misión sea
 * determinista. Igual notifica el fin del turno, como el original.
 */
public class DestinoSinMovimiento extends Destino {

    private final Tablero tablero;
    private int turnos;

    public DestinoSinMovimiento(Tablero tablero) {
        super(tablero);
        this.tablero = tablero;
    }

    @Override
    public void turnoDestino() {
        turnos++;
        tablero.notificarActualizacion("Finalizó el turno de Destino: se movieron 0 de 0 elementos seleccionados.");
    }

    /**
     * @return cantidad de turnos de Destino que ejecutó la misión.
     */
    public int getTurnos() {
        return turnos;
    }
}
