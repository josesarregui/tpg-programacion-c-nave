package modelo.nave;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.RecursoInsuficienteException;
import modelo.asistente.Asistente;
import modelo.tripulacion.Tripulacion;
import modelo.universo.ElementoDelUniverso;
import modelo.warp.MotorWarp;
import modelo.warp.SuministroMotor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstracción común de las naves (E1-01): reúne identidad, tipo, recursos,
 * tripulación y Motor Warp.
 *
 * Las naves se crean sólo mediante {@link NaveFactory} (E1-07): el constructor
 * es de paquete,
 * de modo que el código cliente no puede instanciar las clases concretas.
 *
 * La nave no implementa la lógica de sus subsistemas: delega las operaciones
 * sobre recursos
 * en {@link Recursos} y las transiciones del motor en {@link MotorWarp} (patrón
 * State).
 * Los recursos no se exponen: sólo pueden consultarse o modificarse a través de
 * la nave.
 *
 * Implementa {@link ObservableNave} (patrón Observer) para notificar a las
 * vistas y componentes
 * observadores en tiempo real cuando sus recursos o su estado sufren
 * modificaciones.
 *
 * Implementa {@link ElementoDelUniverso} (Enunciado E2, punto A): las otras naves del tablero son
 * Exploradoras, Cargueros o de Combate creados por la fábrica. Las batallas se resuelven por Double Dispatch
 * puro, sin condicionales de tipo: la nave propia recibe {@link #enfrentarseCon(Nave)} (ida) y le avisa a la
 * enemiga qué tipo de nave la enfrenta con {@code serEnfrentadaPor...} (vuelta). El resultado siempre se
 * expresa desde el punto de vista de la nave propia.
 *
 * La nave queda destruida, de forma definitiva, si pierde una batalla o si el desgaste llega a 100, la energía
 * queda en 0 o el combustible queda en 0 (Enunciado E2, Fase 2). Con la nave destruida, la tripulación
 * murió y la nave ya no está lista para operar.
 *
 * Invariante:
 * - id > 0;
 * - recursos != null y motor != null y observadores != null;
 * - gemas >= 0, combustibleEntregado >= 0 y energiaEntregada >= 0;
 * - si los recursos están agotados (combustible 0, energía 0 o desgaste 100), la nave está destruida.
 * La tripulación puede no estar asignada todavía (Escenario A: se asigna
 * después de crear la nave).
 */
public abstract class Nave implements ObservableNave, ElementoDelUniverso {

    private static int ultimoIdAsignado = 0;

    private final int id;
    private final Recursos recursos;
    private final MotorWarp motor;
    private final List<ObservadorNave> observadores;
    private Tripulacion tripulacion;
    private boolean destruida;
    private int gemas;
    private int combustibleEntregado;
    private int energiaEntregada;

    /**
     * Crea una nave con su configuración inicial de recursos, el motor en estado
     * Disponible,
     * sin tripulación asignada y lista de observadores vacía.
     *
     * @pre Los valores iniciales están dentro de los rangos de {@link Recursos} y
     *      ninguno deja los recursos agotados.
     * @post La nave tiene un id único, los recursos indicados, el motor
     *       inicializado, no tiene tripulación, no está destruida, no tiene gemas
     *       y no entregó suministros.
     */
    Nave(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        ultimoIdAsignado++;
        this.id = ultimoIdAsignado;
        this.recursos = new Recursos(combustibleInicial, energiaInicial, desgasteInicial);
        this.motor = new MotorWarp(new SuministroDeNave());
        this.observadores = new ArrayList<>();
        this.tripulacion = null;
        this.destruida = false;
        this.gemas = 0;
        this.combustibleEntregado = 0;
        this.energiaEntregada = 0;
        assert invariante() : "Fallo invariante: la nave quedó inconsistente tras su creación.";
    }

    /**
     * @post El valor retornado es distinto de null.
     * @return tipo de la nave.
     */
    public abstract TipoNave getTipo();

    // --- Double Dispatch (Enunciado E2, punto A) ---

    /**
     * Primer envío (ida): esta nave, la propia, se enfrenta con una nave enemiga. Cada subclase le avisa a la
     * enemiga qué tipo de nave la enfrenta, invocando su {@code serEnfrentadaPor...} con this (vuelta).
     * No modifica ninguna de las dos naves: aplicar el resultado es responsabilidad de
     * {@link #aplicarResultado(ResultadoBatalla)}.
     *
     * @pre enemiga != null.
     * @post El valor retornado no es nulo y corresponde a la tabla de enfrentamientos (nave propia vs. enemiga):
     *       mismo tipo, pacto de paz; Exploradora gana a Carguero, Carguero gana a Combate y Combate gana a
     *       Exploradora.
     * @param enemiga nave con la que coincide en la misma coordenada.
     * @return resultado de la batalla para esta nave.
     */
    public abstract ResultadoBatalla enfrentarseCon(Nave enemiga);

    /**
     * Segundo envío (vuelta): una Exploradora propia enfrenta a esta nave.
     *
     * @pre propia != null.
     * @post El valor retornado no es nulo y es el resultado para la Exploradora propia.
     */
    abstract ResultadoBatalla serEnfrentadaPorExploradora(Exploradora propia);

    /**
     * Segundo envío (vuelta): un Carguero propio enfrenta a esta nave.
     *
     * @pre propia != null.
     * @post El valor retornado no es nulo y es el resultado para el Carguero propio.
     */
    abstract ResultadoBatalla serEnfrentadaPorCarguero(Carguero propia);

    /**
     * Segundo envío (vuelta): una nave de Combate propia enfrenta a esta nave.
     *
     * @pre propia != null.
     * @post El valor retornado no es nulo y es el resultado para la nave de Combate propia.
     */
    abstract ResultadoBatalla serEnfrentadaPorCombate(Combate propia);

    /**
     * Aplica a esta nave el resultado de una batalla: si lo perdió, queda destruida junto con su tripulación
     * (Enunciado E2, punto A). Ganar o firmar un pacto de paz no la modifica. Una nave destruida sigue
     * destruida.
     *
     * @pre resultado != null.
     * @post estaDestruida() == (destruida antes || resultado.destruyeNavePropia()).
     */
    public void aplicarResultado(ResultadoBatalla resultado) {
        assert resultado != null : "El resultado de la batalla no puede ser nulo.";
        boolean destruidaAntes = destruida;
        destruida = destruida || resultado.destruyeNavePropia();
        assert destruida == (destruidaAntes || resultado.destruyeNavePropia())
                : "Fallo postcondición al aplicar el resultado de la batalla.";
        assert invariante() : "Fallo invariante tras aplicar el resultado de la batalla.";
        notificarObservadores();
    }

    // --- ElementoDelUniverso ---

    /**
     * @post El valor retornado no es nulo ni vacío.
     * @return tipo e identificador de la nave (por ejemplo, "Carguero #3").
     */
    @Override
    public String getNombre() {
        return getTipo().getDescripcion() + " #" + id;
    }

    /**
     * @post El valor retornado es el símbolo del tipo de la nave, el mismo para todas las naves de una clase.
     */
    @Override
    public String getSimbolo() {
        return getTipo().getSimbolo();
    }

    /**
     * Esta nave, como elemento del tablero, se cruza con la nave propia que opera el asistente: le pide al
     * asistente que la nave propia se enfrente con ella (ida del Double Dispatch). Toda orden a la nave
     * propia pasa por su asistente, que aplica el resultado y lo registra en la Bitácora (Aclaración, R2 y R5).
     *
     * @pre asistente != null; asistente.getNave() != this y la nave del asistente no está destruida.
     * @post La batalla se resolvió, su resultado quedó aplicado a la nave del asistente y registrado en su
     *       Bitácora.
     */
    @Override
    public final void interactuarCon(Asistente asistente) {
        assert asistente != null : "El asistente no puede ser nulo.";
        asistente.enfrentarseCon(this);
    }

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
     * Asigna (o reemplaza) la tripulación de la nave. La {@link Tripulacion} ya
     * garantiza
     * su propio invariante (un/a capitán/a y al menos cuatro tripulantes
     * adicionales).
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

    // --- Estado de destrucción (Enunciado E2) ---

    /**
     * @post Retorna true si la nave perdió una batalla o si sus recursos se agotaron alguna vez
     *       (combustible 0, energía 0 o desgaste 100). Una vez destruida, sigue destruida.
     */
    public boolean estaDestruida() {
        return destruida;
    }

    /**
     * La tripulación muere cuando la nave se destruye (Enunciado E2).
     *
     * @post Retorna true si getTripulacion() != null y !estaDestruida().
     */
    public boolean tripulacionConVida() {
        return tripulacion != null && !destruida;
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
     * @post Retorna true si el desgaste alcanzó el umbral de mantenimiento (80 o
     *       más).
     */
    public boolean requiereMantenimiento() {
        return recursos.requiereMantenimiento();
    }

    /**
     * Indica si la nave puede operar: no está destruida, tiene tripulación asignada, no requiere
     * mantenimiento
     * (el desgaste tiene consecuencias sobre la capacidad operativa, Guía I.8) y su
     * motor está
     * en Disponible (Aclaración, R4: antes de actuar, la misión verifica que la
     * nave esté disponible).
     *
     * @post Retorna true si !estaDestruida(), getTripulacion() != null, !requiereMantenimiento() y
     *       getMotor().estaDisponible().
     */
    public boolean estaListaParaOperar() {
        return !destruida && tripulacion != null && !recursos.requiereMantenimiento() && motor.estaDisponible();
    }

    // --- Recursos (E1-09): operaciones, delegadas en Recursos ---

    /**
     * @throws CantidadInvalidaException  si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera la capacidad máxima. La
     *                                    nave no cambia.
     */
    public void cargarCombustible(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        recursos.cargarCombustible(cantidad);
        notificarObservadores();
    }

    /**
     * @throws CantidadInvalidaException  si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera la capacidad máxima. La
     *                                    nave no cambia.
     */
    public void cargarEnergia(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        recursos.cargarEnergia(cantidad);
        notificarObservadores();
    }

    /**
     * Aplica el costo de una operación (por ejemplo, el de una misión) en un solo
     * paso:
     * si alguno de los recursos no alcanza, se rechaza sin cambios parciales.
     * Si el consumo deja el combustible o la energía en 0, o el desgaste en 100, la nave queda destruida.
     *
     * @throws CantidadInvalidaException    si alguna cantidad es negativa.
     * @throws RecursoInsuficienteException si no hay combustible o energía
     *                                      suficientes.
     * @throws CapacidadExcedidaException   si el desgaste superaría el máximo.
     */
    public void consumirRecursos(int combustible, int energia, int desgaste)
            throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException {
        recursos.consumir(combustible, energia, desgaste);
        actualizarDestruccion();
        notificarObservadores();
    }

    /**
     * @post getDesgaste() == 0 y requiereMantenimiento() == false. Si la nave estaba destruida, sigue
     *       destruida.
     */
    public void realizarMantenimiento() {
        recursos.realizarMantenimiento();
        notificarObservadores();
    }

    /**
     * Aplica la energía que quita un impacto (asteroide, Enunciado E2). No se rechaza: la energía no baja de 0.
     * Si la energía queda en 0, la nave queda destruida.
     *
     * @pre energia >= 0.
     * @post getEnergia() == max(energía anterior - energia, 0); si getEnergia() == 0, estaDestruida().
     */
    public void recibirImpacto(int energia) {
        recursos.descontarEnergia(energia);
        actualizarDestruccion();
        notificarObservadores();
    }

    /**
     * Reabastece combustible y energía (estación de carga, Enunciado E2). No se rechaza: cada recurso queda,
     * como máximo, en su capacidad máxima. No repara una nave destruida.
     *
     * @pre combustible >= 0 y energia >= 0.
     * @post cada recurso aumentó la cantidad indicada, sin superar su capacidad máxima.
     */
    public void reabastecer(int combustible, int energia) {
        recursos.reabastecer(combustible, energia);
        notificarObservadores();
    }

    // --- Gemas (M-02) y suministros (M-01), Enunciado E2 ---

    /**
     * @post El valor retornado es mayor o igual a 0.
     * @return gemas recolectadas por la nave.
     */
    public int getGemas() {
        return gemas;
    }

    /**
     * Acredita las gemas recolectadas (M-02, Fase 4: el cierre impacta en la nave lo ganado).
     *
     * @pre cantidad > 0 y !estaDestruida().
     * @post getGemas() == gemas anteriores + cantidad.
     */
    public void acreditarGemas(int cantidad) {
        assert cantidad > 0 : "La cantidad de gemas a acreditar debe ser positiva.";
        assert !destruida : "Una nave destruida no puede recibir gemas.";
        // Se compara contra el espacio libre, como en Recursos, para que la suma no se desborde.
        assert cantidad <= Integer.MAX_VALUE - gemas : "La cantidad de gemas desbordaría el contador.";
        int gemasAnteriores = gemas;
        gemas = gemas + cantidad;
        assert gemas == gemasAnteriores + cantidad : "Fallo postcondición al acreditar gemas.";
        assert invariante() : "Fallo invariante tras acreditar gemas.";
        notificarObservadores();
    }

    /**
     * @post El valor retornado es mayor o igual a 0.
     * @return combustible entregado en total a naves aliadas (M-01).
     */
    public int getCombustibleEntregado() {
        return combustibleEntregado;
    }

    /**
     * @post El valor retornado es mayor o igual a 0.
     * @return energía entregada en total a naves aliadas (M-01).
     */
    public int getEnergiaEntregada() {
        return energiaEntregada;
    }

    /**
     * Entrega suministros a una nave aliada (M-01, Fase 4: el descuento efectivo se hace en el cierre).
     * Los suministros salen del combustible y la energía de la nave y se descuentan en un solo paso: si alguno
     * no alcanza, se rechaza sin cambios parciales. Si la entrega deja el combustible o la energía en 0, la
     * nave queda destruida, como con cualquier otro consumo.
     *
     * @pre !estaDestruida().
     * @post Combustible y energía disminuyeron en las cantidades indicadas y getCombustibleEntregado() y
     *       getEnergiaEntregada() aumentaron en esas cantidades. Si se lanza una excepción, nada cambia.
     * @throws CantidadInvalidaException    si alguna cantidad es negativa.
     * @throws RecursoInsuficienteException si no hay combustible o energía suficientes.
     */
    public void entregarSuministros(int combustible, int energia)
            throws CantidadInvalidaException, RecursoInsuficienteException {
        assert !destruida : "Una nave destruida no puede entregar suministros.";
        int combustibleAnterior = recursos.getCombustible();
        int energiaAnterior = recursos.getEnergia();
        int combustibleEntregadoAnterior = combustibleEntregado;
        int energiaEntregadaAnterior = energiaEntregada;
        try {
            recursos.consumir(combustible, energia, 0);
        } catch (CapacidadExcedidaException e) {
            // No puede ocurrir: la entrega no suma desgaste. Si ocurriera, sería un error de programación
            // (apunte de Excepciones: RuntimeException).
            throw new IllegalStateException("Error de programación: la entrega de suministros sumó desgaste.", e);
        }
        combustibleEntregado = combustibleEntregado + combustible;
        energiaEntregada = energiaEntregada + energia;
        actualizarDestruccion();
        assert recursos.getCombustible() == combustibleAnterior - combustible
                && recursos.getEnergia() == energiaAnterior - energia
                : "Fallo postcondición: la entrega debe descontar los suministros.";
        assert combustibleEntregado == combustibleEntregadoAnterior + combustible
                && energiaEntregada == energiaEntregadaAnterior + energia
                : "Fallo postcondición: la entrega debe acumular los suministros entregados.";
        assert invariante() : "Fallo invariante tras entregar suministros.";
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

    /**
     * Destruye la nave si sus recursos quedaron agotados (Enunciado E2, Fase 2). Se invoca después de toda
     * operación que puede disminuir combustible o energía o aumentar el desgaste.
     *
     * @post Si los recursos están agotados, estaDestruida(); una nave destruida sigue destruida.
     */
    private void actualizarDestruccion() {
        destruida = destruida || recursos.agotados();
        assert invariante() : "Fallo invariante tras actualizar el estado de destrucción.";
    }

    private boolean invariante() {
        return id > 0 && recursos != null && motor != null && observadores != null
                && gemas >= 0 && combustibleEntregado >= 0 && energiaEntregada >= 0
                && (!recursos.agotados() || destruida);
    }

    /**
     * Da al Motor Warp acceso a los recursos de esta nave (Anexo: el motor verifica la energía y cobra los
     * costos del salto). Es una clase interna privada: las operaciones de consumo sólo las ve el motor y no
     * quedan públicas en la nave. Después de cada consumo actualiza el estado de destrucción: un salto puede
     * dejar la nave sin combustible o sin energía, y el enfriamiento puede llevar el desgaste a 100.
     */
    private class SuministroDeNave implements SuministroMotor {

        @Override
        public int getCombustible() {
            return recursos.getCombustible();
        }

        @Override
        public int getEnergia() {
            return recursos.getEnergia();
        }

        @Override
        public void consumirCombustible(int cantidad) {
            consumirSinRechazo(cantidad, 0);
        }

        @Override
        public void consumirEnergia(int cantidad) {
            consumirSinRechazo(0, cantidad);
        }

        @Override
        public void sumarDesgaste(int cantidad) {
            recursos.sumarDesgaste(cantidad);
            actualizarDestruccion();
        }

        /**
         * El estado del motor ya verificó que el recurso alcanza, por eso un rechazo sólo puede deberse a un
         * error de programación (apunte de Excepciones: RuntimeException).
         */
        private void consumirSinRechazo(int combustible, int energia) {
            try {
                recursos.consumir(combustible, energia, 0);
            } catch (CantidadInvalidaException | RecursoInsuficienteException | CapacidadExcedidaException e) {
                throw new IllegalStateException("Error de programación: el motor pidió consumir sin verificar.", e);
            }
            actualizarDestruccion();
        }
    }

    @Override
    public String toString() {
        return getNombre() + " [" + recursos + (destruida ? ", destruida" : "") + "]";
    }
}
