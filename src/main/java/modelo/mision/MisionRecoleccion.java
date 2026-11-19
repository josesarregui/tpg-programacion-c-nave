package modelo.mision;

import modelo.universo.Destino;
import modelo.universo.Posicion;
import modelo.universo.Tablero;

import java.util.Random;

/**
 * M-02 — Recolección (Enunciado E2): llegar hasta la coordenada donde se encuentran riquezas espaciales
 * ({@link Tablero#obtenerObjetivo()}) y, al arribar, recolectar gemas.
 *
 * <ul>
 *   <li>Gemas: la cantidad es variable (ni la Guía 4.2 ni la Tabla de Recursos fijan montos). Se sortea al llegar,
 *       entre {@link #GEMAS_MINIMAS} y {@link #GEMAS_MAXIMAS}, con un generador inyectable para que las pruebas
 *       sean deterministas. Se informan en la evaluación y se acreditan a la nave en el cierre (Fase 4).</li>
 *   <li>Costo de la acción final: {@link #ENERGIA_ADICIONAL} de energía (Tabla de Recursos, IV.4).</li>
 * </ul>
 *
 * Sólo redefine sus pasos particulares; el ciclo lo fija {@link Mision#realizar()} (Template Method).
 *
 * Invariante: gemasObtenidas == 0 o GEMAS_MINIMAS <= gemasObtenidas <= GEMAS_MAXIMAS.
 */
public class MisionRecoleccion extends Mision {

    public static final int ENERGIA_ADICIONAL = 5;
    /** Mínimo de gemas que se recolectan al llegar. */
    public static final int GEMAS_MINIMAS = 60;
    /** Máximo de gemas que se recolectan al llegar. */
    public static final int GEMAS_MAXIMAS = 100;

    private final Random azar;
    private int gemasObtenidas;

    /**
     * @pre tablero y destino no nulos, y destino creado sobre ese tablero.
     * @post La misión queda creada, sin gemas obtenidas.
     */
    public MisionRecoleccion(Tablero tablero, Destino destino) {
        this(tablero, destino, new Random());
    }

    /**
     * @pre tablero y destino no nulos, y destino creado sobre ese tablero; azar != null.
     * @post La misión queda creada, sin gemas obtenidas; las gemas se sortearán con azar al llegar.
     */
    public MisionRecoleccion(Tablero tablero, Destino destino, Random azar) {
        super("M-02", "Recolección", tablero, destino);
        assert azar != null : "El generador de gemas no puede ser nulo.";
        this.azar = azar;
        this.gemasObtenidas = 0;
    }

    /**
     * @post El objetivo es el que eligió el tablero (casilla libre, a distancia mayor o igual a 2 de la nave).
     */
    @Override
    protected Posicion fijarObjetivo(Tablero tablero) {
        return tablero.obtenerObjetivo();
    }

    @Override
    protected int getEnergiaAdicional() {
        return ENERGIA_ADICIONAL;
    }

    /**
     * Recolecta las gemas del punto de interés.
     *
     * @post GEMAS_MINIMAS <= getGemasObtenidas() <= GEMAS_MAXIMAS y quedó registrado entre las acciones.
     */
    @Override
    protected void realizarAccion() {
        gemasObtenidas = GEMAS_MINIMAS + azar.nextInt(GEMAS_MAXIMAS - GEMAS_MINIMAS + 1);
        registrarAccion("Ejecución: llegó al punto de interés y recolectó " + gemasObtenidas
                + " gemas (se acreditan en el cierre).");
        assert gemasObtenidas >= GEMAS_MINIMAS && gemasObtenidas <= GEMAS_MAXIMAS
                : "Fallo postcondición: gemas fuera del rango.";
    }

    /**
     * @return gemas recolectadas al llegar, o 0 si todavía no llegó.
     */
    @Override
    public int getGemasObtenidas() {
        return gemasObtenidas;
    }

    /**
     * Acredita las gemas a la nave a través del asistente (Fase 4).
     *
     * @pre La misión fue exitosa: realizó su acción y getGemasObtenidas() > 0.
     * @post La nave sumó getGemasObtenidas() gemas.
     */
    @Override
    protected void aplicarCierre() {
        assert gemasObtenidas > 0 : "Sólo se acreditan gemas si se recolectaron.";
        getAsistente().acreditarGemas(gemasObtenidas);
    }

    @Override
    public String getCondicionDeExito() {
        return "llegar al punto de interés y recolectar las gemas";
    }
}
