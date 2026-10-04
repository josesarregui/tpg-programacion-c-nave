package modelo.mision;

/**
 * M-01 — Intercepción y asistencia, versión simplificada de la Entrega 1 (Ficha de Inicio, puntos 4 y 5).
 * Acción: llegar al objetivo simulado y realizar la asistencia.
 * Condición de éxito: la asistencia se completa con recursos suficientes.
 * Costo: el común de toda misión más 5 de energía.
 *
 * Sólo redefine sus pasos particulares; el ciclo lo fija {@link Mision#realizar()} (Template Method).
 */
public class MisionIntercepcion extends Mision {

    private static final int ENERGIA_ADICIONAL = 5;

    private boolean asistenciaRealizada;

    /**
     * @post La misión queda creada, sin asistencia realizada.
     */
    public MisionIntercepcion() {
        super("M-01", "Intercepción y asistencia");
        this.asistenciaRealizada = false;
    }

    @Override
    protected int getEnergiaAdicional() {
        return ENERGIA_ADICIONAL;
    }

    /**
     * La acción se realiza después de que la misión consumió sus recursos sin rechazos:
     * por eso, si se llega a este paso, la asistencia se completó con recursos suficientes.
     *
     * @post asistenciaRealizada == true.
     */
    @Override
    protected void realizarAccion() {
        asistenciaRealizada = true;
        registrarAccion("Ejecución: llegó al objetivo simulado y realizó la asistencia.");
    }

    @Override
    protected boolean objetivoCumplido() {
        return asistenciaRealizada;
    }

    @Override
    protected String getCondicionDeExito() {
        return "la asistencia se completa con recursos suficientes";
    }
}
