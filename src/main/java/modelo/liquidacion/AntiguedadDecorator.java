/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.liquidacion;

/**
 * Decorador concreto que agrega el adicional por antiguedad al sueldo del tripulante.
 *
 * @author Sebastian
 */
public class AntiguedadDecorator extends Decorator{
    
    /**
     * Constructor que solo recibe el objeto a envolver.
     * 
     * @pre liquidacion != null.
     * @post Se crea el decorador con la referencia al componente envuelto.
     * @param liquidacion Objeto que implementa la interfase Liquidacion
     */
    public AntiguedadDecorator(Liquidacion liquidacion) {
        super(liquidacion);
    }

    /**
     * Calcula el sueldo acumulado sumando el adicional por antiguedad
     * al sueldo base obtenido del componente envuelto.
     * 
     * @pre El componente envuelto (liquidacion) es distinto de null.
     * @pre El sueldo base del componente envuelto es mayor o igual a 0.
     * @post El valor retornado es mayor o igual al sueldo base del componente envuelto.
     * @post El valor retornado es igual a sueldo_base + adicional_por_antiguedad.
     * @return monto del sueldo acumulado con el adicional por antiguedad.
     */
    @Override
    public double calcularSueldo() {
        // Obtiene el objeto a ser envuelto por esta capa
        Liquidacion liqBase = super.getLiquidacion();
        assert liqBase != null : "La referencia a 'Liquidacion' no puede ser null";
        
        // Obtiene el haber acumulado de las capas internas
        double haberBase = liqBase.calcularSueldo();
        assert haberBase >= 0 : "El haber base acumulado no puede ser negativo";
        
        // Consulta el adicional correspondiente por año de antiguedad
        double adicional = this.getAdicionalAntiguedad();
        assert adicional >= 0 : "El adicional no puede ser negativo";

        // Retorna el acumulado con el adicional de antiguedad
        return haberBase + adicional;
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
        return super.getAdicionalAntiguedad();
    }
}
