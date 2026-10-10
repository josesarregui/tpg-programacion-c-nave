package app;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.LiquidacionInvalidaException;
import excepcion.NaveInexistenteException;
import excepcion.NaveNoDisponibleException;
import excepcion.NaveYaRegistradaException;
import excepcion.RecursoInsuficienteException;
import excepcion.TripulacionInvalidaException;
import excepcion.TripulanteInvalidoException;
import modelo.asistente.Asistente;
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
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import modelo.universo.CentroDeControl;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Programa principal que simula al usuario (E1-03; Aclaración, R6): da de alta naves en el centro de control,
 * elige la nave en uso, crea misiones, se las encomienda a su asistente y las ejecuta.
 * Es el único lugar que imprime; las clases del modelo sólo devuelven datos, de modo que en la E2
 * este programa puede reemplazarse por pantallas sin cambiar el modelo.
 *
 * Toda consulta u orden a una nave pasa por su asistente (R2): el programa nunca opera la nave directamente.
 *
 * Demuestra:
 *  - E1-04 y E1-08: tripulación mínima y liquidación mensual con el detalle de cada concepto, y rechazos.
 *  - E1-01, E1-07 y R1: creación de los tres tipos de nave mediante la fábrica y su registro en el centro de control.
 *  - Escenario A (E1-03, E1-05, E1-06, E1-10): M-01, M-02 y M-03 con informe, recursos finales y Bitácora.
 *  - E1-09 y Escenario D: recursos antes y después de cargas, consumo y mantenimiento; carga que excede la capacidad.
 *  - Escenario B: misión rechazada por combustible insuficiente, sin cambios parciales.
 *  - Escenario C (E1-02): ciclo válido del Motor Warp y rechazo de una transición inválida, registrado en la Bitácora.
 */
public class App {

    public static void main(String[] args) {
        demostrarLiquidacion();
        System.out.println();

        CentroDeControl centro = new CentroDeControl();
        NaveFactory fabrica = new NaveFactory();
        try {
            int idExploradora = darDeAlta(centro, fabrica, TipoNave.EXPLORADORA);
            int idCarguero = darDeAlta(centro, fabrica, TipoNave.CARGUERO);
            int idCombate = darDeAlta(centro, fabrica, TipoNave.COMBATE);
            mostrarNavesRegistradas(centro);
            demostrarRechazosDelCentro(centro, idExploradora);

            System.out.println();
            demostrarEscenarioA(centro, idExploradora);
            System.out.println();
            demostrarRecursosYEscenarioD(centro, idCarguero);
            System.out.println();
            demostrarEscenarioB(centro, idCombate);
            System.out.println();
            demostrarEscenarioC(centro, idCombate);
        } catch (NaveYaRegistradaException | NaveInexistenteException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }
    }

    // --- Centro de control (R1) ---

    /**
     * Crea una nave mediante la fábrica, le asigna su asistente y la registra en el centro de control.
     *
     * @return el id de la nave registrada.
     */
    private static int darDeAlta(CentroDeControl centro, NaveFactory fabrica, TipoNave tipo)
            throws NaveYaRegistradaException {
        Asistente asistente = new AsistenteComando(fabrica.crearNave(tipo));
        centro.registrar(asistente);
        return asistente.getIdNave();
    }

    private static void mostrarNavesRegistradas(CentroDeControl centro) {
        System.out.println("NAVES REGISTRADAS EN EL CENTRO DE CONTROL (creadas mediante la fábrica)");
        for (Asistente asistente : centro.getAsistentes()) {
            System.out.println("   " + describir(asistente));
        }
    }

    private static void demostrarRechazosDelCentro(CentroDeControl centro, int idRegistrado) throws NaveInexistenteException {
        System.out.println();
        System.out.println("CASOS DE RECHAZO DEL CENTRO DE CONTROL");
        try {
            centro.registrar(centro.buscar(idRegistrado));
        } catch (NaveYaRegistradaException e) {
            System.out.println("Rechazado: " + e.getMessage() + " (id: " + e.getIdNave() + ")");
        }
        try {
            centro.buscar(999);
        } catch (NaveInexistenteException e) {
            System.out.println("Rechazado: " + e.getMessage() + " (id buscado: " + e.getIdNaveBuscada() + ")");
        }
        System.out.println("Naves registradas: " + centro.getCantidadNaves() + " (el centro no cambió)");
    }

    // --- Escenario A ---

