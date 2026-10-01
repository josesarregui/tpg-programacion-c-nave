package modelo.warp;

import excepcion.EstadoMotorInvalidoException;
import java.util.Objects;

/**
 * Representa la fase en la que el motor está presurizando reactores
 * y calculando coordenadas hiperespaciales.
 * En este estado, la única acción válida es iniciar el salto warp.
 */

public class PreparandoSaltoState implements State {

    private final MotorWarp motor;

    public PreparandoSaltoState(MotorWarp motor) {
        this.motor = Objects.requireNonNull(motor, "El motor no puede ser nulo.");
    }

    @Override
    public void prepararSalto() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: La secuencia de preparación ya se encuentra en curso."
        );
    }

    @Override
    public void iniciarWarp() {
        // Transición válida: el salto se hace efectivo y entra en hiperespacio
        this.motor.setEstado(new EnWarpState(this.motor));
    }

    @Override
    public void desactivarWarp() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede desactivar Warp porque el salto aún no fue ejecutado."
        );
    }

    @Override
    public void enfriar() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede enfriar el motor mientras se prepara un salto."
        );
    }
}
