package modelo.mision;

/**
 * M-03 — Retorno seguro, versión simplificada de la Entrega 1 (Ficha de Inicio, puntos 4 y 5).
 * Acción: completar el regreso simulado.
 * Condición de éxito: la nave finaliza en estado operativo válido.
 * Costo: sólo el común de toda misión (no consume energía adicional).
 *
 * A diferencia de M-01 y M-02, su éxito depende del estado en que queda la nave: si el desgaste
 * de la misión la lleva al umbral de mantenimiento, la nave ya no está lista para operar y la misión
 * no es exitosa.
 *
 * Sólo redefine sus pasos particulares; el ciclo lo fija {@link Mision#realizar()} (Template Method).
 */
public class MisionRetorno extends Mision {

    private static final int ENERGIA_ADICIONAL = 0;

    /**
     * @post La misión queda creada.
     */
    public MisionRetorno() {
        super("M-03", "Retorno seguro");
    }

    @Override
    protected int getEnergiaAdicional() {
        return ENERGIA_ADICIONAL;
    }

    @Override
    protected void realizarAccion() {
        registrarAccion("Ejecución: completó el regreso simulado a la zona designada.");
    }

    /**
     * @return true si, después del regreso, la nave sigue lista para operar.
     */
    @Override
    protected boolean objetivoCumplido() {
        return getAsistente().naveListaParaOperar();
    }

    @Override
    protected String getCondicionDeExito() {
        return "la nave finaliza en estado operativo válido";
    }
}
