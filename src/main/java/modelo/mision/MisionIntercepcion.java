package modelo.mision;

import java.util.Random;
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
    private final int MAXIMO_TRANSFERIR = 50;
    private final int MINIMO_TRANSFERIR = 0;

    private boolean asistenciaRealizada;
    private int combustibleATransferir;
    private int energiaATransferir;

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
    	Random random = new Random();
    	this.combustibleATransferir = random.nextInt((this.MAXIMO_TRANSFERIR - this.MINIMO_TRANSFERIR) + 1) + this.MINIMO_TRANSFERIR;
    	this.energiaATransferir = random.nextInt((this.MAXIMO_TRANSFERIR - this.MINIMO_TRANSFERIR) + 1) + this.MINIMO_TRANSFERIR;
    	if(!this.getAsistente().getPosicionNave().equals(destino)) {
    		this.combustibleATransferir = this.energiaATransferir = 0;
    		asistenciaRealizada = false;
    		registrarAccion("Ejecución: No llegó al objetivo.");
    	}
    	else if (this.getAsistente().getCombustible() >= this.combustibleATransferir && this.getAsistente().getEnergia() >= this.energiaATransferir) {
    		asistenciaRealizada = true;
    		registrarAccion("Ejecución: llegó al objetivo simulado y realizó la asistencia.");
    	}
    	else {
    		this.combustibleATransferir = this.energiaATransferir = 0;
    		asistenciaRealizada = false;
    		registrarAccion("Ejecución: No tuvo recursos suficientes para la asistencia.");
    	}
    }

    @Override
    protected boolean objetivoCumplido() {
        return asistenciaRealizada;
    }

    @Override
    public int HookCombustibleTransferido() {
		return combustibleATransferir;
	}

    @Override
	public int HookEnergiaTransferida() {
		return energiaATransferir;
	}

	@Override
    protected String getCondicionDeExito() {
        return "la asistencia se completa con recursos suficientes";
    }
}
