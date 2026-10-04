package excepcion;

/**
 * Excepción lanzada cuando se intenta una transición no permitida en el estado actual
 * del Motor Warp (E1-02: el motor no acepta transiciones inválidas silenciosas).
 * Guarda el nombre del estado en el que se rechazó la transición, con su getter,
 * para que el invocante pueda registrarlo en la Bitácora (Escenario C).
 *
 * Extiende IllegalStateException (no comprobada). Ver la decisión pendiente en docs/diseño.md, sección 4.
 */
public class EstadoMotorInvalidoException extends IllegalStateException {

    private final String estadoActual;

    public EstadoMotorInvalidoException(String mensaje, String estadoActual) {
        super(mensaje);
        this.estadoActual = estadoActual;
    }

    public String getEstadoActual() {
        return estadoActual;
    }
}
