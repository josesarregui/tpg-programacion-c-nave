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
import modelo.nave.ResultadoBatalla;
import modelo.nave.TipoNave;
import modelo.tripulacion.Tripulacion;

import java.util.List;
import modelo.nave.Nave;
import modelo.tripulacion.Tripulante;

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
    
    
    // --- Consultas sobre la Tripulacion ---
    
    public List<Tripulante> getTripulantes();
    
    public Tripulante getCapitan();
    
    public int getCantidadTripulantes();

    // --- Consultas sobre la nave que opera ---
    
    /**
     * @return la nave operada por el asistente.
     */
    Nave getNave();

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
     * @return true si la nave fue destruida (perdió una batalla o agotó sus recursos, Enunciado E2). Con la nave
     *         destruida, su tripulación murió.
     */
    boolean naveDestruida();

    /**
     * @return gemas recolectadas por la nave (M-02).
     */
    int getGemas();

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
     * Aplica a la nave la energía que le quita un impacto (asteroide, Enunciado E2). No se rechaza:
     * la energía no baja de 0.
     *
     * @pre energia >= 0.
     * @post getEnergia() == max(energía anterior - energia, 0) y quedó registrado en la Bitácora.
     */
    void recibirImpacto(int energia);

    /**
     * Reabastece la nave (estación de carga, Enunciado E2). No se rechaza: cada recurso queda, como máximo,
     * en su capacidad máxima.
     *
     * @pre combustible >= 0 y energia >= 0.
     * @post cada recurso aumentó la cantidad indicada sin superar su capacidad máxima y quedó registrado
     *       en la Bitácora.
     */
    void reabastecer(int combustible, int energia);

    /**
     * La nave que opera el asistente se enfrenta en batalla con una nave enemiga que coincide en su misma
     * coordenada (Enunciado E2, punto A). El resultado se resuelve por Double Dispatch y depende sólo del tipo
     * de cada nave; si la nave pierde, queda destruida junto con su tripulación.
     *
     * @pre enemiga != null, enemiga != getNave() y !naveDestruida().
     * @post El resultado quedó aplicado a la nave y registrado en la Bitácora; si destruye a la nave propia,
     *       naveDestruida() y la destrucción también quedó registrada.
     * @return resultado de la batalla para la nave que opera el asistente.
     */
    ResultadoBatalla enfrentarseCon(Nave enemiga);

    /**
     * Acredita a la nave las gemas recolectadas (M-02).
     *
     * @pre cantidad > 0 y !naveDestruida().
     * @post getGemas() == gemas anteriores + cantidad y quedó registrado en la Bitácora.
     */
    void acreditarGemas(int cantidad);

    /**
     * Entrega suministros de la nave a una nave aliada (M-01). Si se rechaza, registra el motivo y propaga la
     * excepción; la nave no cambia.
     *
     * @pre !naveDestruida().
     * @post Combustible y energía disminuyeron en las cantidades indicadas y quedó registrado en la Bitácora.
     * @throws CantidadInvalidaException si alguna cantidad es negativa.
     * @throws RecursoInsuficienteException si no hay combustible o energía suficientes.
     */
    void entregarSuministros(int combustible, int energia) throws CantidadInvalidaException, RecursoInsuficienteException;

    /**
     * Ordena un salto normal al Motor Warp (Anexo): la nave avanza 1 casillero y consume combustible.
     * Es una orden directa al motor: verificar que la nave esté lista para operar (tripulación y
     * mantenimiento) es responsabilidad de la misión antes de actuar (Aclaración, R4).
     *
     * @return casilleros que debe avanzar la nave.
     * @throws EstadoMotorInvalidoException si el motor está en medio de una maniobra warp. Nada cambia.
     * @throws RecursoInsuficienteException si no alcanza el combustible. Nada cambia.
     */
    int saltar() throws EstadoMotorInvalidoException, RecursoInsuficienteException;

    /**
     * Ordena al Motor Warp avanzar un turno de la maniobra warp (Anexo): preparar (2 turnos), saltar 2
     * casilleros o enfriar, según el estado del motor. Siempre es una orden válida.
     *
     * @return casilleros que debe avanzar la nave en este turno (0 o 2).
     */
    int saltarWarp();

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

    // --- Patrón Observer (E2 / E3) ---

    /**
     * Registra un observador en la nave que opera este asistente.
     *
     * @param observador el observador a agregar (no nulo).
     */
    void agregarObservador(modelo.nave.ObservadorNave observador);

    /**
     * Elimina un observador previamente registrado en la nave.
     *
     * @param observador el observador a eliminar.
     */
    void eliminarObservador(modelo.nave.ObservadorNave observador);
}
