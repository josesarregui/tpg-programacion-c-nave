package modelo.nave;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.RecursoInsuficienteException;
import modelo.tripulacion.Tripulacion;
import modelo.warp.MotorWarp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * Implementa {@link ObservableNave} (patrón Observer) para notificar a las vistas y componentes
 * observadores en tiempo real cuando sus recursos o su estado sufren modificaciones.
 *
 * Invariante:
 *  - id > 0;
 *  - recursos != null y motor != null y observadores != null.
 * La tripulación puede no estar asignada todavía (Escenario A: se asigna después de crear la nave).
 */
public abstract class Nave implements ObservableNave {

    private static int ultimoIdAsignado = 0;

    private final int id;
    private final Recursos recursos;
    private final MotorWarp motor;
    private final List<ObservadorNave> observadores;
    private Tripulacion tripulacion;

    /**
     * Crea una nave con su configuración inicial de recursos, el motor en estado Disponible,
     * sin tripulación asignada y lista de observadores vacía.
     *
     * @pre Los valores iniciales están dentro de los rangos de {@link Recursos}.
     * @post La nave tiene un id único, los recursos indicados, el motor inicializado y no tiene tripulación.
     */
    Nave(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        ultimoIdAsignado++;
        this.id = ultimoIdAsignado;
        this.recursos = new Recursos(combustibleInicial, energiaInicial, desgasteInicial);
        this.motor = new MotorWarp();
        this.observadores = new ArrayList<>();
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
        notificarObservadores();
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
        notificarObservadores();
    }

    /**
     * @throws CantidadInvalidaException si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera la capacidad máxima. La nave no cambia.
     */
    public void cargarEnergia(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        recursos.cargarEnergia(cantidad);
        notificarObservadores();
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
        notificarObservadores();
    }

    /**
     * @post getDesgaste() == 0 y requiereMantenimiento() == false.
     */
    public void realizarMantenimiento() {
        recursos.realizarMantenimiento();
        notificarObservadores();
    }

    // --- Patron Observer ---

    @Override
    public void agregarObservador(ObservadorNave observador) {
        assert observador != null : "El observador a agregar no puede ser nulo.";
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void eliminarObservador(ObservadorNave observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores() {
        for (ObservadorNave observador : observadores) {
            observador.notificarCambio(this);
        }
    }

    /**
     * @return lista de sólo lectura con los observadores actualmente registrados.
     */
    public List<ObservadorNave> getObservadores() {
        return Collections.unmodifiableList(observadores);
    }

    
    
    private boolean invariante() {
        return id > 0 && recursos != null && motor != null && observadores != null;
    }

    @Override
    public String toString() {
        return getTipo().getDescripcion() + " #" + id + " [" + recursos + "]";
    }
}
