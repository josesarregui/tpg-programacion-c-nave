/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

/**
 *
 * @author Sebastian
 */
public class Alferez extends Tripulante{
    // Sueldo base del rango de Alferez
    private static final double SUELDOBASEALFEREZ = 200.0;
    // Adicional por cada año de antiguedad
    private static final double ADICIONALALFEREZ = 0.05;

    
    
    public Alferez(String nombre, int antiguedad, Origenes origen) {
        super(nombre, antiguedad, origen);
    }


    
    public static double getSUELDOBASEALFEREZ() {
        return SUELDOBASEALFEREZ;
    }
    
    
    @Override
    public double calcularSueldo() {
        return SUELDOBASEALFEREZ;
    }
    
    @Override
    public double getAdicionalCargo() {
        return this.ADICIONALALFEREZ;
    }
    
    @Override
    public String toString() {
        return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

}
