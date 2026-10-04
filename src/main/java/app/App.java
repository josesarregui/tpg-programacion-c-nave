package app;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.LiquidacionInvalidaException;
import excepcion.RecursoInsuficienteException;
import excepcion.TripulacionInvalidaException;
import excepcion.TripulanteInvalidoException;
import modelo.liquidacion.AntiguedadDecorator;
import modelo.liquidacion.ConceptoHaber;
import modelo.liquidacion.LiquidacionTripulacion;
import modelo.liquidacion.LiquidadorHaberes;
import modelo.liquidacion.ReciboHaberes;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.warp.MotorWarp;

import java.time.YearMonth;
import java.util.List;

/**
 * Programa de demostración (E1-03): simula al usuario y muestra por consola los resultados del modelo.
 * Es el único lugar que imprime; las clases del modelo sólo devuelven datos.
 *
 * Demuestra E1-04 y E1-08: tripulación mínima, liquidación mensual con el detalle de cada concepto
 * y casos de rechazo por precondiciones incumplidas.
 * Demuestra E1-01, E1-07 y E1-09: creación de los tres tipos de nave mediante la fábrica,
 * recursos antes y después de cada operación, mantenimiento y los Escenarios B y D.
 * Demuestra E1-02 (Escenario C): ciclo válido del Motor Warp y rechazo de una transición inválida.
 */
public class App {

    public static void main(String[] args) {
        demostrarLiquidacion();
        System.out.println();
        demostrarNaves();
        System.out.println();
        demostrarMotorWarp();
    }

    private static void demostrarMotorWarp() {
        System.out.println("ESCENARIO C - MOTOR WARP");
        MotorWarp motor = new NaveFactory().crearNave(TipoNave.EXPLORADORA).getMotor();
        System.out.println("Inicial: " + motor);
        motor.prepararSalto();
        System.out.println("Preparar salto -> " + motor);
        motor.iniciarWarp();
        System.out.println("Iniciar warp -> " + motor);
        motor.desactivarWarp();
        System.out.println("Desactivar warp -> " + motor);
        motor.enfriar();
        System.out.println("Enfriar -> " + motor);
        try {
            motor.iniciarWarp();
        } catch (EstadoMotorInvalidoException e) {
            System.out.println("Rechazado: " + e.getMessage() + " (estado: " + e.getEstadoActual() + ")");
        }
        System.out.println("Después del rechazo: " + motor + " (el estado no cambió)");
    }

    private static void demostrarLiquidacion() {
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

    private static void demostrarNaves() {
        System.out.println("NAVES CREADAS MEDIANTE LA FABRICA");
        NaveFactory fabrica = new NaveFactory();
        Nave exploradora = fabrica.crearNave(TipoNave.EXPLORADORA);
        Nave carguero = fabrica.crearNave(TipoNave.CARGUERO);
        Nave combate = fabrica.crearNave(TipoNave.COMBATE);
        System.out.println(exploradora);
        System.out.println(carguero);
        System.out.println(combate);

        try {
            exploradora.asignarTripulacion(new Tripulacion(List.of(
                    new Capitan("Picard", 6, Origen.TERRICOLA),
                    new Consejero("Guinan", 9, Origen.MARCIANO),
                    new Teniente("Worf", 4, Origen.VULCANO),
                    new Alferez("Ro", 1, Origen.TERRICOLA),
                    new Alferez("Crusher", 0, Origen.TERRICOLA))));
            System.out.println("Exploradora con tripulación asignada. Lista para operar: " + exploradora.estaListaParaOperar());

            System.out.println();
            System.out.println("OPERACIONES SOBRE RECURSOS");
            exploradora.cargarCombustible(20);
            System.out.println("Carga de 20 de combustible: " + exploradora);
            exploradora.consumirRecursos(4, 5, 4);
            System.out.println("Consumo de 4 de combustible y 5 de energía, con 4 de desgaste: " + exploradora);
            exploradora.consumirRecursos(0, 0, 76);
            System.out.println("Desgaste acumulado: " + exploradora
                    + " -> requiere mantenimiento: " + exploradora.requiereMantenimiento()
                    + ", lista para operar: " + exploradora.estaListaParaOperar());
            exploradora.realizarMantenimiento();
            System.out.println("Después del mantenimiento: " + exploradora
                    + " -> lista para operar: " + exploradora.estaListaParaOperar());
        } catch (TripulanteInvalidoException | TripulacionInvalidaException | CantidadInvalidaException
                 | CapacidadExcedidaException | RecursoInsuficienteException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }

        System.out.println();
        System.out.println("ESCENARIO D - CARGA QUE EXCEDE LA CAPACIDAD");
        System.out.println("Antes:   " + carguero);
        try {
            carguero.cargarCombustible(1);
        } catch (CantidadInvalidaException | CapacidadExcedidaException e) {
            System.out.println("Rechazado: " + e.getMessage());
        }
        System.out.println("Después: " + carguero + " (el estado anterior se conserva)");

        System.out.println();
        System.out.println("ESCENARIO B - RECURSOS INSUFICIENTES");
        try {
            combate.consumirRecursos(78, 0, 0);
        } catch (CantidadInvalidaException | CapacidadExcedidaException | RecursoInsuficienteException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }
        System.out.println("Antes:   " + combate);
        try {
            combate.consumirRecursos(4, 0, 4);
        } catch (RecursoInsuficienteException e) {
            System.out.println("Rechazado: " + e.getMessage() + " (recurso: " + e.getRecurso()
                    + ", disponible: " + e.getDisponible() + ")");
        } catch (CantidadInvalidaException | CapacidadExcedidaException e) {
            System.out.println("Rechazado: " + e.getMessage());
        }
        System.out.println("Después: " + combate + " (sin cambios parciales: el desgaste no aumentó)");
    }
}
