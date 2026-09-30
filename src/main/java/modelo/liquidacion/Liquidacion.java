/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modelo.liquidacion;

import modelo.tripulacion.Origenes;

/**
 *
 * @author Sebastian
 */
public interface Liquidacion {
    
    /**
     * Calcula y retorna el sueldo (o acumulado con adicionales) del tripulante.
     * @return monto del sueldo acumulado
     */
    public double calcularSueldo();
    
    
    public int getConsejos();
    
    
    public int getAntiguedad();
    public double getAdicionalCargo();
    
    
    public Origenes getOrigen();
    
}
