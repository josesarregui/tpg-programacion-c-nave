/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

/**
 * Clase concreta que representa a un tripulante con rango de Alferez.
 * Sueldo base: 200.0 | Adicional por antiguedad: 0.5% del sueldo base por año.
 *
 * @author Sebastian
 */
public class Alferez extends Tripulante{
    // Sueldo base del rango de Alferez
    private static final double SUELDOBASEALFEREZ = 200.0;
    // Adicional por cada año de antiguedad
    private static final double ADICIONALALFEREZ = 0.005;

    
    
    /**
     * Constructor para instanciar un Alferez.
     * 
     * @pre nombre != null y nombre no esta en blanco.
     * @pre antiguedad >= 0.
     * @pre origen != null.
     * @post Se crea un Alferez con los atributos inicializados.
     */
    public Alferez(String nombre, int antiguedad, Origenes origen) {
        super(nombre, antiguedad, origen);
    }

    
    /**
     * Retorna el sueldo base del Alferez.
     * 
     * @pre true (el Alferez esta correctamente inicializado).
     * @post El valor retornado es igual a SUELDOBASEALFEREZ (200.0).
     * @return monto del sueldo base.
     */
    @Override
    public double calcularSueldo() {
        return SUELDOBASEALFEREZ;
    }
    
    /**
     * Calcula el adicional por antiguedad del Alferez.
     * 
     * @pre antiguedad >= 0.
     * @post El valor retornado es mayor o igual a 0.
     * @post El valor retornado es igual a SUELDOBASEALFEREZ * ADICIONALALFEREZ * antiguedad.
     * @return monto del adicional por antiguedad.
     */
    @Override
    public double getAdicionalAntiguedad() {
        // Consulta el adicional por cargo de Alferez
        double adicional = this.SUELDOBASEALFEREZ * this.ADICIONALALFEREZ;
        // Calcula el adicional total por año de antiguedad
        adicional = adicional * super.antiguedad;
        return adicional;
    }
    
    @Override
    public String toString() {
        return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

}
