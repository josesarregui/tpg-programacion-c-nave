package modelo.warp;

import java.util.Objects;

public class MotorWarp {

    private State estado;

    public MotorWarp() {
        estado = new DisponibleState(this);
    }

    public State getEstado() {
        return estado;
    }

    public void setEstado(State nuevoEstado) {
        estado = Objects.requireNonNull(nuevoEstado, "El nuevo estado del motor no puede ser nulo.");
    }

    // =========================================================================
    // Métodos de acción delegados al estado actual (Patrón State)
    // =========================================================================


    public void prepararSalto() {
        estado.prepararSalto();
    }

    public void iniciarWarp() {
        estado.iniciarWarp();
    }

    public void desactivarWarp() {
        estado.desactivarWarp();
    }

    public void enfriar() {
        estado.enfriar();
    }

    @Override
    public String toString() {
        return "MotorWarp [estado=" + estado.getClass().getSimpleName() + "]";
    }
}