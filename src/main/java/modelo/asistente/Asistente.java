package modelo.asistente;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursoInsuficienteException;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import modelo.nave.TipoNave;
import modelo.tripulacion.Tripulacion;

import java.util.List;

/**
 * Asistente de comandos de una nave (E1-03; Aclaración, R2, R4 y R5): define QUÉ debe poder hacer
 * cualquier asistente, sin decir CÓMO (apunte de Interfaces).
 *
 * Cada asistente opera una sola nave y toda consulta u orden a esa nave pasa por él. Lleva una Bitácora
 * con lo que ocurre, se le encomiendan misiones y las ejecuta. Coordina los subsistemas sin implementar
 * su comportamiento interno; cuando una orden se rechaza, registra el motivo y propaga la excepción.
 *
 * El centro de control y las misiones dependen de esta interfaz y no de una clase concreta
 * (principio de inversión de dependencias). Por eso una nueva variante de asistente puede registrarse
 * en el centro de control y realizar misiones sin modificar esas clases (Aclaración, Pedidos del diseño).
 * Las consultas devuelven datos y no texto formateado (Enunciado E1, punto 7), para que puedan
 * mostrarse por consola o en una pantalla sin cambiar el modelo.
 */
public interface Asistente {

    // --- Consultas sobre la nave que opera ---

    /**
     * @return identificador de la nave que opera el asistente.
     */
    int getIdNave();

    /**
     * @return tipo de la nave que opera el asistente (distinto de null).
     */
    TipoNave getTipoNave();

    int getCombustible();

    int getEnergia();

    int getDesgaste();

    boolean requiereMantenimiento();

    boolean tieneTripulacion();

    /**
     * @return true si la nave tiene tripulación, no requiere mantenimiento y su motor está en Disponible.
     */
    boolean naveListaParaOperar();

    /**
     * @return nombre del estado actual del Motor Warp.
     */
    String getEstadoMotor();

    // --- Bitácora (R5) ---

    /**
     * @return eventos de la Bitácora en orden temporal (lista de sólo lectura).
     */
    List<Evento> getEventos();

    /**
     * Registra un acontecimiento en la Bitácora (lo usan también las misiones y el centro de control).
     *
     * @throws IllegalArgumentException si el tipo es nulo o la descripción es nula o vacía.
     */
    void registrarEvento(TipoEvento tipo, String descripcion);

    // --- Órdenes sobre la nave ---

    /**
     * @pre tripulacion != null.
     * @post La nave tiene asignada la tripulación y quedó registrado en la Bitácora.
     */
    void asignarTripulacion(Tripulacion tripulacion);

    /**
     * @throws CantidadInvalidaException si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera la capacidad máxima. La nave no cambia (Escenario D).
     */
    void cargarCombustible(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException;

    /**
     * @throws CantidadInvalidaException si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera la capacidad máxima. La nave no cambia (Escenario D).
     */
    void cargarEnergia(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException;

    /**
     * @post El desgaste de la nave es 0.
     */
    void realizarMantenimiento();

    /**
     * Aplica en un solo paso el costo de una operación (lo usan las misiones): si se rechaza, la nave no cambia.
     */
    void consumirRecursos(int combustible, int energia, int desgaste)
            throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException;

    /**
     * Ordena al Motor Warp preparar el salto (Disponible -> Preparando salto).
     * Es una orden directa al motor (Escenario C): sólo depende del estado del motor. Verificar que la nave
     * esté lista para operar (tripulación y mantenimiento) es responsabilidad de la misión antes de actuar
     * (Aclaración, R4); ver docs/diseño.md, sección 4.
     *
     * @throws EstadoMotorInvalidoException si la transición no es válida en el estado actual. El motor no cambia.
     */
    void prepararSalto() throws EstadoMotorInvalidoException;

    /**
     * Ordena saltar. Al terminar el salto, la nave vuelve a quedar Disponible (Aclaración, R3).
     *
     * @throws EstadoMotorInvalidoException si el motor no estaba preparando el salto. El motor no cambia.
     */
    void saltar() throws EstadoMotorInvalidoException;

    // --- Misiones (R4) ---

    /**
     * @return true si hay una misión encomendada que todavía no se realizó.
     */
    boolean tieneMisionPendiente();

    /**
     * @return la misión encomendada que todavía no se realizó, o null si no hay ninguna.
     */
    Mision getMisionPendiente();

    /**
     * Permite que una misión verifique que se la realiza a través de su asistente (Aclaración, R2).
     *
     * @return true si el asistente está ejecutando esa misión en este momento (dentro de ejecutarMision()).
     */
    boolean estaEjecutando(Mision mision);

    /**
     * @return misiones realizadas por este asistente, en el orden en que se cerraron (lista de sólo lectura).
     *         Cada una conserva su informe.
     */
    List<Mision> getMisionesRealizadas();

    /**
     * Encomienda una misión a este asistente.
     *
     * @pre mision != null, no fue encomendada a otro asistente y !tieneMisionPendiente().
     * @post getMisionPendiente() == mision y mision.getAsistente() == this.
     */
    void encomendarMision(Mision mision);

    /**
     * Cancela la misión pendiente sin realizarla (por ejemplo, después de un rechazo que no se quiere resolver).
     * Como una misión rechazada no modificó la nave, cancelarla tampoco la modifica.
     *
     * @pre tieneMisionPendiente().
     * @post !tieneMisionPendiente() y la cancelación quedó registrada en la Bitácora.
     */
    void cancelarMision();

    /**
     * Ejecuta la misión pendiente. Si se rechaza, registra el motivo y propaga la excepción; la nave no queda
     * con cambios parciales y la misión sigue pendiente, para poder reintentarla (Escenario B).
     *
     * @pre tieneMisionPendiente().
     * @post Si no se lanza excepción: !tieneMisionPendiente(), la misión está al final de getMisionesRealizadas()
     *       y su informe quedó resumido en la Bitácora.
     * @return el informe de la misión.
     * @throws NaveNoDisponibleException si la nave no está lista para operar.
     * @throws RecursoInsuficienteException si no alcanzan los recursos que requiere la misión.
     */
    InformeMision ejecutarMision() throws NaveNoDisponibleException, RecursoInsuficienteException;
}
