package modelo.asistente;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursoInsuficienteException;
import modelo.bitacora.Bitacora;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import modelo.nave.Nave;
import modelo.tripulacion.Tripulacion;

import java.util.List;

/**
 * Asistente de comandos de una nave (E1-03; Aclaración, R2 y R5): versión mínima necesaria para
 * encomendar y ejecutar misiones.
 *
 * Opera una sola nave y toda consulta u orden a la nave pasa por él. No implementa el comportamiento
 * de los subsistemas: delega en la nave (recursos) y en el Motor Warp (transiciones), coordina las
 * órdenes y registra en su Bitácora lo que ocurre, incluidos los errores, que luego propaga al
 * invocante (el programa principal o, en la E2, un controlador).
 *
 * Invariante: nave != null y bitacora != null.
 */
public class AsistenteComando {

    private final Nave nave;
    private final Bitacora bitacora;
    private Mision misionEncomendada;

    /**
     * @pre nave != null (la crea la fábrica de naves).
     * @post El asistente opera la nave recibida, con su Bitácora y sin misión encomendada.
     */
    public AsistenteComando(Nave nave) {
        assert nave != null : "El asistente debe operar una nave.";
        this.nave = nave;
        this.bitacora = new Bitacora();
        this.misionEncomendada = null;
        registrarEvento(TipoEvento.SISTEMA, "Asistente a cargo de la nave " + nave.getTipo().getDescripcion()
                + " #" + nave.getId() + ".");
        assert invariante() : "Fallo invariante tras crear el asistente.";
    }

    // --- Consultas sobre la nave ---

    public String getDescripcionNave() {
        return nave.toString();
    }

    public int getCombustible() {
        return nave.getCombustible();
    }

    public int getEnergia() {
        return nave.getEnergia();
    }

    public int getDesgaste() {
        return nave.getDesgaste();
    }

    public boolean requiereMantenimiento() {
        return nave.requiereMantenimiento();
    }

    public boolean tieneTripulacion() {
        return nave.getTripulacion() != null;
    }

    public boolean naveListaParaOperar() {
        return nave.estaListaParaOperar();
    }

    /**
     * @return nombre del estado actual del Motor Warp.
     */
    public String getEstadoMotor() {
        return nave.getMotor().getEstado().toString();
    }

    /**
     * @return eventos de la Bitácora en orden temporal (lista de sólo lectura).
     */
    public List<Evento> getEventos() {
        return bitacora.getEventos();
    }

    /**
     * @return la última misión encomendada, o null si no se encomendó ninguna.
     */
    public Mision getMisionEncomendada() {
        return misionEncomendada;
    }

    // --- Órdenes sobre la nave ---

    /**
     * @pre tripulacion != null.
     * @post La nave tiene asignada la tripulación y quedó registrado en la Bitácora.
     */
    public void asignarTripulacion(Tripulacion tripulacion) {
        nave.asignarTripulacion(tripulacion);
        registrarEvento(TipoEvento.SISTEMA, "Tripulación asignada (" + tripulacion.getCantidad() + " integrantes).");
    }

