package modelo.mision;

import excepcion.NaveNoDisponibleException;
import excepcion.RecursoInsuficienteException;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.nave.TipoRecurso;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de E1-06 (Template Method), E1-10 (informe) y de los Escenarios A y B de la Ficha de Inicio:
 * ciclo completo de M-01, M-02 y M-03, costos de la Ficha (punto 5), informe, registro en la Bitácora,
 * rechazos sin cambios parciales y contratos incumplidos.
 */
public class MisionTest {

    private AsistenteComando asistente;
    private Tripulacion tripulacion;

    @BeforeEach
    public void inicializar() throws Exception {
        // Exploradora: combustible 60, energía 80, desgaste 0 (Ficha de Inicio, punto 2).
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Kirk", 3, Origen.TERRICOLA));
        integrantes.add(new Consejero("Troi", 2, Origen.VULCANO));
        integrantes.add(new Teniente("Uhura", 5, Origen.MARCIANO));
        integrantes.add(new Alferez("Chekov", 1, Origen.TERRICOLA));
        integrantes.add(new Alferez("Sulu", 0, Origen.MARCIANO));
        tripulacion = new Tripulacion(integrantes);
    }

    // --- Escenario A: ejecución correcta ---

    @Test
    @DisplayName("Escenario A: M-01, M-02 y M-03 completan su ciclo con los costos de la Ficha de Inicio")
    public void testEscenarioA() throws Exception {
        asistente.asignarTripulacion(tripulacion);

        InformeMision informe1 = realizar(new MisionIntercepcion());
        assertTrue(informe1.isExitosa());
        assertEquals(56, asistente.getCombustible());
        assertEquals(75, asistente.getEnergia());
        assertEquals(4, asistente.getDesgaste());

        InformeMision informe2 = realizar(new MisionRecoleccion());
        assertTrue(informe2.isExitosa());
        assertEquals(52, asistente.getCombustible());
        assertEquals(70, asistente.getEnergia());
        assertEquals(8, asistente.getDesgaste());

        InformeMision informe3 = realizar(new MisionRetorno());
        assertTrue(informe3.isExitosa());
        assertEquals(48, asistente.getCombustible());
        assertEquals(70, asistente.getEnergia(), "M-03 no consume energía adicional");
        assertEquals(12, asistente.getDesgaste());
    }

    @Test
    @DisplayName("Template Method: los cuatro pasos se realizan en orden y la misión queda cerrada")
    public void testCicloEnOrden() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        Mision mision = new MisionRecoleccion();
        assertEquals(EtapaMision.CREADA, mision.getEtapa());
        assertNull(mision.getInforme());

        InformeMision informe = realizar(mision);

