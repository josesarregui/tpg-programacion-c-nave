/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.liquidacion;

import modelo.tripulacion.Origenes;

/**
 *
 * @author Sebastian
 */
public class OrigenDecorator extends Decorator{
    /**
     * Constructor que solo recibe el objeto a decorar.
     * @param liquidacion Objeto que implementa Liquidacion
     */
    public OrigenDecorator(Liquidacion liquidacion) {
        super(liquidacion);
    }

    
    @Override
    public double calcularSueldo() {
        // Obtiene el sueldo acumulado de la capa previa
        double haberAcumulado = super.getLiquidacion().calcularSueldo();

        // Consulta el origen del tripulante base
        Origenes origenTripulante = this.getOrigen();

        // Aplica la bonificacion correspondiente segun el Origen
        double bonoOrigen = calcularBonoPorOrigen(origenTripulante, haberAcumulado);

        // Retorna la suma del sueldo acumulado mas el adicional por origen
        return haberAcumulado + bonoOrigen;
    }

    /**
     * Logica auxiliar para determinar el monto del bono segun el Origen.
     */
    private double calcularBonoPorOrigen(Origenes origen, double haberBase) {
        double adicional;
        switch (origen){ 
            case MARCIANO:
                adicional = haberBase * 0.15; // 15% de bonificacion por ser de Marte
            break;
            case VULCANO:
                adicional =  haberBase * 0.10; // 10% de bonificacion por ser de Vulcano
            break;
            case TERRICOLA:
                adicional =  haberBase * 0.0; // Sin adicional
            break;
            default:
                adicional =  0.0; // Sin adicional
            break;
        }
        return adicional;
    }

    @Override
    public Origenes getOrigen() {
        return super.getOrigen();
    }

    
}
