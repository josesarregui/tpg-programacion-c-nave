package modelo.warp;

public interface State {
    void prepararSalto();
    void iniciarWarp();
    void desactivarWarp();
    void enfriar();

    /**
     * Consulta si el motor puede iniciar una nueva operación (Aclaración, R4:
     * antes de actuar, una misión debe verificar que la nave esté disponible).
     * Cada estado responde por sí mismo, sin condicionales en el motor ni en la nave.
     *
     * @return true sólo en el estado Disponible.
     */
    boolean estaDisponible();
}
