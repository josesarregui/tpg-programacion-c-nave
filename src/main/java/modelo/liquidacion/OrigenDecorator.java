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
public class OrigenDecorator extends Decorator{
    /**
     * Constructor que solo recibe el objeto a decorar.
     * @param liquidacion Objeto que implementa la interfase Liquidacion
     */
    public OrigenDecorator(Liquidacion liquidacion) {
        super(liquidacion);
    }

    
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
     * @return el origen del tripulante
     */
    @Override
    public Origenes getOrigen() {
        return super.getOrigen();
    }
}
