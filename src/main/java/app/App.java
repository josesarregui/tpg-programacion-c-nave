/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import modelo.liquidacion.AntiguedadDecorator;
import modelo.liquidacion.Liquidacion;
import modelo.liquidacion.OrigenDecorator;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origenes;
import modelo.tripulacion.Teniente;

/**
 *
 * @author Sebastian
 */
public class App {

    public static void main(String[] args) {
        
        System.out.println("CAPITAN");

        Liquidacion capitan = new Capitan("Sebastian", 5, Origenes.TERRICOLA);
        System.out.println("Sueldo base: " + capitan.calcularSueldo());

        capitan = new AntiguedadDecorator(capitan);
        System.out.println("Sueldo con antiguedad: " + capitan.calcularSueldo());
        
        capitan = new OrigenDecorator(capitan);
        System.out.println("Sueldo con adicional de orgien: " + capitan.calcularSueldo());

        
        
        
        

        System.out.println("CONSEJERO");

        Liquidacion consejero = new Consejero("Juan", 4, Origenes.VULCANO,4);
        System.out.println("Sueldo base: " + consejero.calcularSueldo());

        consejero = new AntiguedadDecorator(consejero);
        System.out.println("Sueldo con antiguedad: " + consejero.calcularSueldo());

        consejero = new OrigenDecorator(consejero);
        System.out.println("Sueldo con adicional de orgien: " + consejero.calcularSueldo());

        
        
        
        
        System.out.println("TENIENTE");

        Liquidacion teniente = new Teniente("Pedro", 3, Origenes.MARCIANO);
        System.out.println("Sueldo base: " + teniente.calcularSueldo());

        teniente = new AntiguedadDecorator(teniente);
        System.out.println("Sueldo con antiguedad: " + teniente.calcularSueldo());

        teniente = new OrigenDecorator(teniente);
        System.out.println("Sueldo con adicional de orgien: " + teniente.calcularSueldo());
        
        
        
        
        System.out.println("ALFEREZ");

        Liquidacion alferez = new Alferez("Maria", 8, Origenes.TERRICOLA);
        System.out.println("Sueldo base: " + alferez.calcularSueldo());

        alferez = new AntiguedadDecorator(alferez);
        System.out.println("Sueldo con antiguedad: " + alferez.calcularSueldo());
        
        alferez = new OrigenDecorator(alferez);
        System.out.println("Sueldo con adicional de orgien: " + alferez.calcularSueldo());
    }
}
