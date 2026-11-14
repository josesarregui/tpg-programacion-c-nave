package modelo.warp;

/**
 * Recursos de la nave que el Motor Warp necesita consultar y consumir (Anexo, "Navegación y Motor Warp").
 *
 * El motor depende de esta interfaz y no de la nave (inversión de dependencias): así puede probarse con un
 * suministro de prueba y no conoce nada más de la nave. La nave la implementa con una clase interna privada,
 * de modo que estas operaciones de consumo no quedan públicas en la nave.
 *
 * Cada estado del motor verifica que alcance el recurso antes de pedir el consumo, en el mismo paso en que
 * cambia de estado: el cambio de estado y el costo ocurren juntos y nunca queda uno sin el otro.
 */
public interface SuministroMotor {

    /**
     * @return combustible disponible en la nave.
     */
    int getCombustible();

    /**
     * @return energía disponible en la nave.
     */
    int getEnergia();

    /**
     * Descuenta combustible.
     *
     * @pre 0 <= cantidad <= getCombustible() (el estado lo verifica antes de pedirlo).
     * @post getCombustible() == combustible anterior - cantidad.
     */
    void consumirCombustible(int cantidad);

    /**
     * Descuenta energía.
     *
     * @pre 0 <= cantidad <= getEnergia() (el estado lo verifica antes de pedirlo).
     * @post getEnergia() == energía anterior - cantidad.
     */
    void consumirEnergia(int cantidad);

    /**
     * Suma desgaste a la nave. Si la suma supera el máximo, el desgaste queda en el máximo: alcanzar el máximo
     * es una consecuencia del recorrido (la nave se destruye, Enunciado E2), no una operación que se rechace.
     *
     * @pre cantidad >= 0.
     * @post el desgaste aumentó en cantidad, sin superar el máximo.
     */
    void sumarDesgaste(int cantidad);
}
