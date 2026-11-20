package modelo.mision;

/**
 * Balance de recursos de una misión (Enunciado E2, Fase 3: "el balance detallado de los recursos ganados,
 * consumidos o perdidos durante el trayecto"), como datos y no como texto, para mostrarlo por consola o en Swing
 * sin cambiar el modelo.
 *
 * <ul>
 *   <li><b>Consumidos:</b> lo que la misión gastó por decisión propia (Tabla de Recursos, IV.5; Anexo): el consumo
 *       inicial del despegue, escaneos de largo y corto alcance, recálculos y saltos del Motor Warp.</li>
 *   <li><b>Perdidos:</b> lo que quitaron los elementos del universo al coincidir con la nave (asteroides).</li>
 *   <li><b>Ganados:</b> lo que aportaron los elementos del universo (estaciones de carga).</li>
 *   <li><b>Resultado de la misión:</b> gemas obtenidas (M-02), suministros a entregar (M-01) y recarga de
 *       combustible y energía y reparación del desgaste en la base (M-03, Anexo). Se informan en la evaluación y
 *       se aplican a la nave en el cierre (Fase 4).</li>
 * </ul>
 *
 * Es inmutable y sólo lo crea {@link Mision} (constructor de paquete).
 *
 * Invariante: todos los valores son mayores o iguales a 0.
 */
public final class BalanceRecursos {

    private final int combustibleConsumido;
    private final int energiaConsumida;
    private final int desgasteProducido;
    private final int combustiblePerdido;
    private final int energiaPerdida;
    private final int combustibleGanado;
    private final int energiaGanada;
    private final int gemasObtenidas;
    private final int combustibleEntregado;
    private final int energiaEntregada;
    private final int combustibleRecargado;
    private final int energiaRecargada;
    private final int desgasteReparado;

    /**
     * @pre Todos los valores son mayores o iguales a 0. Los calcula la propia misión, por eso un valor inválido
     *      sólo puede deberse a un error de programación y se verifica con aserciones.
     * @post El balance queda con los valores recibidos y no puede modificarse.
     */
    BalanceRecursos(int combustibleConsumido, int energiaConsumida, int desgasteProducido,
                    int combustiblePerdido, int energiaPerdida, int combustibleGanado, int energiaGanada,
                    int gemasObtenidas, int combustibleEntregado, int energiaEntregada,
                    int combustibleRecargado, int energiaRecargada, int desgasteReparado) {
        this.combustibleConsumido = combustibleConsumido;
        this.energiaConsumida = energiaConsumida;
        this.desgasteProducido = desgasteProducido;
        this.combustiblePerdido = combustiblePerdido;
        this.energiaPerdida = energiaPerdida;
        this.combustibleGanado = combustibleGanado;
        this.energiaGanada = energiaGanada;
        this.gemasObtenidas = gemasObtenidas;
        this.combustibleEntregado = combustibleEntregado;
        this.energiaEntregada = energiaEntregada;
        this.combustibleRecargado = combustibleRecargado;
        this.energiaRecargada = energiaRecargada;
        this.desgasteReparado = desgasteReparado;
        assert invariante() : "Fallo invariante: el balance de recursos no admite valores negativos.";
    }

    public int getCombustibleConsumido() {
        return combustibleConsumido;
    }

    public int getEnergiaConsumida() {
        return energiaConsumida;
    }

    public int getDesgasteProducido() {
        return desgasteProducido;
    }

    public int getCombustiblePerdido() {
        return combustiblePerdido;
    }

    public int getEnergiaPerdida() {
        return energiaPerdida;
    }

    public int getCombustibleGanado() {
        return combustibleGanado;
    }

    public int getEnergiaGanada() {
        return energiaGanada;
    }

    /**
     * @return gemas obtenidas al llegar al objetivo (M-02); se acreditan en el cierre.
     */
    public int getGemasObtenidas() {
        return gemasObtenidas;
    }

    /**
     * @return combustible a entregar a la nave aliada (M-01); se descuenta en el cierre.
     */
    public int getCombustibleEntregado() {
        return combustibleEntregado;
    }

    /**
     * @return energía a entregar a la nave aliada (M-01); se descuenta en el cierre.
     */
    public int getEnergiaEntregada() {
        return energiaEntregada;
    }

    /**
     * @return combustible recargado en la base (M-03): lo que falta para la capacidad máxima; se aplica en el cierre.
     */
    public int getCombustibleRecargado() {
        return combustibleRecargado;
    }

    /**
     * @return energía recargada en la base (M-03): lo que falta para la capacidad máxima; se aplica en el cierre.
     */
    public int getEnergiaRecargada() {
        return energiaRecargada;
    }

    /**
     * @return desgaste reparado en la base (M-03): todo el desgaste al llegar; se aplica en el cierre.
     */
    public int getDesgasteReparado() {
        return desgasteReparado;
    }

    private boolean invariante() {
        return combustibleConsumido >= 0 && energiaConsumida >= 0 && desgasteProducido >= 0
                && combustiblePerdido >= 0 && energiaPerdida >= 0
                && combustibleGanado >= 0 && energiaGanada >= 0
                && gemasObtenidas >= 0 && combustibleEntregado >= 0 && energiaEntregada >= 0
                && combustibleRecargado >= 0 && energiaRecargada >= 0 && desgasteReparado >= 0;
    }

    @Override
    public String toString() {
        return "consumidos (combustible " + combustibleConsumido + ", energía " + energiaConsumida
                + ", desgaste " + desgasteProducido + "); perdidos (combustible " + combustiblePerdido
                + ", energía " + energiaPerdida + "); ganados (combustible " + combustibleGanado
                + ", energía " + energiaGanada + "); gemas " + gemasObtenidas + "; entregados (combustible "
                + combustibleEntregado + ", energía " + energiaEntregada + "); recarga en la base (combustible "
                + combustibleRecargado + ", energía " + energiaRecargada + ", desgaste reparado " + desgasteReparado
                + ")";
    }
}
