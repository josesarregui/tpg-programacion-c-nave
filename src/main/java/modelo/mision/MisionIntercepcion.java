package modelo.mision;

import excepcion.CantidadInvalidaException;
import excepcion.RecursoInsuficienteException;
import modelo.universo.Destino;
import modelo.universo.Posicion;
import modelo.universo.Tablero;

import java.util.Random;

/**
 * M-01 — Intercepción y asistencia (Enunciado E2): alcanzar a una nave aliada que solicita auxilio y entregarle
 * combustible y energía. Los recursos se transfieren sólo cuando ambas naves están en la misma posición: el
 * objetivo del tablero ({@link Tablero#obtenerObjetivo()}).
 *
 * <ul>
 *   <li>Suministros solicitados: los pide la nave aliada y son variables (ni la Guía 4.2 ni la Tabla de Recursos
 *       fijan montos). Se sortean al crear la misión, entre {@link #SUMINISTRO_MINIMO} y {@link #SUMINISTRO_MAXIMO},
 *       para que la preparación pueda verificarlos (Aclaración, R4: cada misión conoce sus requisitos). El generador
 *       es inyectable para que las pruebas sean deterministas.</li>
 *   <li>Verificación en destino (Fase 2): la nave debe conservar los suministros tras el viaje; si no, la misión
 *       falla. La entrega se descuenta en el cierre (Fase 4).</li>
 *   <li>Costo de la acción final: {@link #ENERGIA_ADICIONAL} de energía (Tabla de Recursos, IV.4).</li>
 * </ul>
 *
 * Sólo redefine sus pasos particulares; el ciclo lo fija {@link Mision#realizar()} (Template Method).
 *
 * Invariante: SUMINISTRO_MINIMO <= combustibleAEntregar, energiaAEntregar <= SUMINISTRO_MAXIMO.
 */
public class MisionIntercepcion extends Mision {

    public static final int ENERGIA_ADICIONAL = 5;
    /** Mínimo de cada suministro que puede solicitar la nave aliada. */
    public static final int SUMINISTRO_MINIMO = 5;
    /** Máximo de cada suministro que puede solicitar la nave aliada. */
    public static final int SUMINISTRO_MAXIMO = 20;

    private final int combustibleAEntregar;
    private final int energiaAEntregar;

    /**
     * @pre tablero y destino no nulos, y destino creado sobre ese tablero.
     * @post Los suministros a entregar quedaron sorteados entre SUMINISTRO_MINIMO y SUMINISTRO_MAXIMO.
     */
    public MisionIntercepcion(Tablero tablero, Destino destino) {
        this(tablero, destino, new Random());
    }

    /**
     * @pre tablero y destino no nulos, y destino creado sobre ese tablero; azar != null.
     * @post Los suministros a entregar quedaron sorteados con azar entre SUMINISTRO_MINIMO y SUMINISTRO_MAXIMO.
     */
    public MisionIntercepcion(Tablero tablero, Destino destino, Random azar) {
        super("M-01", "Intercepción y asistencia", tablero, destino);
        assert azar != null : "El generador de suministros no puede ser nulo.";
        this.combustibleAEntregar = sortear(azar);
        this.energiaAEntregar = sortear(azar);
        assert invariante() : "Fallo invariante tras crear la misión M-01.";
    }

    private static int sortear(Random azar) {
        return SUMINISTRO_MINIMO + azar.nextInt(SUMINISTRO_MAXIMO - SUMINISTRO_MINIMO + 1);
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
     * @return combustible solicitado por la nave aliada.
     */
    @Override
    public int getCombustibleAEntregar() {
        return combustibleAEntregar;
    }

    /**
     * @return energía solicitada por la nave aliada.
     */
    @Override
    public int getEnergiaAEntregar() {
        return energiaAEntregar;
    }

    /**
     * La nave llegó a la posición de la nave aliada y conserva los suministros: queda establecido el contacto.
     */
    @Override
    protected void realizarAccion() {
        registrarAccion("Ejecución: contacto con la nave aliada; asistencia de " + combustibleAEntregar
                + " de combustible y " + energiaAEntregar + " de energía (se entrega en el cierre).");
    }

    /**
     * Entrega los suministros a la nave aliada a través del asistente (Fase 4).
     *
     * @pre La verificación en destino comprobó que la nave conserva más combustible y energía que los
     *      suministros, de modo que la entrega no se rechaza ni destruye la nave.
     * @post La nave entregó combustibleAEntregar de combustible y energiaAEntregar de energía.
     */
    @Override
    protected void aplicarCierre() {
        try {
            getAsistente().entregarSuministros(combustibleAEntregar, energiaAEntregar);
        } catch (CantidadInvalidaException | RecursoInsuficienteException e) {
            // No puede ocurrir: las cantidades son positivas y la verificación en destino garantizó que alcanzan.
            throw new IllegalStateException("Error de programación al entregar los suministros de M-01.", e);
        }
    }

    @Override
    protected String getCondicionDeExito() {
        return "llegar a la nave aliada y conservar los suministros a entregar";
    }

    private boolean invariante() {
        return combustibleAEntregar >= SUMINISTRO_MINIMO && combustibleAEntregar <= SUMINISTRO_MAXIMO
                && energiaAEntregar >= SUMINISTRO_MINIMO && energiaAEntregar <= SUMINISTRO_MAXIMO;
    }
}
