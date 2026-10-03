package modelo.warp;

import java.util.Objects;

/**
 * Motor Warp de la nave: contexto del patrón State (E1-02).
 * Delega cada acción en su estado actual, que decide si la transición es válida;
 * así el motor no necesita condicionales sobre el estado en el que se encuentra.
 *
 * Invariante: estado != null. El motor se crea en estado Disponible (Ficha de Inicio, punto 2).
 */
public class MotorWarp {

    private State estado;

    /**
     * @post getEstado() es una instancia de DisponibleState.
     */
    public MotorWarp() {
        estado = new DisponibleState(this);
    }

    public State getEstado() {
        return estado;
    }

    /**
     * Cambia el estado del motor. Es de paquete: sólo los estados (mismo paquete) lo invocan al
     * completar una transición válida, de modo que ningún cliente puede saltear el ciclo de estados.
     *
     * @pre nuevoEstado != null.
     * @post getEstado() == nuevoEstado.
     */
    void setEstado(State nuevoEstado) {
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

    /**
     * Consulta delegada al estado actual: el motor no necesita preguntar en qué estado está.
     *
     * @return true si el motor está en Disponible y puede iniciar una nueva operación.
     */
    public boolean estaDisponible() {
        return estado.estaDisponible();
    }

    @Override
    public String toString() {
        return "MotorWarp [estado=" + estado + "]";
    }
}
