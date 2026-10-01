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
     * @param liquidacion Objeto que implementa la interfase Liquidacion
     */
    public Decorator(Liquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    
    /**
     * Metodo que retorna el obejeto a ser decorado.
     * @return Objeto a ser decorado.
     */
    public Liquidacion getLiquidacion() {
        return liquidacion;
    }
    /**
     * Metodo que setea el objeto a ser decorado.
     * @param Objeto que implementa Liquidacion que se guarda para ser decorado.
     */
    public void setLiquidacion(Liquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }
    
    
    
    /**
     * Calcula y retorna el sueldo adicional correspondiente del tripulante.
     * @return monto de sueldo adicional.
     */
    @Override
    public double getAdicionalAntiguedad() {
        return this.liquidacion.getAdicionalAntiguedad();
    }
    /**
     * Consulta y retorna el origen correspondiente al tripulante.
     * @return el origen del tripulante
     */
    @Override
    public Origenes getOrigen() {
        return this.liquidacion.getOrigen();
    }
}
