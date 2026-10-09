package modelo.warp;

import excepcion.EstadoMotorInvalidoException;

/**
 * Representa la fase en la cual el motor disipa el calor residual
 * generado durante el viaje a velocidad Warp.
 * La única acción válida es completar el enfriamiento térmico,
 * lo que restablece el motor al estado inicial Disponible.
 */
public class EnfriamientoState implements State {

    private static final String NOMBRE = "Enfriamiento";

    private final MotorWarp motor;

    /**
     * @pre motor != null (lo crea el propio motor o un estado al completar una transición).
     */
    public EnfriamientoState(MotorWarp motor) {
        assert motor != null : "El estado debe pertenecer a un motor.";
        this.motor = motor;
    }

    @Override
    public void prepararSalto() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: No se puede preparar un nuevo salto mientras el motor disipa calor en enfriamiento.",
                NOMBRE
        );
    }

    @Override
    public void iniciarWarp() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: Intento crítico de salto. El motor se encuentra sobrecalentado en fase de enfriamiento.",
                NOMBRE
        );
    }

    @Override
    public void desactivarWarp() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: El salto Warp ya fue desactivado previamente.",
                NOMBRE
        );
    }

    @Override
    public void enfriar() {
        // Transición válida: disipación térmica completada con éxito.
        // El ciclo se cierra y el motor vuelve a estar operativo.
        this.motor.setEstado(new DisponibleState(this.motor));
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
