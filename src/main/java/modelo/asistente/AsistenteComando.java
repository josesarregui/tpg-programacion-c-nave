package modelo.asistente;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursoInsuficienteException;
import modelo.bitacora.Bitacora;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.EtapaMision;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import modelo.nave.Nave;
import modelo.nave.TipoNave;
import modelo.tripulacion.Tripulacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Asistente de comandos de una nave (E1-03): implementación de {@link Asistente}.
 *
 * Opera una sola nave (Aclaración, R2), que recibe ya creada por la fábrica, y toda consulta u orden a la nave
 * pasa por él. No implementa el comportamiento de los subsistemas: delega en la nave (recursos) y en el
 * Motor Warp (transiciones), coordina las órdenes y registra en su Bitácora lo que ocurre, incluidos los
 * errores, que luego propaga al invocante (el programa principal o, en la E2, un controlador).
 *
 * Admite una misión pendiente a la vez: para encomendar otra, la anterior debe realizarse o cancelarse.
 * Las misiones realizadas se conservan con su informe, y al cerrar cada una su informe queda resumido
 * en la Bitácora, que así sirve como informe de lo ocurrido en cada misión (Aclaración, R5).
 *
 * Invariante:
 *  - nave != null, bitacora != null y misionesRealizadas != null;
 *  - todas las misiones realizadas están cerradas y fueron encomendadas a este asistente;
 *  - la misión pendiente, si existe, fue encomendada a este asistente y todavía no se cerró; si no se la está
 *    ejecutando, está en etapa CREADA (mientras se ejecuta, consulta y ordena a través del asistente en sus
 *    etapas intermedias);
 *  - la misión en ejecución, si existe, es la misión pendiente.
 */
public class AsistenteComando implements Asistente {

    private final Nave nave;
    private final Bitacora bitacora;
    private final List<Mision> misionesRealizadas;
    private Mision misionPendiente;
    private Mision misionEnEjecucion;

    /**
     * @pre nave != null (la crea la fábrica de naves).
     * @post El asistente opera la nave recibida, con su Bitácora, sin misión pendiente ni misiones realizadas.
     */
    public AsistenteComando(Nave nave) {
        assert nave != null : "El asistente debe operar una nave.";
        this.nave = nave;
        this.bitacora = new Bitacora();
        this.misionesRealizadas = new ArrayList<>();
        this.misionPendiente = null;
        this.misionEnEjecucion = null;
        // Se registra directamente en la Bitácora y no con registrarEvento(), que es público y una variante
        // de asistente podría redefinir: en el constructor esa variante todavía no terminó de construirse.
        bitacora.registrarEvento(new Evento(TipoEvento.SISTEMA, "Asistente a cargo de la nave "
                + nave.getTipo().getDescripcion() + " #" + nave.getId() + "."));
        assert invariante() : "Fallo invariante tras crear el asistente.";
    }

    // --- Consultas sobre la nave ---

    @Override
    public int getIdNave() {
        return nave.getId();
    }

    @Override
    public TipoNave getTipoNave() {
        return nave.getTipo();
    }

    @Override
    public int getCombustible() {
        return nave.getCombustible();
    }

    @Override
    public int getEnergia() {
        return nave.getEnergia();
    }

    @Override
    public int getDesgaste() {
        return nave.getDesgaste();
    }

    @Override
    public boolean requiereMantenimiento() {
        return nave.requiereMantenimiento();
    }

    @Override
    public boolean tieneTripulacion() {
        return nave.getTripulacion() != null;
    }

    @Override
    public boolean naveListaParaOperar() {
        return nave.estaListaParaOperar();
    }

    @Override
    public String getEstadoMotor() {
        return nave.getMotor().getEstado().toString();
    }

    // --- Bitácora ---

    @Override
    public List<Evento> getEventos() {
        return bitacora.getEventos();
    }

    @Override
    public void registrarEvento(TipoEvento tipo, String descripcion) {
        bitacora.registrarEvento(new Evento(tipo, descripcion));
    }

    // --- Órdenes sobre la nave ---