    private static void demostrarEscenarioA(CentroDeControl centro, int idNave) throws NaveInexistenteException {
        System.out.println("ESCENARIO A - EJECUCIÓN CORRECTA DE M-01, M-02 Y M-03");
        Asistente asistente = centro.seleccionar(idNave);
        try {
            asistente.asignarTripulacion(crearTripulacion("Janeway", "Tuvok", "Paris", "Kim", "Torres"));
            System.out.println("Nave en uso: " + describir(centro.getAsistenteEnUso()));
            mostrarBitacora(asistente);

            // Ficha de Inicio, Escenario A, paso 5: para cada misión se muestran el informe, los recursos y su Bitácora.
            Mision[] misiones = {new MisionIntercepcion(), new MisionRecoleccion(), new MisionRetorno()};
            for (Mision mision : misiones) {
                int eventosAnteriores = asistente.getEventos().size();
                System.out.println();
                System.out.println("Recursos antes de " + mision.getCodigo() + ": " + describir(asistente));
                asistente.encomendarMision(mision);
                mostrarInforme(asistente.ejecutarMision());
                System.out.println("Recursos después de " + mision.getCodigo() + ": " + describir(asistente));
                mostrarEventosDesde(asistente, eventosAnteriores, "BITÁCORA DE " + mision.getCodigo());
            }
        } catch (TripulanteInvalidoException | TripulacionInvalidaException
                 | NaveNoDisponibleException | RecursoInsuficienteException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }
        System.out.println();
        System.out.println("Misiones realizadas por la nave: " + asistente.getMisionesRealizadas().size());
    }

    // --- Recursos, mantenimiento y Escenario D ---

    private static void demostrarRecursosYEscenarioD(CentroDeControl centro, int idNave) throws NaveInexistenteException {
        System.out.println("OPERACIONES SOBRE RECURSOS Y MANTENIMIENTO");
        Asistente asistente = centro.seleccionar(idNave);
        try {
            asistente.asignarTripulacion(crearTripulacion("Picard", "Guinan", "Worf", "Ro", "Crusher"));
            System.out.println("Nave en uso: " + describir(centro.getAsistenteEnUso()));
            asistente.cargarEnergia(20);
            System.out.println("Carga de 20 de energía: " + describir(asistente));
            asistente.consumirRecursos(4, 5, 4);
            System.out.println("Consumo de 4 de combustible y 5 de energía, con 4 de desgaste: " + describir(asistente));
            asistente.consumirRecursos(0, 0, 76);
            System.out.println("Desgaste acumulado: " + describir(asistente)
                    + " -> requiere mantenimiento: " + siNo(asistente.requiereMantenimiento())
                    + ", lista para operar: " + siNo(asistente.naveListaParaOperar()));
            asistente.realizarMantenimiento();
            System.out.println("Después del mantenimiento: " + describir(asistente)
                    + " -> lista para operar: " + siNo(asistente.naveListaParaOperar()));
            asistente.cargarCombustible(4);
            System.out.println("Carga de 4 de combustible: " + describir(asistente));
        } catch (TripulanteInvalidoException | TripulacionInvalidaException | CantidadInvalidaException
                 | CapacidadExcedidaException | RecursoInsuficienteException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }

        System.out.println();
        System.out.println("ESCENARIO D - CARGA QUE EXCEDE LA CAPACIDAD");
        System.out.println("Antes:   " + describir(asistente));
        try {
            asistente.cargarCombustible(1);
        } catch (CantidadInvalidaException | CapacidadExcedidaException e) {
            System.out.println("Rechazado: " + e.getMessage());
        }
        System.out.println("Después: " + describir(asistente) + " (el estado anterior se conserva)");
        mostrarBitacora(asistente);
    }

    // --- Escenario B ---

    private static void demostrarEscenarioB(CentroDeControl centro, int idNave) throws NaveInexistenteException {
        System.out.println("ESCENARIO B - MISIÓN CON RECURSOS INSUFICIENTES");
        Asistente asistente = centro.seleccionar(idNave);
        System.out.println("Nave en uso: " + describir(centro.getAsistenteEnUso()));
        try {
            asistente.asignarTripulacion(crearTripulacion("Sisko", "Dax", "Kira", "Nog", "Ezri"));
            asistente.consumirRecursos(77, 0, 0);
        } catch (TripulanteInvalidoException | TripulacionInvalidaException | CantidadInvalidaException
                 | RecursoInsuficienteException | CapacidadExcedidaException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }
        System.out.println("Antes (después de consumir 77 de combustible): " + describir(asistente));
        asistente.encomendarMision(new MisionRecoleccion());
        try {
            asistente.ejecutarMision();
        } catch (RecursoInsuficienteException e) {
            System.out.println("Rechazado: " + e.getMessage() + " (recurso: " + e.getRecurso()
                    + ", disponible: " + e.getDisponible() + ")");
        } catch (NaveNoDisponibleException e) {
            System.out.println("Rechazado: " + e.getMessage());
        }
        System.out.println("Después: " + describir(asistente) + " (sin cambios parciales)");
        System.out.println("Misión pendiente: " + asistente.getMisionPendiente() + " (puede reintentarse o cancelarse)");
        asistente.cancelarMision();
        mostrarBitacora(asistente);
    }

