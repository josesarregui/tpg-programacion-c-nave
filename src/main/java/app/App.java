package app;

import excepcion.LiquidacionInvalidaException;
import excepcion.TripulacionInvalidaException;
import excepcion.TripulanteInvalidoException;
import modelo.liquidacion.AntiguedadDecorator;
import modelo.liquidacion.ConceptoHaber;
import modelo.liquidacion.LiquidacionTripulacion;
import modelo.liquidacion.LiquidadorHaberes;
import modelo.liquidacion.ReciboHaberes;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;

import java.time.YearMonth;
import java.util.List;

/**
 * Programa de demostración (E1-03): simula al usuario y muestra por consola los resultados del modelo.
 * Es el único lugar que imprime; las clases del modelo sólo devuelven datos.
 *
 * Demuestra E1-04 y E1-08: tripulación mínima, liquidación mensual con el detalle de cada concepto
 * y casos de rechazo por precondiciones incumplidas.
 */
public class App {

    public static void main(String[] args) {
        YearMonth octubre = YearMonth.of(2026, 10);
        try {
            Consejero consejero = new Consejero("Troi", 4, Origen.VULCANO);
            consejero.registrarConsejo(octubre);
            consejero.registrarConsejo(octubre);
            consejero.registrarConsejo(octubre);

            Tripulacion tripulacion = new Tripulacion(List.of(
                    new Capitan("Kirk", 3, Origen.TERRICOLA),
                    consejero,
                    new Teniente("Uhura", 5, Origen.MARCIANO),
                    new Alferez("Chekov", 1, Origen.TERRICOLA),
                    new Alferez("Sulu", 0, Origen.MARCIANO)));

            LiquidacionTripulacion liquidacion = new LiquidadorHaberes().liquidar(tripulacion, octubre);
            System.out.println("LIQUIDACION DE HABERES - " + liquidacion.getPeriodo());
            for (ReciboHaberes recibo : liquidacion.getRecibos()) {
                System.out.println();
                System.out.println(recibo.getTripulante());
                for (ConceptoHaber concepto : recibo.getConceptos()) {
                    System.out.println("   " + concepto);
                }
                System.out.println("   TOTAL: " + recibo.getTotal() + " PG");
            }
            System.out.println();
            System.out.println("TOTAL DE LA TRIPULACION: " + liquidacion.calcularTotal() + " PG");
        } catch (TripulanteInvalidoException | TripulacionInvalidaException | LiquidacionInvalidaException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }

        System.out.println();
        System.out.println("CASOS DE RECHAZO");
        try {
            new Teniente("Spock", -2, Origen.VULCANO);
        } catch (TripulanteInvalidoException e) {
            System.out.println("Rechazado: " + e.getMessage() + " (antigüedad recibida: " + e.getAntiguedadRecibida() + ")");
        }
        try {
            new AntiguedadDecorator(new AntiguedadDecorator(new Capitan("Pike", 3, Origen.TERRICOLA)));
        } catch (TripulanteInvalidoException | LiquidacionInvalidaException e) {
            System.out.println("Rechazado: " + e.getMessage());
        }
    }
}
