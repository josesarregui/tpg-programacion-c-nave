/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.liquidacion;

import modelo.tripulacion.Origenes;

/**
 * Clase abstracta que actua como base del patron Decorator para la liquidacion.
 * Envuelve un objeto {@link Liquidacion} y delega las operaciones al componente interno.
 *
 * @author Sebastian
 */
public abstract class Decorator implements Liquidacion{
    // Atributo protegido para que los decoradores concretos puedan acceder a el
    protected Liquidacion liquidacion;

    /**
     * Constructor que recibe el componente a envolver.
     * 
     * @pre liquidacion != null.
     * @post Se almacena la referencia al componente envuelto.
     * @param liquidacion Objeto que implementa la interfase Liquidacion
     */
    public Decorator(Liquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    
    /**
     * Metodo que retorna el obejeto a ser decorado.
     * 
     * @pre El decorador fue correctamente inicializado con un componente valido.
     * @post El valor retornado es distinto de null.
     * @return Objeto a ser decorado.
     */
    public Liquidacion getLiquidacion() {
        return liquidacion;
    }
    /**
     * Metodo que setea el objeto a ser decorado.
     * 
     * @pre liquidacion != null.
     * @post Se actualiza la referencia al componente envuelto.
     * @param liquidacion Objeto que implementa Liquidacion que se guarda para ser decorado.
     */
    public void setLiquidacion(Liquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }
    
    
    
    /**
     * Calcula y retorna el sueldo adicional correspondiente del tripulante.
     * Delega al componente envuelto.
     * 
     * @pre El componente envuelto (liquidacion) es distinto de null.
     * @post El valor retornado es mayor o igual a 0.
     * @return monto de sueldo adicional.
     */
    @Override
    public double getAdicionalAntiguedad() {
        return this.liquidacion.getAdicionalAntiguedad();
    }
    /**
     * Consulta y retorna el origen correspondiente al tripulante.
     * Delega al componente envuelto.
     * 
     * @pre El componente envuelto (liquidacion) es distinto de null.
     * @post El valor retornado es distinto de null y pertenece al enum {@link Origenes}.
     * @return el origen del tripulante
     */
    @Override
    public Origenes getOrigen() {
        return this.liquidacion.getOrigen();
    }
}