    @Override
    public void asignarTripulacion(Tripulacion tripulacion) {
        assert tripulacion != null : "La tripulación a asignar no puede ser nula.";
        nave.asignarTripulacion(tripulacion);
        registrarEvento(TipoEvento.SISTEMA, "Tripulación asignada (" + tripulacion.getCantidad() + " integrantes).");
        assert tieneTripulacion() : "Fallo postcondición al asignar la tripulación.";
        assert invariante() : "Fallo invariante tras asignar la tripulación.";
    }

    /**
     * Carga combustible en la nave y registra el resultado. Si la carga se rechaza, registra el motivo
     * y propaga la excepción; la nave no cambia (Escenario D).
     *
     * @post getCombustible() == combustible anterior + cantidad.
     */
    @Override
    public void cargarCombustible(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        int combustibleAnterior = nave.getCombustible();
        try {
            nave.cargarCombustible(cantidad);
        } catch (CantidadInvalidaException | CapacidadExcedidaException e) {
            registrarEvento(TipoEvento.ERROR, "Carga de combustible rechazada: " + e.getMessage());
            throw e;
        }
        registrarEvento(TipoEvento.RECURSO, "Carga de " + cantidad + " de combustible. " + nave);
        assert nave.getCombustible() == combustibleAnterior + cantidad : "Fallo postcondición al cargar combustible.";
        assert invariante() : "Fallo invariante tras cargar combustible.";
    }

    /**
     * Carga energía en la nave y registra el resultado. Si la carga se rechaza, registra el motivo
     * y propaga la excepción; la nave no cambia (Escenario D).
     *
     * @post getEnergia() == energía anterior + cantidad.
     */
    @Override
    public void cargarEnergia(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        int energiaAnterior = nave.getEnergia();
        try {
            nave.cargarEnergia(cantidad);
        } catch (CantidadInvalidaException | CapacidadExcedidaException e) {
            registrarEvento(TipoEvento.ERROR, "Carga de energía rechazada: " + e.getMessage());
            throw e;
        }
        registrarEvento(TipoEvento.RECURSO, "Carga de " + cantidad + " de energía. " + nave);
        assert nave.getEnergia() == energiaAnterior + cantidad : "Fallo postcondición al cargar energía.";
        assert invariante() : "Fallo invariante tras cargar energía.";
    }

    @Override
    public void realizarMantenimiento() {
        nave.realizarMantenimiento();
        registrarEvento(TipoEvento.RECURSO, "Mantenimiento realizado. " + nave);
        assert nave.getDesgaste() == 0 : "Fallo postcondición: el mantenimiento debe llevar el desgaste a 0.";
        assert invariante() : "Fallo invariante tras el mantenimiento.";
    }

    /**
     * Aplica en un solo paso el costo de una operación (lo usan las misiones). Si se rechaza,
     * registra el motivo y propaga la excepción; la nave no queda con cambios parciales.
     *
     * @post Combustible y energía disminuyeron y el desgaste aumentó en las cantidades indicadas.
     */
    @Override
    public void consumirRecursos(int combustible, int energia, int desgaste)
            throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException {
        int combustibleAnterior = nave.getCombustible();
        int energiaAnterior = nave.getEnergia();
        int desgasteAnterior = nave.getDesgaste();
        try {
            nave.consumirRecursos(combustible, energia, desgaste);
        } catch (CantidadInvalidaException | RecursoInsuficienteException | CapacidadExcedidaException e) {
            registrarEvento(TipoEvento.ERROR, "Consumo de recursos rechazado: " + e.getMessage());
            throw e;
        }
        registrarEvento(TipoEvento.RECURSO, "Consumo de " + combustible + " de combustible y " + energia
                + " de energía, con " + desgaste + " de desgaste. " + nave);
        assert nave.getCombustible() == combustibleAnterior - combustible
                && nave.getEnergia() == energiaAnterior - energia
                && nave.getDesgaste() == desgasteAnterior + desgaste : "Fallo postcondición al consumir recursos.";
        assert invariante() : "Fallo invariante tras consumir recursos.";
    }

