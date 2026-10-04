package app;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.LiquidacionInvalidaException;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursoInsuficienteException;
import excepcion.TripulacionInvalidaException;
import excepcion.TripulanteInvalidoException;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.liquidacion.AntiguedadDecorator;
import modelo.liquidacion.ConceptoHaber;
import modelo.liquidacion.LiquidacionTripulacion;
import modelo.liquidacion.LiquidadorHaberes;
import modelo.liquidacion.ReciboHaberes;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import modelo.mision.MisionIntercepcion;
import modelo.mision.MisionRecoleccion;
import modelo.mision.MisionRetorno;
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
 * Demuestra E1-03, E1-06 y E1-10 (Escenarios A y B): el asistente ejecuta M-01, M-02 y M-03 mostrando
 * informe, recursos finales y Bitácora, y una misión rechazada por recursos insuficientes sin cambios parciales.
 */
public class App {

    public static void main(String[] args) {
        demostrarLiquidacion();
        System.out.println();
        demostrarNaves();
        System.out.println();
        demostrarMotorWarp();
        System.out.println();
        demostrarMisiones();
    }

    private static void demostrarMisiones() {
        System.out.println("ESCENARIO A - EJECUCION CORRECTA DE M-01, M-02 Y M-03");
        NaveFactory fabrica = new NaveFactory();
        fabrica.crearNave(TipoNave.CARGUERO);
        fabrica.crearNave(TipoNave.COMBATE);
        AsistenteComando asistente = new AsistenteComando(fabrica.crearNave(TipoNave.EXPLORADORA));
        try {
            asistente.asignarTripulacion(new Tripulacion(List.of(
                    new Capitan("Janeway", 7, Origen.TERRICOLA),
                    new Consejero("Tuvok", 10, Origen.VULCANO),
                    new Teniente("Paris", 2, Origen.TERRICOLA),
                    new Alferez("Kim", 0, Origen.TERRICOLA),
                    new Alferez("Torres", 1, Origen.MARCIANO))));
            System.out.println("Nave seleccionada: " + asistente.getDescripcionNave());

            Mision[] misiones = {new MisionIntercepcion(), new MisionRecoleccion(), new MisionRetorno()};
            for (Mision mision : misiones) {
                asistente.encomendarMision(mision);
                mostrarInforme(asistente.ejecutarMision());
                System.out.println("   Recursos finales: " + asistente.getDescripcionNave());
            }
        } catch (TripulanteInvalidoException | TripulacionInvalidaException
                 | NaveNoDisponibleException | RecursoInsuficienteException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }
        mostrarBitacora(asistente);

        System.out.println();
        System.out.println("ESCENARIO B - MISION CON RECURSOS INSUFICIENTES");
        AsistenteComando asistenteCarguero = new AsistenteComando(fabrica.crearNave(TipoNave.CARGUERO));
        try {
            asistenteCarguero.asignarTripulacion(new Tripulacion(List.of(
                    new Capitan("Sisko", 5, Origen.TERRICOLA),
                    new Consejero("Dax", 8, Origen.MARCIANO),
                    new Teniente("Kira", 3, Origen.MARCIANO),
                    new Alferez("Nog", 0, Origen.TERRICOLA),
                    new Alferez("Ezri", 1, Origen.TERRICOLA))));
            asistenteCarguero.consumirRecursos(97, 0, 0);
        } catch (TripulanteInvalidoException | TripulacionInvalidaException | CantidadInvalidaException
                 | RecursoInsuficienteException | CapacidadExcedidaException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }
        System.out.println("Antes:   " + asistenteCarguero.getDescripcionNave());
        asistenteCarguero.encomendarMision(new MisionRecoleccion());
        try {
            asistenteCarguero.ejecutarMision();
        } catch (RecursoInsuficienteException e) {
            System.out.println("Rechazado: " + e.getMessage() + " (recurso: " + e.getRecurso()
                    + ", disponible: " + e.getDisponible() + ")");
        } catch (NaveNoDisponibleException e) {
            System.out.println("Rechazado: " + e.getMessage());
        }
        System.out.println("Después: " + asistenteCarguero.getDescripcionNave() + " (sin cambios parciales)");
        mostrarBitacora(asistenteCarguero);
    }

    private static void mostrarInforme(InformeMision informe) {
        System.out.println();
        System.out.println("INFORME " + informe.getMision() + " - " + (informe.isExitosa() ? "EXITOSA" : "NO EXITOSA"));
        for (String accion : informe.getAcciones()) {
            System.out.println("   " + accion);
        }
        System.out.println("   Recursos consumidos: combustible " + informe.getCombustibleConsumido()
                + ", energía " + informe.getEnergiaConsumida() + ", desgaste " + informe.getDesgasteProducido());
        System.out.println("   Estado final: combustible " + informe.getCombustibleFinal() + ", energía "
                + informe.getEnergiaFinal() + ", desgaste " + informe.getDesgasteFinal() + ", motor "
                + informe.getEstadoMotorFinal() + ", operativa: " + (informe.isNaveOperativa() ? "sí" : "no"));
        System.out.println("   Observaciones: " + informe.getObservaciones());
    }

    private static void mostrarBitacora(AsistenteComando asistente) {
        System.out.println();
        System.out.println("BITACORA");
        for (Evento evento : asistente.getEventos()) {
            System.out.println("   " + evento);
        }
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

        System.out.println();
        System.out.println("Transición inválida ordenada a través del asistente (queda registrada en la Bitácora):");
        AsistenteComando asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.COMBATE));
        try {
            asistente.saltar();
        } catch (EstadoMotorInvalidoException e) {
            System.out.println("Rechazado: " + e.getMessage() + " (estado: " + e.getEstadoActual() + ")");
        }
        mostrarBitacora(asistente);
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
