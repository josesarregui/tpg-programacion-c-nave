/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.liquidacion;

import modelo.tripulacion.Origenes;

/**
 * Decorador concreto que agrega el adicional por origen al sueldo del tripulante.
 * El porcentaje de bonificacion depende del planeta de origen.
 *
 * @author Sebastian
 */
public class OrigenDecorator extends Decorator{
    /**
     * Constructor que solo recibe el objeto a decorar.
     * 
     * @pre liquidacion != null.
     * @post Se crea el decorador con la referencia al componente envuelto.
     * @param liquidacion Objeto que implementa la interfase Liquidacion
     */
    public OrigenDecorator(Liquidacion liquidacion) {
        super(liquidacion);
    }

    
    /**
     * Calcula el sueldo acumulado sumando el adicional por origen
     * al sueldo base obtenido del componente envuelto.
     * 
     * @pre El componente envuelto (liquidacion) es distinto de null.
     * @pre El sueldo base del componente envuelto es mayor o igual a 0.
     * @pre El origen del tripulante es distinto de null.
     * @post El valor retornado es mayor o igual al sueldo base del componente envuelto.
     * @post El valor retornado es igual a sueldo_base + (sueldo_base * porcentaje_bono_origen).
     * @return monto del sueldo acumulado con el adicional por origen.
     */
    @Override
    public double calcularSueldo() {
        // Obtiene el objeto a ser envuelto por esta capa
        Liquidacion liqBase = super.getLiquidacion();
        assert liqBase != null : "La referencia a 'Liquidacion' no puede ser null";
        
        // Obtiene el haber acumulado de las capas internas
        double haberBase = liqBase.calcularSueldo();
        assert haberBase >= 0 : "El haber base acumulado no puede ser negativo";

        // Consulta el origen del tripulante base
        Origenes origenTripulante = this.getOrigen();
        
        // Aplica la bonificacion correspondiente segun el Origen
        double adicional = origenTripulante.calcularBonoPorOrigen(haberBase);
        assert adicional >= 0 : "El adicional no puede ser negativo";

        // Retorna la suma del sueldo acumulado mas el adicional por origen
        return haberBase + adicional;
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
        return super.getOrigen();
    }
}
