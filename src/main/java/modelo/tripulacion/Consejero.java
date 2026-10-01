/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

/**
 *
 * @author Sebastian
 */
public class Consejero extends Tripulante{
    // Sueldo base del rango de Consejero
    private static final double SUELDOBASECONSEJHERO = 600.0;
    // Adicional por cada año de antiguedad
    private static final double ADICIONALCONSEJERO = 0.05;
    // Cantidad de consejos registrados
    private int cantConsejos;

    
    
    public Consejero(String nombre, int antiguedad, Origenes origen) {
        super(nombre, antiguedad, origen);
        this.cantConsejos = 0;
    }
    public Consejero(String nombre, int antiguedad, Origenes origen, int cantConsejos) {
        super(nombre, antiguedad, origen);
        this.cantConsejos = cantConsejos;
    }
    

    
    public static double getSUELDOBASECONSEJHERO() {
        return SUELDOBASECONSEJHERO;
    }

    @Override
    public double calcularSueldo() {
        return SUELDOBASECONSEJHERO;
    }
    
    @Override
    public double getAdicionalAntiguedad() {        
        // Consulta el adicional por cargo de Consejero
        double adicional = this.SUELDOBASECONSEJHERO * this.ADICIONALCONSEJERO;
        // Calcula el adicional total por año de antiguedad
        adicional = adicional * super.antiguedad;
        // Se le agrega el adicional por cada consejo realizado
        adicional = adicional + this.cantConsejos * 2;
        return adicional;
    }

    @Override
    public String toString() {
        return "Consejero{" + "cantConsejos=" + cantConsejos + super.toString() +'}';
    }
}
