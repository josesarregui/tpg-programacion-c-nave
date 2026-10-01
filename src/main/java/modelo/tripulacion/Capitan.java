/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

/**
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
     */
    public Capitan(String nombre, int antiguedad, Origenes origen) {
        super(nombre, antiguedad, origen);
    }
    
    
    @Override
    public double calcularSueldo() {
        return SUELDOBASECAPITAN;
    }
    
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
