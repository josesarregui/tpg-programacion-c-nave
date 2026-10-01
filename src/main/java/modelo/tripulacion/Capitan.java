/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

/**
 * Clase concreta que representa a un tripulante con rango de Capitan.
 * Sueldo base: 1000.0 | Adicional por antiguedad: 20% del sueldo base por año.
 *
 * @author Sebastian
 */
public class Capitan extends Tripulante{
    // Sueldo base del rango de Capitan
    private static final double SUELDOBASECAPITAN = 1000.0;
    // Adicional por cada año de antiguedad
    private static final double ADICIONALCAPITAN = 0.20;

    /**
     * Constructor para instanciar un Capitan.
     * 
     * @pre nombre != null y nombre no esta en blanco.
     * @pre antiguedad >= 0.
     * @pre origen != null.
     * @post Se crea un Capitan con los atributos inicializados.
     */
    public Capitan(String nombre, int antiguedad, Origenes origen) {
        super(nombre, antiguedad, origen);
    }
    
    
    /**
     * Retorna el sueldo base del Capitan.
     * 
     * @pre true (el Capitan esta correctamente inicializado).
     * @post El valor retornado es igual a SUELDOBASECAPITAN (1000.0).
     * @return monto del sueldo base.
     */
    @Override
    public double calcularSueldo() {
        return SUELDOBASECAPITAN;
    }
    
    /**
     * Calcula el adicional por antiguedad del Capitan.
     * 
     * @pre antiguedad >= 0.
     * @post El valor retornado es mayor o igual a 0.
     * @post El valor retornado es igual a SUELDOBASECAPITAN * ADICIONALCAPITAN * antiguedad.
     * @return monto del adicional por antiguedad.
     */
    @Override
    public double getAdicionalAntiguedad() {        
        // Consulta el adicional por cargo de Capitan
        double adicional = this.SUELDOBASECAPITAN * this.ADICIONALCAPITAN;
        // Calcula el adicional total por año de antiguedad
        adicional = adicional * super.antiguedad;
        return adicional;
    }
    
    @Override
    public String toString() {
        return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
}
