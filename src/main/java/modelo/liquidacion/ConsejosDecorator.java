/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.liquidacion;

/**
 *
 * @author Sebastian
 */
public class ConsejosDecorator extends Decorator{

    /**
     * Constructor que solo recibe el objeto a envolver.
     * @param liquidacion Objeto que implementa Liquidacion
     */
    public ConsejosDecorator(Liquidacion liquidacion) {
        super(liquidacion);
    }

    
    
    @Override
    public double calcularSueldo() {
        // Obtiene el haber acumulado de las capas internas
        double haberBase = super.getLiquidacion().calcularSueldo();

        // Consulta la cantidad de consejos
        int cantidadConsejos = this.getConsejos();

        // Calcula la bonificación segun la cantidad de consejos
        double adicional = 2 * cantidadConsejos;

        // Retorna el acumulado mas el adicional por consejo
        return haberBase + adicional;
    }


    
    @Override
    public int getConsejos() {
        return super.getLiquidacion().getConsejos();
    }

    
}