    // --- Escenario C ---

    private static void demostrarEscenarioC(CentroDeControl centro, int idNave) throws NaveInexistenteException {
        System.out.println("ESCENARIO C - MOTOR WARP");
        Asistente asistente = centro.seleccionar(idNave);
        int eventosAnteriores = asistente.getEventos().size();
        System.out.println("Motor inicial: " + asistente.getEstadoMotor());
        try {
            asistente.prepararSalto();
            System.out.println("Preparar salto -> " + asistente.getEstadoMotor());
            asistente.saltar();
            System.out.println("Saltar -> " + asistente.getEstadoMotor()
                    + " (recorrido: En warp -> Enfriamiento -> Disponible, ver Bitácora)");
        } catch (EstadoMotorInvalidoException e) {
            System.out.println("Error inesperado en la demostración: " + e.getMessage());
        }
        try {
            asistente.saltar();
        } catch (EstadoMotorInvalidoException e) {
            System.out.println("Saltar sin preparar el salto. Rechazado: " + e.getMessage()
                    + " (estado: " + e.getEstadoActual() + ")");
        }
        System.out.println("Después del rechazo: " + asistente.getEstadoMotor() + " (el estado no cambió)");
        mostrarEventosDesde(asistente, eventosAnteriores, "BITÁCORA (eventos del Escenario C)");
    }

    // --- Liquidación de haberes (E1-04, E1-08) ---

    private static void demostrarLiquidacion() {
        YearMonth octubre = YearMonth.of(2026, 10);
        try {
            Consejero consejero = new Consejero("Troi", 4, Origen.VULCANO);
            consejero.registrarConsejo(octubre);
            consejero.registrarConsejo(octubre);
            consejero.registrarConsejo(octubre);

            List<Tripulante> integrantes = new ArrayList<>();
            integrantes.add(new Capitan("Kirk", 3, Origen.TERRICOLA));
            integrantes.add(consejero);
            integrantes.add(new Teniente("Uhura", 5, Origen.MARCIANO));
            integrantes.add(new Alferez("Chekov", 1, Origen.TERRICOLA));
            integrantes.add(new Alferez("Sulu", 0, Origen.MARCIANO));
            Tripulacion tripulacion = new Tripulacion(integrantes);

            LiquidacionTripulacion liquidacion = new LiquidadorHaberes().liquidar(tripulacion, octubre);
            System.out.println("LIQUIDACIÓN DE HABERES - " + liquidacion.getPeriodo());
            for (ReciboHaberes recibo : liquidacion.getRecibos()) {
                System.out.println();
                System.out.println(recibo.getTripulante());
                for (ConceptoHaber concepto : recibo.getConceptos()) {
                    System.out.println("   " + concepto);
                }
                System.out.println("   TOTAL: " + recibo.getTotal() + " PG");
            }
            System.out.println();
            System.out.println("TOTAL DE LA TRIPULACIÓN: " + liquidacion.calcularTotal() + " PG");
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

    // --- Auxiliares de presentación ---

    private static Tripulacion crearTripulacion(String capitan, String consejero, String teniente,
                                                String alferez1, String alferez2)
            throws TripulanteInvalidoException, TripulacionInvalidaException {
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan(capitan, 5, Origen.TERRICOLA));
        integrantes.add(new Consejero(consejero, 8, Origen.VULCANO));
        integrantes.add(new Teniente(teniente, 3, Origen.MARCIANO));
        integrantes.add(new Alferez(alferez1, 0, Origen.TERRICOLA));
        integrantes.add(new Alferez(alferez2, 1, Origen.MARCIANO));
        return new Tripulacion(integrantes);
    }

    private static String describir(Asistente asistente) {
        return asistente.getTipoNave().getDescripcion() + " #" + asistente.getIdNave()
                + " [combustible=" + asistente.getCombustible() + ", energía=" + asistente.getEnergia()
                + ", desgaste=" + asistente.getDesgaste() + ", motor=" + asistente.getEstadoMotor() + "]";
    }

    private static String siNo(boolean valor) {
        return valor ? "sí" : "no";
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
                + informe.getEstadoMotorFinal() + ", operativa: " + siNo(informe.isNaveOperativa()));
        System.out.println("   Observaciones: " + informe.getObservaciones());
    }

    private static void mostrarBitacora(Asistente asistente) {
        mostrarEventosDesde(asistente, 0, "BITÁCORA");
    }

    /**
     * Muestra los eventos de la Bitácora registrados a partir de la posición indicada (en orden temporal).
     */
    private static void mostrarEventosDesde(Asistente asistente, int desde, String titulo) {
        System.out.println();
        System.out.println(titulo);
        List<Evento> eventos = asistente.getEventos();
        for (int i = desde; i < eventos.size(); i++) {
            System.out.println("   " + eventos.get(i));
        }
    }
}