    /**
     * Ordena al Motor Warp preparar el salto (Disponible -> Preparando salto).
     * Si la transición se rechaza, registra el rechazo con el estado en que ocurrió y lo propaga (Escenario C).
     */
    @Override
    public void prepararSalto() throws EstadoMotorInvalidoException {
        try {
            nave.getMotor().prepararSalto();
        } catch (EstadoMotorInvalidoException e) {
            registrarRechazoDelMotor(e);
            throw e;
        }
        registrarEvento(TipoEvento.MOTOR, "Motor Warp: " + getEstadoMotor() + ".");
        assert invariante() : "Fallo invariante tras preparar el salto.";
    }

    /**
     * Ordena saltar: el motor entra en warp y, al terminar el salto, vuelve a Disponible pasando por
     * Enfriamiento, tal como recorre el Escenario C. Como todavía no se modela el paso del tiempo, el
     * enfriamiento es inmediato y la nave queda Disponible cuando termina el salto (Aclaración, R3).
     * Cada cambio del motor queda registrado en la Bitácora.
     *
     * Si el motor no estaba preparando el salto, la primera transición se rechaza y el motor no cambia:
     * se registra el rechazo con el estado en que ocurrió y se propaga (Escenario C). Las transiciones
     * siguientes no pueden fallar, porque cada una parte del estado al que llevó la anterior.
     */
    @Override
    public void saltar() throws EstadoMotorInvalidoException {
        try {
            nave.getMotor().iniciarWarp();
            registrarEvento(TipoEvento.MOTOR, "Motor Warp: " + getEstadoMotor() + ".");
            nave.getMotor().desactivarWarp();
            registrarEvento(TipoEvento.MOTOR, "Motor Warp: " + getEstadoMotor() + ".");
            nave.getMotor().enfriar();
            registrarEvento(TipoEvento.MOTOR, "Motor Warp: " + getEstadoMotor() + ".");
        } catch (EstadoMotorInvalidoException e) {
            registrarRechazoDelMotor(e);
            throw e;
        }
        assert nave.getMotor().estaDisponible() : "Fallo postcondición: al terminar el salto el motor debe quedar Disponible.";
        assert invariante() : "Fallo invariante tras el salto.";
    }

    // --- Misiones ---

    @Override
    public boolean estaEjecutando(Mision mision) {
        return mision != null && misionEnEjecucion == mision;
    }

    @Override
    public boolean tieneMisionPendiente() {
        return misionPendiente != null;
    }

    @Override
    public Mision getMisionPendiente() {
        return misionPendiente;
    }

    @Override
    public List<Mision> getMisionesRealizadas() {
        return Collections.unmodifiableList(misionesRealizadas);
    }

    /**
     * Encomienda una misión a este asistente (Aclaración, R4).
     */
    @Override
    public void encomendarMision(Mision mision) {
        assert mision != null : "La misión a encomendar no puede ser nula.";
        assert misionPendiente == null : "Hay una misión pendiente: debe realizarse o cancelarse antes de encomendar otra.";
        assert mision.getAsistente() == null : "La misión " + mision.getCodigo() + " ya fue encomendada a un asistente.";
        // Primero queda como pendiente de este asistente: la misión verifica que se le encomiende a través de él.
        misionPendiente = mision;
        mision.asignarAsistente(this);
        registrarEvento(TipoEvento.MISION, "Misión encomendada: " + mision.getCodigo() + " — " + mision.getNombre() + ".");
        assert misionPendiente == mision && mision.getAsistente() == this : "Fallo postcondición al encomendar la misión.";
        assert invariante() : "Fallo invariante tras encomendar la misión.";
    }

    @Override
    public void cancelarMision() {
        assert misionPendiente != null : "No hay una misión pendiente para cancelar.";
        registrarEvento(TipoEvento.MISION, "Misión " + misionPendiente.getCodigo() + " cancelada sin realizarse.");
        misionPendiente = null;
        assert !tieneMisionPendiente() : "Fallo postcondición al cancelar la misión.";
        assert invariante() : "Fallo invariante tras cancelar la misión.";
    }

