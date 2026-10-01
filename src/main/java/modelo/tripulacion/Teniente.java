/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

/**
 * Clase concreta que representa a un tripulante con rango de Teniente.
 * Sueldo base: 400.0 | Adicional por antiguedad: 3% del sueldo base por año.
 *
 * @author Sebastian
 */
public class Teniente extends Tripulante{
    // Sueldo base del rango de Teniente
    private static final double SUELDOBASETENIENTE = 400.0;
    // Adicional por cada año de antiguedad
    private static final double ADICIONALTENIENTE = 0.03;

    
    
    /**
     * Constructor para instanciar un Teniente.
     * 
     * @pre nombre != null y nombre no esta en blanco.
     * @pre antiguedad >= 0.
     * @pre origen != null.
     * @post Se crea un Teniente con los atributos inicializados.
     */
    public Teniente(String nombre, int antiguedad, Origenes origen) {
        super(nombre, antiguedad, origen);
    }

    
  
    /**
     * Retorna el sueldo base del Teniente.
     * 
     * @pre true (el Teniente esta correctamente inicializado).
     * @post El valor retornado es igual a SUELDOBASETENIENTE (400.0).
     * @return monto del sueldo base.
     */
    @Override
    public double calcularSueldo() {
        return SUELDOBASETENIENTE;
    }  
    
    /**
     * Calcula el adicional por antiguedad del Teniente.
     * 
     * @pre antiguedad >= 0.
     * @post El valor retornado es mayor o igual a 0.
     * @post El valor retornado es igual a SUELDOBASETENIENTE * ADICIONALTENIENTE * antiguedad.
     * @return monto del adicional por antiguedad.
     */
    @Override
    public double getAdicionalAntiguedad() {        
        // Consulta el adicional por cargo de Consejero
        double adicional = this.SUELDOBASETENIENTE * this.ADICIONALTENIENTE;
        // Calcula el adicional total por año de antiguedad
        adicional = adicional * super.antiguedad;
        return adicional;
    }
    
    @Override
    public String toString() {
        return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
}
