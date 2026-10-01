package modelo.warp;

import excepcion.EstadoMotorInvalidoException;
import java.util.Objects;

/**
 * Representa la fase en la cual el motor disipa el calor residual
 * generado durante el viaje a velocidad Warp.
 * La única acción válida es completar el enfriamiento térmico,
 * lo que restablece el motor al estado inicial Disponible.
 */
public class EnfriamientoState implements State {

    private final MotorWarp motor;

    public EnfriamientoState(MotorWarp motor) {
        this.motor = Objects.requireNonNull(motor, "El motor no puede ser nulo.");
    }

    @Override
    public void prepararSalto() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede preparar un nuevo salto mientras el motor disipa calor en enfriamiento."
        );
    }

    @Override
    public void iniciarWarp() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: Intento crítico de salto. El motor se encuentra sobrecalentado en fase de enfriamiento."
        );
    }

    @Override
    public void desactivarWarp() {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: El salto Warp ya fue desactivado previamente."
        );
    }

    @Override
    public void enfriar() {
        // Transición válida: disipación térmica completada con éxito.
        // El ciclo se cierra y el motor vuelve a estar operativo.
        this.motor.setEstado(new DisponibleState(this.motor));
    }
}