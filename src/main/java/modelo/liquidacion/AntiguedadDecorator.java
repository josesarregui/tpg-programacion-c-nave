/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.liquidacion;

/**
 *
 * @author Sebastian
 */
public class AntiguedadDecorator extends Decorator{
    
    /**
     * Constructor que solo recibe el objeto a envolver.
     * @param liquidacion Objeto que implementa Liquidacion
     */
    public AntiguedadDecorator(Liquidacion liquidacion) {
        super(liquidacion);
    }

    @Override
    public double calcularSueldo() {
        // Obtiene el haber acumulado de las capas internas
        double haberBase = super.getLiquidacion().calcularSueldo();

        // Consulta los años de antiguedad
        int anios = this.getAntiguedad();
        
        // Consulta el adicional por año de antiguedad
        double adicional = this.getAdicionalCargo();

        //  Calcula la bonificacion segun los años de antiguedad
        adicional = anios * (haberBase * adicional);

        // Retorna el acumulado con el adicional de antiguedad
        return haberBase + adicional;
    }
    
    

    @Override
    public int getAntiguedad() {
        return super.getAntiguedad();
    }

    @Override
    public double getAdicionalCargo() {
        return super.getAdicionalCargo();
    }
}
