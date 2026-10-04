package modelo.mision;

/**
 * M-02 — Recolección, versión simplificada de la Entrega 1 (Ficha de Inicio, puntos 4 y 5).
 * Acción: llegar al punto simulado y obtener datos o una muestra.
 * Condición de éxito: el elemento queda registrado como obtenido.
 * Costo: el común de toda misión más 5 de energía.
 *
 * Sólo redefine sus pasos particulares; el ciclo lo fija {@link Mision#realizar()} (Template Method).
 */
public class MisionRecoleccion extends Mision {

    private static final int ENERGIA_ADICIONAL = 5;

    private boolean elementoObtenido;

    /**
     * @post La misión queda creada, sin elemento obtenido.
     */
    public MisionRecoleccion() {
        super("M-02", "Recolección");
        this.elementoObtenido = false;
    }

    @Override
    protected int getEnergiaAdicional() {
        return ENERGIA_ADICIONAL;
    }

    /**
     * @post elementoObtenido == true y quedó registrado entre las acciones de la misión.
     */
    @Override
    protected void realizarAccion() {
        elementoObtenido = true;
        registrarAccion("Ejecución: llegó al punto simulado y obtuvo la muestra, que quedó registrada como obtenida.");
    }

    @Override
    protected boolean objetivoCumplido() {
        return elementoObtenido;
    }

    @Override
    protected String getCondicionDeExito() {
        return "el elemento queda registrado como obtenido";
    }
}
