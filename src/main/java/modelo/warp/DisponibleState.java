package modelo.warp;

import excepcion.EstadoMotorInvalidoException;

/**
 * Estado inicial del motor (Ficha de Inicio, punto 2): el motor está libre y la nave puede iniciar
 * una nueva operación. La única acción válida es preparar el salto; las demás se rechazan
 * con {@link EstadoMotorInvalidoException} sin cambiar el estado.
 */
public class DisponibleState implements State {

    private static final String NOMBRE = "Disponible";

    private final MotorWarp motor;

    /**
     * Constructor de paquete: sólo lo usan el motor y los demás estados al completar una transición válida.
     * Así ningún cliente puede crear un estado para forzar un cambio que saltee el ciclo (E1-02).
     *
     * @pre motor != null.
     */
    DisponibleState(MotorWarp motor) {
        assert motor != null : "El estado debe pertenecer a un motor.";
        this.motor = motor;
    }

    @Override
    public void prepararSalto() {
        // Única transición válida
        this.motor.setEstado(new PreparandoSaltoState(this.motor));
    }

    @Override
    public void iniciarWarp() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede entrar a Warp directamente sin preparar el salto previo.",
                NOMBRE
        );
    }

    @Override
    public void desactivarWarp() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede desactivar Warp porque el motor no está en hiperespacio.",
                NOMBRE
        );
    }

    @Override
    public void enfriar() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: El motor ya se encuentra frío y disponible en temperatura nominal.",
                NOMBRE
        );
    }

    @Override
    public boolean estaDisponible() {
        return true;
    }

    @Override
    public String toString() {
        return NOMBRE;
    }
}
