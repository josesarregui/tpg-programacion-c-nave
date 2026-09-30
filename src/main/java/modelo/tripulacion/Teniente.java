/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

/**
 *
 * @author Sebastian
 */
public class Teniente extends Tripulante{
    // Sueldo base del rango de Teniente
    private static final double SUELDOBASETENIENTE = 400.0;
    // Adicional por cada año de antiguedad
    private static final double ADICIONALTENIENTE = 0.3;

    
    
    public Teniente(String nombre, int antiguedad, Origenes origen) {
        super(nombre, antiguedad, origen);
    }

 
    
    public static double getSUELDOBASETENIENTE() {
        return SUELDOBASETENIENTE;
    }
  
    @Override
    public double calcularSueldo() {
        return SUELDOBASETENIENTE;
    }  
    
    @Override
    public double getAdicionalCargo() {
        return this.ADICIONALTENIENTE;
    }
    
    @Override
    public String toString() {
        return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
}
