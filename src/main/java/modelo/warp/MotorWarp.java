package modelo.warp;

import excepcion.EstadoMotorInvalidoException;

/**
 * Motor Warp de la nave: contexto del patrón State (E1-02).
 * Delega cada acción en su estado actual, que decide si la transición es válida;
 * así el motor no necesita condicionales sobre el estado en el que se encuentra.
 *
 * Ciclo válido (Escenario C): Disponible -> Preparando salto -> En warp -> Enfriamiento -> Disponible.
 * Una transición inválida se rechaza con {@link EstadoMotorInvalidoException} y el estado no cambia
 * (Observaciones: el Motor Warp no acepta transiciones inválidas silenciosas).
 *
 * Invariante: estado != null. El motor se crea en estado Disponible (Ficha de Inicio, punto 2).
 */
public class MotorWarp {

    private State estado;

    /**
     * @post getEstado() es el estado Disponible (estaDisponible() == true).
     */
    public MotorWarp() {
        estado = new DisponibleState(this);
        assert estaDisponible() : "Fallo postcondición: el motor debe crearse en Disponible.";
        assert invariante() : "Fallo invariante tras crear el motor.";
    }

    public State getEstado() {
        return estado;
    }

    /**
     * Cambia el estado del motor. Es de paquete: sólo los estados (mismo paquete) lo invocan al
     * completar una transición válida, de modo que ningún cliente puede saltear el ciclo de estados.
     * Un estado nulo sólo puede deberse a un error de programación, por eso se verifica con una aserción.
     *
     * @pre nuevoEstado != null.
     * @post getEstado() == nuevoEstado.
     */
    void setEstado(State nuevoEstado) {
        assert nuevoEstado != null : "El nuevo estado del motor no puede ser nulo.";
        estado = nuevoEstado;
        assert estado == nuevoEstado : "Fallo postcondición al cambiar el estado del motor.";
        assert invariante() : "Fallo invariante tras cambiar el estado del motor.";
    }

    // =========================================================================
    // Métodos de acción delegados al estado actual (Patrón State).
    // Si la transición es válida, el estado actual la realiza y el motor pasa a un estado nuevo.
    // Si no es válida, el estado la rechaza con EstadoMotorInvalidoException, el motor no cambia
    // y la excepción se propaga al invocante (el asistente).
    // =========================================================================

    /**
     * Disponible -> Preparando salto.
     *
     * @post Si no se lanza excepción, el motor pasó a Preparando salto. Si se lanza, el estado no cambió.
     * @throws EstadoMotorInvalidoException si el motor no está en Disponible.
     */
    public void prepararSalto() throws EstadoMotorInvalidoException {
        State estadoAnterior = estado;
        estado.prepararSalto();
        assert estado != estadoAnterior : "Fallo postcondición: la transición válida debe cambiar el estado.";
        assert invariante() : "Fallo invariante tras preparar el salto.";
    }

    /**
     * Preparando salto -> En warp.
     *
     * @post Si no se lanza excepción, el motor pasó a En warp. Si se lanza, el estado no cambió.
     * @throws EstadoMotorInvalidoException si el motor no está en Preparando salto.
     */
    public void iniciarWarp() throws EstadoMotorInvalidoException {
        State estadoAnterior = estado;
        estado.iniciarWarp();
        assert estado != estadoAnterior : "Fallo postcondición: la transición válida debe cambiar el estado.";
        assert invariante() : "Fallo invariante tras iniciar el warp.";
    }

    /**
     * En warp -> Enfriamiento.
     *
     * @post Si no se lanza excepción, el motor pasó a Enfriamiento. Si se lanza, el estado no cambió.
     * @throws EstadoMotorInvalidoException si el motor no está En warp.
     */
    public void desactivarWarp() throws EstadoMotorInvalidoException {
        State estadoAnterior = estado;
        estado.desactivarWarp();
        assert estado != estadoAnterior : "Fallo postcondición: la transición válida debe cambiar el estado.";
        assert invariante() : "Fallo invariante tras desactivar el warp.";
    }

    /**
     * Enfriamiento -> Disponible.
     *
     * @post Si no se lanza excepción, el motor volvió a Disponible. Si se lanza, el estado no cambió.
     * @throws EstadoMotorInvalidoException si el motor no está en Enfriamiento.
     */
    public void enfriar() throws EstadoMotorInvalidoException {
        State estadoAnterior = estado;
        estado.enfriar();
        assert estado != estadoAnterior && estaDisponible() : "Fallo postcondición: al enfriar el motor debe quedar Disponible.";
        assert invariante() : "Fallo invariante tras enfriar el motor.";
    }

    /**
     * Consulta delegada al estado actual: el motor no necesita preguntar en qué estado está.
     *
     * @return true si el motor está en Disponible y puede iniciar una nueva operación.
     */
    public boolean estaDisponible() {
        return estado.estaDisponible();
    }

    private boolean invariante() {
        return estado != null;
    }

    @Override
    public String toString() {
        return "MotorWarp [estado=" + estado + "]";
    }
}
