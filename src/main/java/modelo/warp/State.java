package modelo.warp;

import excepcion.EstadoMotorInvalidoException;
import excepcion.RecursoInsuficienteException;

/**
 * Estado del Motor Warp (patrón State; Anexo, "Navegación y Motor Warp").
 *
 * Cada estado decide qué hacer con las dos operaciones del motor, sin condicionales en el motor:
 *  - {@link #saltar()}: salto normal, avanza 1 casillero;
 *  - {@link #saltarWarp()}: avanza un turno de la maniobra warp (preparar, saltar 2 casilleros o enfriar).
 *
 * Ambas devuelven cuántos casilleros debe avanzar la nave en este turno (0, 1 o 2): el motor no conoce el
 * tablero, sólo informa el desplazamiento que corresponde. Los costos se cobran en el mismo paso en que el
 * estado cambia, mediante el {@link SuministroMotor} del motor.
 *
 * Las operaciones declaran todas las excepciones que puede lanzar algún estado, porque una implementación no
 * puede declarar más excepciones comprobadas que la interfaz (apunte de Excepciones, "Cláusula throws y
 * redefinición de métodos").
 */
public interface State {

    /**
     * Salto normal: avanza 1 casillero y consume combustible.
     *
     * @return casilleros que avanza la nave.
     * @throws EstadoMotorInvalidoException si el motor no está Disponible; el motor y la nave no cambian.
     * @throws RecursoInsuficienteException si no alcanza el combustible; el motor y la nave no cambian.
     */
    int saltar() throws EstadoMotorInvalidoException, RecursoInsuficienteException;

    /**
     * Avanza un turno de la maniobra warp. Siempre es válida: cada estado sabe qué paso de la maniobra le toca.
     *
     * @return casilleros que avanza la nave (2 sólo en el turno del salto; 0 en los demás).
     */
    int saltarWarp();

    /**
     * Consulta si el motor puede iniciar una nueva operación (Aclaración, R4: antes de actuar, una misión
     * verifica que la nave esté disponible). Cada estado responde por sí mismo.
     *
     * @return true sólo en el estado Disponible.
     */
    boolean estaDisponible();
}