    /**
     * Ejecuta la misión pendiente. Si se rechaza, registra el motivo en la Bitácora y propaga la excepción
     * al invocante; la nave no queda con cambios parciales y la misión sigue pendiente (Escenario B).
     * Si se completa, la misión pasa a las realizadas y su informe queda resumido en la Bitácora (R5).
     */
    @Override
    public InformeMision ejecutarMision() throws NaveNoDisponibleException, RecursoInsuficienteException {
        assert misionPendiente != null : "No hay una misión pendiente para ejecutar.";
        int cantidadAnterior = misionesRealizadas.size();
        registrarEvento(TipoEvento.MISION, "Inicio de la misión " + misionPendiente.getCodigo() + ".");

        InformeMision informe;
        // Mientras dura realizar(), la misión puede verificar que la ejecuta su asistente (estaEjecutando).
        // El bloque finally garantiza que la marca se quite tanto si la misión se completa como si se rechaza
        // (apunte de Excepciones: si ocurre "pre", debe ocurrir "post").
        misionEnEjecucion = misionPendiente;
        try {
            informe = misionPendiente.realizar();
        } catch (NaveNoDisponibleException | RecursoInsuficienteException e) {
            registrarEvento(TipoEvento.ERROR, "Misión " + misionPendiente.getCodigo() + " rechazada: " + e.getMessage());
            throw e;
        } finally {
            misionEnEjecucion = null;
        }

        misionesRealizadas.add(misionPendiente);
        misionPendiente = null;
        registrarInforme(informe);
        assert misionesRealizadas.size() == cantidadAnterior + 1 && !tieneMisionPendiente()
                : "Fallo postcondición: la misión ejecutada debe pasar a las realizadas.";
        assert invariante() : "Fallo invariante tras ejecutar la misión.";
        return informe;
    }

    // --- Auxiliares ---

    /**
     * Deja en la Bitácora el resumen del informe de una misión cerrada: resultado, recursos consumidos,
     * estado final de la nave y observaciones (Aclaración, R5: la Bitácora sirve como informe de cada misión).
     */
    private void registrarInforme(InformeMision informe) {
        registrarEvento(TipoEvento.MISION, "Informe de " + informe.getMision() + ": "
                + (informe.isExitosa() ? "exitosa" : "no exitosa")
                + ". Consumo: combustible " + informe.getCombustibleConsumido()
                + ", energía " + informe.getEnergiaConsumida()
                + ", desgaste " + informe.getDesgasteProducido()
                + ". Estado final: combustible " + informe.getCombustibleFinal()
                + ", energía " + informe.getEnergiaFinal()
                + ", desgaste " + informe.getDesgasteFinal()
                + ", motor " + informe.getEstadoMotorFinal()
                + ", operativa: " + (informe.isNaveOperativa() ? "sí" : "no")
                + ". " + informe.getObservaciones());
    }

    private void registrarRechazoDelMotor(EstadoMotorInvalidoException e) {
        registrarEvento(TipoEvento.ERROR, e.getMessage() + " (estado: " + e.getEstadoActual() + ")");
    }

    private boolean invariante() {
        boolean realizadasCerradas = true;
        // Invariante de ciclo: todas las misiones ya recorridas están cerradas y fueron encomendadas a este asistente.
        for (Mision mision : misionesRealizadas) {
            realizadasCerradas = realizadasCerradas && mision != null
                    && mision.getEtapa() == EtapaMision.CERRADA && mision.getAsistente() == this;
        }
        boolean pendienteValida = misionPendiente == null
                || (misionPendiente.getAsistente() == this && misionPendiente.getEtapa() != EtapaMision.CERRADA
                    && (misionEnEjecucion == misionPendiente || misionPendiente.getEtapa() == EtapaMision.CREADA));
        boolean ejecucionValida = misionEnEjecucion == null || misionEnEjecucion == misionPendiente;
        return nave != null && bitacora != null && misionesRealizadas != null
                && realizadasCerradas && pendienteValida && ejecucionValida;
    }
}
