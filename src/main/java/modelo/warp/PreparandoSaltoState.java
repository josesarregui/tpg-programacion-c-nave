package modelo.warp;

import excepcion.EstadoMotorInvalidoException;

/**
 * Representa la fase en la que el motor está presurizando reactores
 * y calculando coordenadas hiperespaciales.
 * En este estado, la única acción válida es iniciar el salto warp.
 */

public class PreparandoSaltoState implements State {

    private static final String NOMBRE = "Preparando salto";

    private final MotorWarp motor;

    /**
     * Constructor de paquete: sólo lo usan el motor y los demás estados al completar una transición válida.
     * Así ningún cliente puede crear un estado para forzar un cambio que saltee el ciclo (E1-02).
     *
     * @pre motor != null.
     */
    PreparandoSaltoState(MotorWarp motor) {
        assert motor != null : "El estado debe pertenecer a un motor.";
        this.motor = motor;
    }

    @Override
    public void prepararSalto() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: La secuencia de preparación ya se encuentra en curso.",
                NOMBRE
        );
    }

    @Override
    public void iniciarWarp() {
        // Transición válida: el salto se hace efectivo y entra en hiperespacio
        this.motor.setEstado(new EnWarpState(this.motor));
    }

    @Override
    public void desactivarWarp() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede desactivar Warp porque el salto aún no fue ejecutado.",
                NOMBRE
        );
    }

    @Override
    public void enfriar() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede enfriar el motor mientras se prepara un salto.",
                NOMBRE
        );
    }

    @Override
    public boolean estaDisponible() {
        return false;
    }

    @Override
    public String toString() {
        return NOMBRE;
    }
}