        assertEquals(EtapaMision.CERRADA, mision.getEtapa());
        assertSame(informe, mision.getInforme());
        List<String> acciones = informe.getAcciones();
        assertEquals(4, acciones.size());
        assertTrue(acciones.get(0).startsWith("Preparación"));
        assertTrue(acciones.get(1).startsWith("Ejecución"));
        assertTrue(acciones.get(2).startsWith("Evaluación"));
        assertTrue(acciones.get(3).startsWith("Cierre"));
    }

    @Test
    @DisplayName("E1-10: el informe incluye misión, resultado, acciones, recursos consumidos, estado final y observaciones")
    public void testInforme() throws Exception {
        asistente.asignarTripulacion(tripulacion);

        InformeMision informe = realizar(new MisionIntercepcion());

        assertEquals("M-01 — Intercepción y asistencia", informe.getMision());
        assertTrue(informe.isExitosa());
        assertFalse(informe.getAcciones().isEmpty());
        assertEquals(4, informe.getCombustibleConsumido());
        assertEquals(5, informe.getEnergiaConsumida());
        assertEquals(4, informe.getDesgasteProducido());
        assertEquals(56, informe.getCombustibleFinal());
        assertEquals(75, informe.getEnergiaFinal());
        assertEquals(4, informe.getDesgasteFinal());
        assertEquals("Disponible", informe.getEstadoMotorFinal());
        assertTrue(informe.isNaveOperativa());
        assertTrue(informe.getObservaciones().contains("cumplida"));
        assertThrows(UnsupportedOperationException.class, () -> informe.getAcciones().add("acción ajena"),
                "El informe no puede modificarse desde afuera");
    }

    @Test
    @DisplayName("Aclaración R4: una misión exitosa hace que la nave prepare su salto y salte, y vuelve a Disponible")
    public void testSaltoAlCerrar() throws Exception {
        asistente.asignarTripulacion(tripulacion);

        realizar(new MisionIntercepcion());

        assertEquals("Disponible", asistente.getEstadoMotor());
        List<Evento> eventosMotor = eventosDeTipo(TipoEvento.MOTOR);
        assertEquals(4, eventosMotor.size());
        assertTrue(eventosMotor.get(0).getDescripcion().contains("Preparando salto"));
        assertTrue(eventosMotor.get(1).getDescripcion().contains("En warp"));
        assertTrue(eventosMotor.get(2).getDescripcion().contains("Enfriamiento"));
        assertTrue(eventosMotor.get(3).getDescripcion().contains("Disponible"));
    }

    @Test
    @DisplayName("E1-05: la Bitácora registra la ejecución de la misión, los recursos y el motor en orden temporal")
    public void testRegistroEnBitacora() throws Exception {
        asistente.asignarTripulacion(tripulacion);

        realizar(new MisionRecoleccion());

        assertFalse(eventosDeTipo(TipoEvento.MISION).isEmpty());
        assertEquals(1, eventosDeTipo(TipoEvento.RECURSO).size());
        assertFalse(eventosDeTipo(TipoEvento.MOTOR).isEmpty());
        List<Evento> eventos = asistente.getEventos();
        for (int i = 1; i < eventos.size(); i++) {
            assertFalse(eventos.get(i).getFechaHora().isBefore(eventos.get(i - 1).getFechaHora()));
        }
    }

    @Test
    @DisplayName("M-03: si el desgaste lleva la nave a requerir mantenimiento, la misión no es exitosa y no salta")
    public void testRetornoNoExitoso() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(0, 0, 76); // desgaste 76: todavía puede operar

        InformeMision informe = realizar(new MisionRetorno());

        assertFalse(informe.isExitosa());
        assertEquals(80, informe.getDesgasteFinal());
        assertFalse(informe.isNaveOperativa());
        assertTrue(informe.getObservaciones().contains("no cumplida"));
        assertEquals("Disponible", asistente.getEstadoMotor(), "Sin éxito no se ordena el salto");
        assertTrue(eventosDeTipo(TipoEvento.MOTOR).isEmpty());
    }

    // --- Escenario B: recursos insuficientes ---

    @Test
    @DisplayName("Escenario B: sin combustible suficiente la misión se rechaza, sin cambios parciales, y se registra el motivo")
    public void testEscenarioBCombustible() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(57, 0, 0); // quedan 3 de combustible
        Mision mision = new MisionIntercepcion();
        asistente.encomendarMision(mision);

        RecursoInsuficienteException e = assertThrows(RecursoInsuficienteException.class, asistente::ejecutarMision);

        assertEquals(TipoRecurso.COMBUSTIBLE, e.getRecurso());
        assertEquals(3, e.getDisponible());
        assertEquals(4, e.getCantidadSolicitada());
        assertEquals(3, asistente.getCombustible());
        assertEquals(80, asistente.getEnergia());
        assertEquals(0, asistente.getDesgaste());
        assertEquals("Disponible", asistente.getEstadoMotor());
        assertEquals(EtapaMision.CREADA, mision.getEtapa());
        assertNull(mision.getInforme());
        assertUltimoEventoEsError("rechazada");
    }

    @Test
    @DisplayName("Escenario B: sin energía suficiente se rechaza M-01, pero M-03 (sin energía adicional) puede realizarse")
    public void testEscenarioBEnergia() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(0, 77, 0); // quedan 3 de energía
        asistente.encomendarMision(new MisionIntercepcion());

        RecursoInsuficienteException e = assertThrows(RecursoInsuficienteException.class, asistente::ejecutarMision);

        assertEquals(TipoRecurso.ENERGIA, e.getRecurso());
        assertEquals(60, asistente.getCombustible(), "No se consumió combustible");
        assertEquals(3, asistente.getEnergia());
        asistente.cancelarMision(); // se desiste de M-01: no modificó la nave
        assertTrue(realizar(new MisionRetorno()).isExitosa());
    }

    @Test
    @DisplayName("Escenario B: una misión rechazada puede realizarse después de cargar combustible")
    public void testReintentoTrasCarga() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(57, 0, 0);
        Mision mision = new MisionRecoleccion();
        asistente.encomendarMision(mision);
        assertThrows(RecursoInsuficienteException.class, asistente::ejecutarMision);

        asistente.cargarCombustible(10);
        InformeMision informe = asistente.ejecutarMision();

        assertTrue(informe.isExitosa());
        assertEquals(9, asistente.getCombustible());
    }

    @Test
    @DisplayName("Aclaración R4: una nave sin tripulación no está disponible y la misión se rechaza")
    public void testNaveSinTripulacion() {
        asistente.encomendarMision(new MisionIntercepcion());

        NaveNoDisponibleException e = assertThrows(NaveNoDisponibleException.class, asistente::ejecutarMision);

        assertEquals("M-01", e.getCodigoMision());
        assertFalse(e.tieneTripulacion());
        assertEquals(60, asistente.getCombustible());
        assertUltimoEventoEsError("rechazada");
    }

    @Test
    @DisplayName("Guía I.8: una nave que requiere mantenimiento no puede realizar misiones hasta hacerlo")
    public void testNaveRequiereMantenimiento() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(0, 0, 80);
        Mision mision = new MisionRecoleccion();
        asistente.encomendarMision(mision);

        NaveNoDisponibleException e = assertThrows(NaveNoDisponibleException.class, asistente::ejecutarMision);
        assertTrue(e.requiereMantenimiento());

        asistente.realizarMantenimiento();
        assertTrue(asistente.ejecutarMision().isExitosa());
    }

    // --- Contratos incumplidos (aserciones) ---

    @Test
    @DisplayName("Rechazo por contrato: una misión no puede realizarse dos veces")
    public void testMisionRealizadaDosVeces() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        Mision mision = new MisionIntercepcion();
        asistente.encomendarMision(mision);
        asistente.ejecutarMision();

        assertThrows(AssertionError.class, asistente::ejecutarMision);
    }

    @Test
    @DisplayName("Rechazo por contrato (R4): una misión no puede encomendarse a dos asistentes")
    public void testMisionEnDosAsistentes() {
        Mision mision = new MisionIntercepcion();
        asistente.encomendarMision(mision);
        AsistenteComando otroAsistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.CARGUERO));

        assertThrows(AssertionError.class, () -> otroAsistente.encomendarMision(mision));
    }

    @Test
    @DisplayName("Rechazo por contrato: no se puede realizar una misión que no fue encomendada a un asistente")
    public void testMisionSinAsistente() {
        assertThrows(AssertionError.class, () -> new MisionRetorno().realizar());
        assertThrows(AssertionError.class, asistente::ejecutarMision);
    }

    @Test
    @DisplayName("Rechazo por contrato (R2): la misión sólo se encomienda y se realiza a través del asistente")
    public void testMisionSinPasarPorElAsistente() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        Mision sinEncomendar = new MisionRetorno();
        assertThrows(AssertionError.class, () -> sinEncomendar.asignarAsistente(asistente));
        assertNull(sinEncomendar.getAsistente());
        assertFalse(asistente.tieneMisionPendiente());

        Mision mision = new MisionRetorno();
        asistente.encomendarMision(mision);
        assertThrows(AssertionError.class, mision::realizar);

        assertEquals(EtapaMision.CREADA, mision.getEtapa(), "La misión no se realizó");
        assertEquals(60, asistente.getCombustible(), "La nave no cambió");
        assertTrue(asistente.ejecutarMision().isExitosa(), "El asistente sigue pudiendo realizarla");
    }

    @Test
    @DisplayName("Rechazo por contrato: una misión cancelada no puede realizarse")
    public void testMisionCancelada() {
        Mision mision = new MisionIntercepcion();
        asistente.encomendarMision(mision);
        asistente.cancelarMision();

        assertThrows(AssertionError.class, mision::realizar);
        assertEquals(EtapaMision.CREADA, mision.getEtapa());
    }

    // --- Auxiliares ---

    private InformeMision realizar(Mision mision) throws Exception {
        asistente.encomendarMision(mision);
        return asistente.ejecutarMision();
    }

    private List<Evento> eventosDeTipo(TipoEvento tipo) {
        List<Evento> resultado = new ArrayList<>();
        for (Evento evento : asistente.getEventos()) {
            if (evento.getTipo() == tipo) {
                resultado.add(evento);
            }
        }
        return resultado;
    }

    private void assertUltimoEventoEsError(String texto) {
        List<Evento> eventos = asistente.getEventos();
        Evento ultimo = eventos.get(eventos.size() - 1);
        assertEquals(TipoEvento.ERROR, ultimo.getTipo());
        assertTrue(ultimo.getDescripcion().contains(texto));
    }
}
