package modelo.nave;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.RecursoInsuficienteException;
import modelo.tripulacion.Tripulacion;
import modelo.warp.MotorWarp;

/**
 * Abstracción común de las naves (E1-01): reúne identidad, tipo, recursos, tripulación y Motor Warp.
 *
 * Las naves se crean sólo mediante {@link NaveFactory} (E1-07): el constructor es de paquete,
 * de modo que el código cliente no puede instanciar las clases concretas.
 *
 * La nave no implementa la lógica de sus subsistemas: delega las operaciones sobre recursos
 * en {@link Recursos} y las transiciones del motor en {@link MotorWarp} (patrón State).
 * Los recursos no se exponen: sólo pueden consultarse o modificarse a través de la nave.
 *
 * Invariante:
 *  - id > 0;
 *  - recursos != null y motor != null.
 * La tripulación puede no estar asignada todavía (Escenario A: se asigna después de crear la nave).
 */
public abstract class Nave {

    private static int ultimoIdAsignado = 0;

    private final int id;
    private final Recursos recursos;
    private final MotorWarp motor;
    private Tripulacion tripulacion;

    /**
     * Crea una nave con su configuración inicial de recursos, el motor en estado Disponible
     * y sin tripulación asignada.
     *
     * @pre Los valores iniciales están dentro de los rangos de {@link Recursos}.
     * @post La nave tiene un id único, los recursos indicados, el motor inicializado y no tiene tripulación.
     */
    Nave(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        ultimoIdAsignado++;
        this.id = ultimoIdAsignado;
        this.recursos = new Recursos(combustibleInicial, energiaInicial, desgasteInicial);
        this.motor = new MotorWarp();
        this.tripulacion = null;
        assert invariante() : "Fallo invariante: la nave quedó inconsistente tras su creación.";
    }

    /**
     * @post El valor retornado es distinto de null.
     * @return tipo de la nave.
     */
    public abstract TipoNave getTipo();

    public int getId() {
        return id;
    }

    public MotorWarp getMotor() {
        return motor;
    }

    /**
     * @return la tripulación de la nave, o null si todavía no fue asignada.
     */
    public Tripulacion getTripulacion() {
        return tripulacion;
    }

    /**
     * Asigna (o reemplaza) la tripulación de la nave. La {@link Tripulacion} ya garantiza
     * su propio invariante (un/a capitán/a y al menos cuatro tripulantes adicionales).
     *
     * @pre nuevaTripulacion != null.
     * @post getTripulacion() == nuevaTripulacion.
     */
    public void asignarTripulacion(Tripulacion nuevaTripulacion) {
        assert nuevaTripulacion != null : "La tripulación a asignar no puede ser nula.";
        this.tripulacion = nuevaTripulacion;
        assert this.tripulacion == nuevaTripulacion : "Fallo postcondición al asignar la tripulación.";
        assert invariante() : "Fallo invariante tras asignar la tripulación.";
    }

    // --- Recursos (E1-09): consultas ---

    public int getCombustible() {
        return recursos.getCombustible();
    }

    public int getEnergia() {
        return recursos.getEnergia();
    }

    public int getDesgaste() {
        return recursos.getDesgaste();
    }

    /**
     * @post Retorna true si el desgaste alcanzó el umbral de mantenimiento (80 o más).
     */
    public boolean requiereMantenimiento() {
        return recursos.requiereMantenimiento();
    }

    /**
     * Indica si la nave puede operar: tiene tripulación asignada, no requiere mantenimiento
     * (el desgaste tiene consecuencias sobre la capacidad operativa, Guía I.8) y su motor está
     * en Disponible (Aclaración, R4: antes de actuar, la misión verifica que la nave esté disponible).
     *
     * @post Retorna true si getTripulacion() != null, !requiereMantenimiento() y getMotor().estaDisponible().
     */
    public boolean estaListaParaOperar() {
        return tripulacion != null && !recursos.requiereMantenimiento() && motor.estaDisponible();
    }

    // --- Recursos (E1-09): operaciones, delegadas en Recursos ---

    /**
     * @throws CantidadInvalidaException si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera la capacidad máxima. La nave no cambia.
     */
    public void cargarCombustible(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        recursos.cargarCombustible(cantidad);
    }

    /**
     * @throws CantidadInvalidaException si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera la capacidad máxima. La nave no cambia.
     */
    public void cargarEnergia(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        recursos.cargarEnergia(cantidad);
    }

    /**
     * Aplica el costo de una operación (por ejemplo, el de una misión) en un solo paso:
     * si alguno de los recursos no alcanza, se rechaza sin cambios parciales.
     *
     * @throws CantidadInvalidaException si alguna cantidad es negativa.
     * @throws RecursoInsuficienteException si no hay combustible o energía suficientes.
     * @throws CapacidadExcedidaException si el desgaste superaría el máximo.
     */
    public void consumirRecursos(int combustible, int energia, int desgaste)
            throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException {
        recursos.consumir(combustible, energia, desgaste);
    }

    /**
     * @post getDesgaste() == 0 y requiereMantenimiento() == false.
     */
    public void realizarMantenimiento() {
        recursos.realizarMantenimiento();
    }

    private boolean invariante() {
        return id > 0 && recursos != null && motor != null;
    }

    @Override
    public String toString() {
        return getTipo().getDescripcion() + " #" + id + " [" + recursos + "]";
    }
}
