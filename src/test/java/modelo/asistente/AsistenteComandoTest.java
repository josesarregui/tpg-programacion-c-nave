package modelo.asistente;

import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.NaveNoDisponibleException;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.EtapaMision;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import modelo.mision.MisionIntercepcion;
import modelo.mision.MisionRetorno;
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
 * Evidencia de E1-03 y E1-05 (Aclaración, R2, R4 y R5): el asistente delega las órdenes en la nave y en el
 * Motor Warp, registra en su Bitácora las operaciones y los rechazos (Escenarios C y D), propaga el error al
 * invocante, administra sus misiones y deja en la Bitácora el informe de cada una.
 */
public class AsistenteComandoTest {

    private AsistenteComando asistente;

    @BeforeEach
    public void inicializar() {
        // Carguero: combustible 100, energía 60, desgaste 0 (Ficha de Inicio, punto 2).
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.CARGUERO));
    }

    @Test
    @DisplayName("El asistente registra su puesta en servicio y empieza sin misiones")
    public void testCreacion() {
        assertEquals(1, asistente.getEventos().size());
        assertEquals(TipoEvento.SISTEMA, asistente.getEventos().get(0).getTipo());
        assertFalse(asistente.tieneMisionPendiente());
        assertNull(asistente.getMisionPendiente());
        assertTrue(asistente.getMisionesRealizadas().isEmpty());
    }

    @Test
    @DisplayName("Las consultas devuelven datos de la nave que opera, no texto formateado")
    public void testConsultas() {
        assertTrue(asistente.getIdNave() > 0);
        assertEquals(TipoNave.CARGUERO, asistente.getTipoNave());
        assertEquals(100, asistente.getCombustible());
        assertEquals(60, asistente.getEnergia());
        assertEquals(0, asistente.getDesgaste());
        assertEquals("Disponible", asistente.getEstadoMotor());
        assertFalse(asistente.tieneTripulacion());
        assertFalse(asistente.naveListaParaOperar(), "Sin tripulación la nave no está lista para operar");
    }

    // --- Motor Warp (Escenario C) ---

    @Test
    @DisplayName("Escenario C: la transición inválida se rechaza, no cambia el motor y queda registrada")
    public void testTransicionInvalidaRegistrada() {
        EstadoMotorInvalidoException e = assertThrows(EstadoMotorInvalidoException.class, asistente::saltar);

        assertEquals("Disponible", e.getEstadoActual());
        assertEquals("Disponible", asistente.getEstadoMotor());
        assertUltimoEvento(TipoEvento.ERROR, "Disponible");
    }

    @Test
    @DisplayName("Escenario C: preparar el salto dos veces se rechaza y el motor sigue en Preparando salto")
    public void testPrepararSaltoDosVeces() throws Exception {
        asistente.prepararSalto();

        EstadoMotorInvalidoException e = assertThrows(EstadoMotorInvalidoException.class, asistente::prepararSalto);

        assertEquals("Preparando salto", e.getEstadoActual());
        assertEquals("Preparando salto", asistente.getEstadoMotor());
        assertUltimoEvento(TipoEvento.ERROR, "Preparando salto");
    }

    @Test
    @DisplayName("Escenario C: preparar el salto y saltar registra cada cambio del motor en orden y termina en Disponible")
    public void testSaltoRegistrado() throws Exception {
        asistente.prepararSalto();
        asistente.saltar();

        assertEquals("Disponible", asistente.getEstadoMotor());
        List<Evento> eventos = asistente.getEventos();
        assertEquals(5, eventos.size());
        assertTrue(eventos.get(1).getDescripcion().contains("Preparando salto"));
        assertTrue(eventos.get(2).getDescripcion().contains("En warp"));
        assertTrue(eventos.get(3).getDescripcion().contains("Enfriamiento"));
        assertTrue(eventos.get(4).getDescripcion().contains("Disponible"));
    }

    // --- Recursos (Escenario D, E1-09) ---

    @Test
    @DisplayName("Escenario D: una carga que excede la capacidad se rechaza, conserva el estado y queda registrada")
    public void testCargaExcedidaRegistrada() {
        CapacidadExcedidaException e = assertThrows(CapacidadExcedidaException.class,
                () -> asistente.cargarCombustible(1));

        assertEquals(TipoRecurso.COMBUSTIBLE, e.getRecurso());
        assertEquals(100, asistente.getCombustible());
        assertUltimoEvento(TipoEvento.ERROR, "rechazada");
    }

    @Test
    @DisplayName("E1-05: las cargas y el mantenimiento quedan registrados como operaciones sobre recursos")
    public void testOperacionesRegistradas() throws Exception {
        asistente.cargarEnergia(10);
        assertEquals(70, asistente.getEnergia());
        assertUltimoEvento(TipoEvento.RECURSO, "energía");

        asistente.consumirRecursos(0, 0, 85);
        assertTrue(asistente.requiereMantenimiento());
        asistente.realizarMantenimiento();
        assertEquals(0, asistente.getDesgaste());
        assertUltimoEvento(TipoEvento.RECURSO, "Mantenimiento");
    }

    // --- Misiones (R4 y R5) ---

    @Test
    @DisplayName("R5: la misión realizada se conserva con su informe y la Bitácora resume su resultado")
    public void testMisionRealizadaEInformeEnBitacora() throws Exception {
        asistente.asignarTripulacion(crearTripulacion());
        Mision mision = new MisionRetorno();
        asistente.encomendarMision(mision);
        assertSame(mision, asistente.getMisionPendiente());

        InformeMision informe = asistente.ejecutarMision();

        assertFalse(asistente.tieneMisionPendiente());
        assertFalse(asistente.estaEjecutando(mision), "Al terminar, la misión ya no está en ejecución");
        assertEquals(1, asistente.getMisionesRealizadas().size());
        assertSame(mision, asistente.getMisionesRealizadas().get(0));
        assertSame(informe, asistente.getMisionesRealizadas().get(0).getInforme());
        assertUltimoEvento(TipoEvento.MISION, "Informe de M-03");
        assertUltimoEvento(TipoEvento.MISION, "exitosa");
        assertUltimoEvento(TipoEvento.MISION, "Consumo: combustible 4, energía 0, desgaste 4");
        assertUltimoEvento(TipoEvento.MISION, "Estado final: combustible 96");
    }

    @Test
    @DisplayName("Escenario B: la misión rechazada sigue pendiente y puede cancelarse sin modificar la nave")
    public void testMisionRechazadaYCancelada() {
        Mision mision = new MisionIntercepcion();
        asistente.encomendarMision(mision); // la nave no tiene tripulación

        assertThrows(NaveNoDisponibleException.class, asistente::ejecutarMision);
        assertSame(mision, asistente.getMisionPendiente());
        assertFalse(asistente.estaEjecutando(mision), "El rechazo también quita la marca de ejecución (finally)");
        assertEquals(EtapaMision.CREADA, mision.getEtapa());

        asistente.cancelarMision();

        assertFalse(asistente.tieneMisionPendiente());
        assertTrue(asistente.getMisionesRealizadas().isEmpty());
        assertEquals(100, asistente.getCombustible());
        assertEquals(60, asistente.getEnergia());
        assertUltimoEvento(TipoEvento.MISION, "cancelada");
    }

    @Test
    @DisplayName("Rechazo por contrato: no se encomienda otra misión mientras haya una pendiente")
    public void testNoSeEncomiendaConMisionPendiente() {
        Mision primera = new MisionIntercepcion();
        asistente.encomendarMision(primera);
        Mision segunda = new MisionRetorno();

        assertThrows(AssertionError.class, () -> asistente.encomendarMision(segunda));
        assertSame(primera, asistente.getMisionPendiente());
        assertNull(segunda.getAsistente(), "La segunda misión no quedó encomendada: no hay cambios parciales");
    }

    @Test
    @DisplayName("Rechazo por contrato: no se cancela ni se ejecuta una misión si no hay ninguna pendiente")
    public void testSinMisionPendiente() {
        assertThrows(AssertionError.class, asistente::cancelarMision);
        assertThrows(AssertionError.class, asistente::ejecutarMision);
    }

    @Test
    @DisplayName("Las listas que entrega el asistente son de sólo lectura")
    public void testListasDeSoloLectura() {
        assertThrows(UnsupportedOperationException.class, () -> asistente.getMisionesRealizadas().add(new MisionRetorno()));
        assertThrows(UnsupportedOperationException.class, () -> asistente.getEventos().clear());
    }

    @Test
    @DisplayName("Rechazo por contrato: un asistente debe operar una nave y recibir una tripulación")
    public void testAsistenteSinNave() {
        assertThrows(AssertionError.class, () -> new AsistenteComando(null));
        assertThrows(AssertionError.class, () -> asistente.asignarTripulacion(null));
    }

    // --- Auxiliares ---

    private Tripulacion crearTripulacion() throws Exception {
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Kirk", 3, Origen.TERRICOLA));
        integrantes.add(new Consejero("Troi", 2, Origen.VULCANO));
        integrantes.add(new Teniente("Uhura", 5, Origen.MARCIANO));
        integrantes.add(new Alferez("Chekov", 1, Origen.TERRICOLA));
        integrantes.add(new Alferez("Sulu", 0, Origen.MARCIANO));
        return new Tripulacion(integrantes);
    }

    private void assertUltimoEvento(TipoEvento tipo, String texto) {
        List<Evento> eventos = asistente.getEventos();
        Evento ultimo = eventos.get(eventos.size() - 1);
        assertEquals(tipo, ultimo.getTipo());
        assertTrue(ultimo.getDescripcion().contains(texto), "El último evento no contiene: " + texto);
    }
}