    /**
     * Carga combustible en la nave y registra el resultado. Si la carga se rechaza, registra el motivo
     * y propaga la excepción; la nave no cambia (Escenario D).
     */
    public void cargarCombustible(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        try {
            nave.cargarCombustible(cantidad);
            registrarEvento(TipoEvento.RECURSO, "Carga de " + cantidad + " de combustible. " + nave);
        } catch (CantidadInvalidaException | CapacidadExcedidaException e) {
            registrarEvento(TipoEvento.ERROR, "Carga de combustible rechazada: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Carga energía en la nave y registra el resultado. Si la carga se rechaza, registra el motivo
     * y propaga la excepción; la nave no cambia (Escenario D).
     */
    public void cargarEnergia(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        try {
            nave.cargarEnergia(cantidad);
            registrarEvento(TipoEvento.RECURSO, "Carga de " + cantidad + " de energía. " + nave);
        } catch (CantidadInvalidaException | CapacidadExcedidaException e) {
            registrarEvento(TipoEvento.ERROR, "Carga de energía rechazada: " + e.getMessage());
            throw e;
        }
    }

    /**
     * @post El desgaste de la nave es 0 y quedó registrado en la Bitácora.
     */
    public void realizarMantenimiento() {
        nave.realizarMantenimiento();
        registrarEvento(TipoEvento.RECURSO, "Mantenimiento realizado. " + nave);
    }

    /**
     * Aplica en un solo paso el costo de una operación (lo usan las misiones). Si se rechaza,
     * registra el motivo y propaga la excepción; la nave no queda con cambios parciales.
     */
    public void consumirRecursos(int combustible, int energia, int desgaste)
            throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException {
        try {
            nave.consumirRecursos(combustible, energia, desgaste);
            registrarEvento(TipoEvento.RECURSO, "Consumo de " + combustible + " de combustible y " + energia
                    + " de energía, con " + desgaste + " de desgaste. " + nave);
        } catch (CantidadInvalidaException | RecursoInsuficienteException | CapacidadExcedidaException e) {
            registrarEvento(TipoEvento.ERROR, "Consumo de recursos rechazado: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Ordena al Motor Warp preparar el salto (Disponible -> Preparando salto).
     *
     * @throws EstadoMotorInvalidoException si la transición no es válida en el estado actual; se registra en la Bitácora.
     */
    public void prepararSalto() {
        try {
            nave.getMotor().prepararSalto();
            registrarEvento(TipoEvento.MOTOR, "Motor Warp: " + getEstadoMotor() + ".");
        } catch (EstadoMotorInvalidoException e) {
            registrarEvento(TipoEvento.ERROR, e.getMessage() + " (estado: " + e.getEstadoActual() + ")");
            throw e;
        }
    }

    /**
     * Ordena saltar: el motor entra en warp y, al terminar el salto, vuelve a Disponible pasando por
     * Enfriamiento (Aclaración, R3: como todavía no se modela el paso del tiempo, la nave vuelve a
     * Disponible cuando termina el salto; se respeta la secuencia del Escenario C).
     *
     * @throws EstadoMotorInvalidoException si alguna transición no es válida en el estado actual; se registra en la Bitácora.
     */
    public void saltar() {
        try {
            nave.getMotor().iniciarWarp();
            registrarEvento(TipoEvento.MOTOR, "Motor Warp: " + getEstadoMotor() + ".");
            nave.getMotor().desactivarWarp();
            registrarEvento(TipoEvento.MOTOR, "Motor Warp: " + getEstadoMotor() + ".");
            nave.getMotor().enfriar();
            registrarEvento(TipoEvento.MOTOR, "Motor Warp: " + getEstadoMotor() + ".");
        } catch (EstadoMotorInvalidoException e) {
            registrarEvento(TipoEvento.ERROR, e.getMessage() + " (estado: " + e.getEstadoActual() + ")");
            throw e;
        }
    }

    // --- Misiones ---

    /**
     * Encomienda una misión a este asistente (Aclaración, R4).
     *
     * @pre mision != null y no fue encomendada a otro asistente.
     * @post getMisionEncomendada() == mision y mision.getAsistente() == this.
     */
    public void encomendarMision(Mision mision) {
        assert mision != null : "La misión a encomendar no puede ser nula.";
        mision.asignarAsistente(this);
        this.misionEncomendada = mision;
        registrarEvento(TipoEvento.MISION, "Misión encomendada: " + mision.getCodigo() + " — " + mision.getNombre() + ".");
        assert misionEncomendada == mision && mision.getAsistente() == this : "Fallo postcondición al encomendar la misión.";
    }

    /**
     * Ejecuta la misión encomendada. Si se rechaza, registra el motivo en la Bitácora y propaga la excepción
     * al invocante; la nave no queda con cambios parciales (Escenario B).
     *
     * @pre Hay una misión encomendada que todavía no se realizó.
     * @return el informe de la misión.
     * @throws NaveNoDisponibleException si la nave no está lista para operar.
     * @throws RecursoInsuficienteException si no alcanzan los recursos que requiere la misión.
     */
    public InformeMision ejecutarMision() throws NaveNoDisponibleException, RecursoInsuficienteException {
        assert misionEncomendada != null : "No hay una misión encomendada para ejecutar.";
        registrarEvento(TipoEvento.MISION, "Inicio de la misión " + misionEncomendada.getCodigo() + ".");
        try {
            return misionEncomendada.realizar();
        } catch (NaveNoDisponibleException | RecursoInsuficienteException e) {
            registrarEvento(TipoEvento.ERROR, "Misión " + misionEncomendada.getCodigo() + " rechazada: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Registra un acontecimiento en la Bitácora (lo usan también las misiones).
     *
     * @throws IllegalArgumentException si el tipo es nulo o la descripción es nula o vacía.
     */
    public void registrarEvento(TipoEvento tipo, String descripcion) {
        bitacora.registrarEvento(new Evento(tipo, descripcion));
    }

    private boolean invariante() {
        return nave != null && bitacora != null;
    }
}
