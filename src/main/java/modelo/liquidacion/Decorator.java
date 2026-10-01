package modelo.liquidacion;

import excepcion.TripulacionInvalidaException;
import modelo.tripulacion.Origen;

/**
 * Decorador Abstracto Base del patrón Decorator.
 * Proporciona una alternativa flexible a la herencia para extender la liquidación de haberes.
 */
public abstract class Decorator implements Liquidacion {

    protected Liquidacion trip;

    public Decorator(Liquidacion trip) {
        this.setLiquidacion(trip);
        assert invariante() : "Fallo invariante: El decorador carece de componente base."; //
    }

    private boolean invariante() {
        return this.trip != null;
    }

    public Liquidacion getLiquidacion() {
        return this.trip;
    }

    public void setLiquidacion(Liquidacion liq) {
        if (liq == null) {
            throw new TripulacionInvalidaException("El cálculo de haberes no aceptará conceptos nulos.");
        }
        this.trip = liq;
    }

    @Override public double calcularSueldo() { return trip.calcularSueldo(); }
    @Override public int getAntiguedad() { return trip.getAntiguedad(); }
    @Override public double getAdicionalCargo() { return trip.getAdicionalCargo(); }
    @Override public Origen getOrigen() { return trip.getOrigen(); }
    @Override public double getSUELDOBASE() { return trip.getSUELDOBASE(); }
}
