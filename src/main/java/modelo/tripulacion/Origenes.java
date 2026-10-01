/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package modelo.tripulacion;

/**
 * Enum que representa los posibles origenes de un tripulante.
 * Cada origen tiene asociado un porcentaje de bonificacion salarial.
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
     * 
     * @pre porcentajeBono >= 0 y porcentajeBono <= 1.
     * @post Se almacena el porcentaje de bonificacion asociado al origen.
     */
    Origenes(double porcentajeBono) {
        this.porcentajeBono = porcentajeBono;
    }

    
    /**
     * Consulta el porcentaje del bono segun el origen.
     * 
     * @pre true (sin precondiciones adicionales, el enum siempre esta inicializado).
     * @post El valor retornado es mayor o igual a 0 y menor o igual a 1.
     * @return Porcentaje de bono correspondiente segun el origen.
     */
    public double getPorcentajeBono() {
        return this.porcentajeBono;
    }

    /**
     * Calcula el monto del bono segun el haber recibido.
     * 
     * @pre haberBase >= 0.
     * @post El valor retornado es mayor o igual a 0.
     * @post El valor retornado es igual a haberBase * porcentajeBono.
     * @param haberBase Sueldo sobre el cual se aplica el porcentaje
     * @return Valor del adicional por origen
     */
    public double calcularBonoPorOrigen(double haberBase) {
        return haberBase * this.porcentajeBono;
    }
    
}