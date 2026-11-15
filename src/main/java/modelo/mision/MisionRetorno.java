package modelo.mision;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
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
    
    private boolean seLlego;

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
    	if (this.getAsistente().getPosicionNave().equals(this.tablero.getObjetivo())) {
    		this.seLlego = true;
    		registrarAccion("Ejecución: completó el regreso simulado a la zona designada.");
    	}
    	else {
    		this.seLlego = false;
    		registrarAccion("Ejecución: No se completó el regreso a la zona designada.");
    	}
    }

    /**
     * @return Se cumple si llego a la estacion.
     */
    @Override
    protected boolean objetivoCumplido() {
    	this.getAsistente().realizarMantenimiento();
    	try {
    		this.getAsistente().cargarCombustible(100 - this.getAsistente().getCombustible());
    		this.getAsistente().cargarEnergia(100-this.getAsistente().getEnergia());
    	}
    	catch(CapacidadExcedidaException e) {
    		
    	}
    	catch(CantidadInvalidaException e) {
    		
    	}
    	return this.seLlego;
    }

    @Override
    protected String getCondicionDeExito() {
        return "la nave finaliza en estado operativo válido";
    }
}
