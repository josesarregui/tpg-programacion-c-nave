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
     * @param liquidacion Objeto que implementa la interfase Liquidacion
     */
    public AntiguedadDecorator(Liquidacion liquidacion) {
        super(liquidacion);
    }

    @Override
    public double calcularSueldo() {
        // Obtiene el objeto a ser envuelto por esta capa
        Liquidacion liqBase = super.getLiquidacion();
        assert liqBase != null : "Error de Invariante: La referencia a 'Liquidacion' no puede ser null";
        
        // Obtiene el haber acumulado de las capas internas
        double haberBase = liqBase.calcularSueldo();
        assert haberBase >= 0 : "Invariante violado: El haber base acumulado no puede ser negativo";
        
        // Consulta el adicional correspondiente por año de antiguedad
        double adicional = this.getAdicionalAntiguedad();
        assert adicional >= 0 : "Invariante violado: El adicional no puede ser negativo";

        // Retorna el acumulado con el adicional de antiguedad
        return haberBase + adicional;
    }
    
    /**
     * Calcula y retorna el sueldo adicional correspondiente del tripulante.
     * @return monto de sueldo adicional.
     */
    @Override
    public double getAdicionalAntiguedad() {
        return super.getAdicionalAntiguedad();
    }
}
