package modelo.warp;

import excepcion.EstadoMotorInvalidoException;
import java.util.Objects;

/**
 * Representa la fase en la cual la nave se encuentra navegando en hiperespacio (Warp).
 * En este estado, los reactores operan a máxima potencia de distorsión.
 * La única acción válida es desactivar Warp para salir del hiperespacio
 * e iniciar la fase de enfriamiento térmico.
 */

public class EnWarpState implements State {

    private static final String NOMBRE = "En warp";

    private final MotorWarp motor;

    public EnWarpState(MotorWarp motor) {
        this.motor = Objects.requireNonNull(motor, "El motor no puede ser nulo.");
    }

    @Override
    public void prepararSalto() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede preparar un salto mientras la nave ya está navegando en Warp.",
                NOMBRE
        );
    }

    @Override
    public void iniciarWarp() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: El motor ya se encuentra activo en velocidad Warp.",
                NOMBRE
        );
    }

    @Override
    public void desactivarWarp() {
        // Transición válida: salida del hiperespacio hacia enfriamiento térmico
        this.motor.setEstado(new EnfriamientoState(this.motor));
    }

    @Override
    public void enfriar() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede enfriar el motor mientras el campo Warp continúe activo. Debe desactivarse primero.",
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
