/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modelo.liquidacion;

import modelo.tripulacion.Origenes;

/**
 * Interfaz que define el contrato de liquidacion de sueldos para tripulantes.
 * Utiliza el patron Decorator para componer capas de calculo salarial.
 *
 * @author Sebastian
 */
public interface Liquidacion {
    
    /**
     * Calcula y retorna el sueldo (o acumulado con adicionales) del tripulante.
     * 
     * @pre El objeto que implementa esta interfaz debe estar correctamente inicializado.
     * @post El valor retornado es mayor o igual a 0.
     * @return monto del sueldo acumulado
     */
    public double calcularSueldo();
    
    
    /**
     * Calcula y retorna el sueldo adicional correspondiente del tripulante.
     * 
     * @pre El objeto que implementa esta interfaz debe estar correctamente inicializado.
     * @post El valor retornado es mayor o igual a 0.
     * @return monto de sueldo adicional.
     */
    public double getAdicionalAntiguedad();
    /**
     * Consulta y retorna el origen correspondiente al tripulante.
     * 
     * @pre El objeto que implementa esta interfaz debe estar correctamente inicializado.
     * @post El valor retornado es distinto de null y pertenece al enum {@link Origenes}.
     * @return el origen del tripulante
     */
    public Origenes getOrigen();
}
