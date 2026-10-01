/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package modelo.tripulacion;

/**
 *
 * @author Sebastian
 */
public enum Origenes {
    TERRICOLA(0.0),   // 0% de bonificacion por ser de la Tierra
    VULCANO(0.10),    // 10% de bonificacion por ser de Vulcano
    MARCIANO(0.15);   // 15% de bonificacion por ser de Marte
    private final double porcentajeBono;

    
    
    /**
     * Constructor de Origenes para asociar cada origen con su porcentaje.
     */
    Origenes(double porcentajeBono) {
        this.porcentajeBono = porcentajeBono;
    }

    
    /**
     * Consulta el porsentaje del bono segun el origen.
     * @return Porsentaje de bono correspondinte segun el origen.
     */
    public double getPorcentajeBono() {
        return this.porcentajeBono;
    }

    /**
     * Calcula el monto del bono segun el haber recibido.
     * @param haberBase Sueldo sobre el cual se aplica el porcentaje
     * @return Valor del adicional por origen
     */
    public double calcularBonoPorOrigen(double haberBase) {
        return haberBase * this.porcentajeBono;
    }
    
}