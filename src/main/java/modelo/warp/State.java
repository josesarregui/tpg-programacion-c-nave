package modelo.warp;

import excepcion.EstadoMotorInvalidoException;

/**
 * Estado del Motor Warp (patrón State, E1-02). Cada estado decide qué
 * transiciones son válidas:
 * las válidas cambian el estado del motor y las inválidas se rechazan con
 * {@link EstadoMotorInvalidoException}, sin cambiar el estado.
 *
 * Las acciones declaran la excepción porque una implementación no puede
 * declarar más excepciones
 * comprobadas que la interfaz (apunte de Excepciones, "Cláusula throws y
 * redefinición de métodos").
 */
public interface State {

    /**
     * Disponible -> Preparando salto.
     *
     * @throws EstadoMotorInvalidoException si la transición no es válida en este
     *                                      estado; el estado no cambia.
     */
    void prepararSalto() throws EstadoMotorInvalidoException;

    /**
     * Preparando salto -> En warp.
     *
     * @throws EstadoMotorInvalidoException si la transición no es válida en este
     *                                      estado; el estado no cambia.
     */
    void iniciarWarp() throws EstadoMotorInvalidoException;

    /**
     * En warp -> Enfriamiento.
     *
     * @throws EstadoMotorInvalidoException si la transición no es válida en este
     *                                      estado; el estado no cambia.
     */
    void desactivarWarp() throws EstadoMotorInvalidoException;

    /**
     * Enfriamiento -> Disponible.
     *
     * @throws EstadoMotorInvalidoException si la transición no es válida en este
     *                                      estado; el estado no cambia.
     */
    void enfriar() throws EstadoMotorInvalidoException;

    /**
     * Consulta si el motor puede iniciar una nueva operación (Aclaración, R4:
     * antes de actuar, una misión debe verificar que la nave esté disponible).
     * Cada estado responde por sí mismo, sin condicionales en el motor ni en la
     * nave.
     *
     * @return true sólo en el estado Disponible.
     */
    boolean estaDisponible();

    /**
     * Salto normal: avance de 1 casillero.
     * No se ve afectado por el estado del motor warp ni lanza excepcion de estado
     * invalido.
     */
    default void saltar() throws EstadoMotorInvalidoException {
        // Por defecto, un salto comun no altera el motor ni se bloquea.
    }

    /**
     * Salto Warp: impulso para esquivar obstaculos avanzando 2 casilleros.
     */
    default void saltarWarp() throws EstadoMotorInvalidoException {
        prepararSalto();
    }
}
