/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.liquidacion;

import modelo.tripulacion.Origenes;

/**
 *
 * @author Sebastian
 */
public abstract class Decorator implements Liquidacion{
    // Atributo protegido para que los decoradores concretos puedan acceder a el
    protected Liquidacion liquidacion;

    /**
     * Constructor que recibe el componente a envolver.
     * @param liquidacion Objeto que implementa Liquidacion
     */
    public Decorator(Liquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    
    
    public Liquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(Liquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }
    
    
    @Override
    public int getConsejos() {
        return this.liquidacion.getConsejos();
    }
    
    @Override
    public int getAntiguedad() {
        return this.liquidacion.getAntiguedad();
    }

    @Override
    public Origenes getOrigen() {
        return this.liquidacion.getOrigen();
    }

    @Override
    public double getAdicionalCargo() {
        return this.liquidacion.getAdicionalCargo();
    }
}
