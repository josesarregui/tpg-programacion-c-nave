package modelo.warp;

import excepcion.EstadoMotorInvalidoException;
import java.util.Objects;


public class DisponibleState implements State {

    private static final String NOMBRE = "Disponible";

    private final MotorWarp motor;

    public DisponibleState(MotorWarp motor) {
        this.motor = Objects.requireNonNull(motor, "El motor no puede ser nulo.");
    }

    @Override
    public void prepararSalto() {
        // Unica Transición válida
        this.motor.setEstado(new PreparandoSaltoState(this.motor));
    }

    @Override
    public void iniciarWarp() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede entrar a Warp directamente sin preparar el salto previo.",
                NOMBRE
        );
    }

    @Override
    public void desactivarWarp() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede desactivar Warp porque el motor no está en hiperespacio.",
                NOMBRE
        );
    }

    @Override
    public void enfriar() {
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
