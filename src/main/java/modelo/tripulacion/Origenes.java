/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package modelo.tripulacion;

/**
 * Enum que representa los posibles origenes planetarios de un tripulante.
 * Cada origen tiene asociado un porcentaje de bonificacion sobre el sueldo.
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
     * @pre porcentajeBono &gt;= 0.0.
     * @post Se crea una constante del enum con el porcentaje de bonificacion asignado.
     * @param porcentajeBono porcentaje de bonificacion asociado al origen
     */
    Origenes(double porcentajeBono) {
        this.porcentajeBono = porcentajeBono;
    }

    
    /**
     * Consulta el porsentaje del bono segun el origen.
     * 
     * @pre Ninguna (el enum siempre esta correctamente inicializado).
     * @post El valor retornado es mayor o igual a cero.
     * @return Porsentaje de bono correspondinte segun el origen.
     */
    public double getPorcentajeBono() {
        return this.porcentajeBono;
    }

    /**
     * Calcula el monto del bono segun el haber recibido.
     * 
     * @pre haberBase &gt;= 0.
     * @post El valor retornado es mayor o igual a cero.
     * @post El valor retornado es igual a haberBase * porcentajeBono.
     * @param haberBase Sueldo sobre el cual se aplica el porcentaje
     * @return Valor del adicional por origen
     */
    public double calcularBonoPorOrigen(double haberBase) {
        return haberBase * this.porcentajeBono;
    }
    
}