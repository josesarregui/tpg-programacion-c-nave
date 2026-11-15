package modelo.universo;

/**
 * Doble de prueba de {@link Destino}: en un turno dado traslada un elemento a una casilla elegida por la prueba
 * (por ejemplo, para cruzar un asteroide en la ruta de la nave); en los demás turnos no mueve nada.
 */
public class DestinoProgramado extends Destino {

    private final TableroDePrueba tablero;
    private final int turnoDelMovimiento;
    private final Posicion desde;
    private final Posicion hacia;
    private int turno;

    /**
     * @pre En el turno indicado, desde está ocupada y hacia está libre, dentro del tablero y no es el objetivo.
     */
    public DestinoProgramado(TableroDePrueba tablero, int turnoDelMovimiento, Posicion desde, Posicion hacia) {
        super(tablero);
        this.tablero = tablero;
        this.turnoDelMovimiento = turnoDelMovimiento;
        this.desde = desde;
        this.hacia = hacia;
    }

    @Override
    public void turnoDestino() {
        turno++;
        if (turno == turnoDelMovimiento) {
            tablero.trasladar(desde, hacia);
        }
        tablero.notificarActualizacion("Finalizó el turno de Destino.");
    }
}
