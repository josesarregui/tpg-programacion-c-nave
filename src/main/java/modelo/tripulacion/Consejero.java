/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

/**
 * Clase concreta que representa a un tripulante con rango de Consejero.
 * Sueldo base: 600.0 | Adicional por antiguedad: 5% del sueldo base por año + $2 por consejo.
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

    
    
    /**
     * Constructor para instanciar un Consejero sin consejos registrados.
     * 
     * @pre nombre != null y nombre no esta en blanco.
     * @pre antiguedad >= 0.
     * @pre origen != null.
     * @post Se crea un Consejero con cantConsejos inicializado en 0.
     */
    public Consejero(String nombre, int antiguedad, Origenes origen) {
        super(nombre, antiguedad, origen);
        this.cantConsejos = 0;
    }
    /**
     * Constructor para instanciar un Consejero con cantidad de consejos inicial.
     * 
     * @pre nombre != null y nombre no esta en blanco.
     * @pre antiguedad >= 0.
     * @pre origen != null.
     * @pre cantConsejos >= 0.
     * @post Se crea un Consejero con la cantidad de consejos especificada.
     */
    public Consejero(String nombre, int antiguedad, Origenes origen, int cantConsejos) {
        super(nombre, antiguedad, origen);
        assert cantConsejos >= 0 : "La cantidad de consejos no puede ser negativa.";
        this.cantConsejos = cantConsejos;
    }
    

    
    /**
     * Consulta el sueldo base del Consejero.
     * 
     * @post El valor retornado es igual a SUELDOBASECONSEJHERO (600.0).
     * @return sueldo base del rango Consejero.
     */
    public static double getSUELDOBASECONSEJHERO() {
        return SUELDOBASECONSEJHERO;
    }

    /**
     * Retorna el sueldo base del Consejero.
     * 
     * @post El valor retornado es igual a SUELDOBASECONSEJHERO (600.0).
     * @return monto del sueldo base.
     */
    @Override
    public double calcularSueldo() {
        return SUELDOBASECONSEJHERO;
    }
    
    /**
     * Calcula el adicional por antiguedad del Consejero.
     * Incluye un bonus de $2 por cada consejo registrado.
     * 
     * @pre antiguedad >= 0.
     * @pre cantConsejos >= 0.
     * @post El valor retornado es mayor o igual a 0.
     * @post El valor retornado es igual a (SUELDOBASECONSEJHERO * ADICIONALCONSEJERO * antiguedad) + (cantConsejos * 2).
     * @return monto del adicional por antiguedad mas bonus por consejos.
     */
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
