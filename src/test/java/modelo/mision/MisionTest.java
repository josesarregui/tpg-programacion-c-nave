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
import modelo.universo.DestinoSinMovimiento;
import modelo.universo.Posicion;
import modelo.universo.TableroDePrueba;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del ciclo de vida de las misiones (E1-06 y Enunciado E2, Template Method), del informe (E1-10 y Fase 3),
 * de los cierres (Fase 4) y de los Escenarios A y B, sobre un tablero de 10 x 10 vacío y un Destino que no mueve
 * elementos, para que el recorrido sea determinista.
 *
 * Recorrido de referencia: la nave parte del centro (5, 5) y el objetivo está a 3 casillas, en (5, 8). Sin
 * imprevistos, cada misión cuesta (Tabla de Recursos, IV; Anexo): despegue 5 de energía; por casilla, un escaneo
 * corto (1 de energía), un salto normal (2 de combustible) y 1 de desgaste; y la acción final.
 */
public class MisionTest {

    private static final long SEMILLA = 2026L;
    private static final Posicion CENTRO = new Posicion(5, 5);
    private static final Posicion OBJETIVO = new Posicion(5, 8);

    private AsistenteComando asistente;
    private Tripulacion tripulacion;
    private TableroDePrueba tablero;
    private DestinoSinMovimiento destino;

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
        tablero = new TableroDePrueba(10, 10);
        tablero.fijarObjetivo(OBJETIVO);
        destino = new DestinoSinMovimiento(tablero);
    }

    // --- Escenario A y Template Method ---

    @Test
    @DisplayName("Escenario A: M-02 recorre 3 casillas, recolecta gemas y el cierre las acredita")
    public void testRecoleccionExitosa() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        MisionRecoleccion mision = new MisionRecoleccion(tablero, destino, new Random(SEMILLA));

        InformeMision informe = realizar(mision);

        int gemasEsperadas = MisionRecoleccion.GEMAS_MINIMAS
                + new Random(SEMILLA).nextInt(MisionRecoleccion.GEMAS_MAXIMAS - MisionRecoleccion.GEMAS_MINIMAS + 1);
        assertTrue(informe.isExitosa());
        assertEquals(OBJETIVO, tablero.getPosicionNave());
        assertEquals(gemasEsperadas, mision.getGemasObtenidas());
        assertEquals(gemasEsperadas, asistente.getGemas(), "El cierre acredita las gemas (Fase 4)");
        assertEquals(gemasEsperadas, informe.getBalance().getGemasObtenidas());
        // Ejemplo de la Tabla de Recursos (IV.6) con 3 casillas: 6 de combustible, 5 + 3 + 5 de energía, 3 de desgaste.
        assertEquals(54, asistente.getCombustible());
        assertEquals(67, asistente.getEnergia());
        assertEquals(3, asistente.getDesgaste());
        assertEquals(3, destino.getTurnos(), "Un turno de Destino por cada turno de la nave");
    }

    @Test
    @DisplayName("Fase 4: M-01 entrega en el cierre los suministros solicitados por la nave aliada")
    public void testIntercepcionExitosa() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        MisionIntercepcion mision = new MisionIntercepcion(tablero, destino, new Random(SEMILLA));
        int combustible = mision.getCombustibleAEntregar();
        int energia = mision.getEnergiaAEntregar();

        InformeMision informe = realizar(mision);

        assertTrue(informe.isExitosa());
        assertEquals(54, informe.getCombustibleFinal(), "El informe muestra el estado antes del cierre");
        assertEquals(67, informe.getEnergiaFinal());
        assertEquals(54 - combustible, asistente.getCombustible(), "El descuento se hace en el cierre");
        assertEquals(67 - energia, asistente.getEnergia());
        assertEquals(combustible, asistente.getNave().getCombustibleEntregado());
        assertEquals(energia, asistente.getNave().getEnergiaEntregada());
        assertEquals(combustible, informe.getBalance().getCombustibleEntregado());
        assertEquals(energia, informe.getBalance().getEnergiaEntregada());
    }

    @Test
    @DisplayName("Suministros y gemas aleatorios: con la misma semilla son reproducibles y están en el rango")
    public void testValoresAleatoriosEnRango() {
        MisionIntercepcion una = new MisionIntercepcion(tablero, destino, new Random(SEMILLA));
        MisionIntercepcion otra = new MisionIntercepcion(tablero, destino, new Random(SEMILLA));

        assertEquals(una.getCombustibleAEntregar(), otra.getCombustibleAEntregar());
        assertEquals(una.getEnergiaAEntregar(), otra.getEnergiaAEntregar());
        for (int i = 0; i < 50; i++) {
            MisionIntercepcion mision = new MisionIntercepcion(tablero, destino, new Random(i));
            assertTrue(mision.getCombustibleAEntregar() >= MisionIntercepcion.SUMINISTRO_MINIMO
                    && mision.getCombustibleAEntregar() <= MisionIntercepcion.SUMINISTRO_MAXIMO);
            assertTrue(mision.getEnergiaAEntregar() >= MisionIntercepcion.SUMINISTRO_MINIMO
                    && mision.getEnergiaAEntregar() <= MisionIntercepcion.SUMINISTRO_MAXIMO);
        }
    }

    @Test
    @DisplayName("Fase 4: M-03 regresa al centro (base de origen), repara el desgaste y recarga en el cierre")
    public void testRetornoExitoso() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(20, 30, 40);
        tablero.moverNave(new Posicion(5, 2));

        InformeMision informe = realizar(new MisionRetorno(tablero, destino));

        assertTrue(informe.isExitosa());
        assertEquals(CENTRO, informe.getObjetivo());
        assertEquals(CENTRO, tablero.getPosicionNave());
        assertEquals(43, informe.getDesgasteFinal(), "El informe muestra el desgaste del arribo");
        assertEquals(0, asistente.getDesgaste(), "El cierre repara el desgaste");
        assertEquals(100, asistente.getCombustible(), "El cierre recarga el combustible");
        assertEquals(100, asistente.getEnergia(), "El cierre recarga la energía");
    }

    @Test
    @DisplayName("Template Method: los cuatro pasos se realizan en orden; el informe existe desde la evaluación")
    public void testCicloEnOrden() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        Mision mision = new MisionRecoleccion(tablero, destino, new Random(SEMILLA));
        assertEquals(EtapaMision.CREADA, mision.getEtapa());
        assertNull(mision.getInforme());
        assertNull(mision.getObjetivo());

        InformeMision informe = realizar(mision);

        assertEquals(EtapaMision.CERRADA, mision.getEtapa());
        assertSame(informe, mision.getInforme());
        List<String> acciones = informe.getAcciones();
        assertTrue(acciones.get(0).startsWith("Preparación"));
        assertTrue(acciones.get(1).startsWith("Despegue"));
        assertTrue(acciones.get(acciones.size() - 2).startsWith("Ejecución: llegó"));
        assertTrue(acciones.get(acciones.size() - 1).startsWith("Evaluación"));
        assertUltimoEvento("Informe de M-02");
    }

    @Test
    @DisplayName("Fase 3: el informe incluye objetivo, ruta, balance como datos, estado de la nave y observaciones")
    public void testInforme() throws Exception {
        asistente.asignarTripulacion(tripulacion);

        InformeMision informe = realizar(new MisionRecoleccion(tablero, destino, new Random(SEMILLA)));

        assertEquals("M-02 — Recolección", informe.getMision());
        assertEquals(OBJETIVO, informe.getObjetivo());
        assertEquals(4, informe.getRuta().size(), "Partida más 3 casillas");
        assertEquals(CENTRO, informe.getRuta().get(0));
        assertEquals(OBJETIVO, informe.getRuta().get(3));
        BalanceRecursos balance = informe.getBalance();
        assertEquals(6, balance.getCombustibleConsumido());
        assertEquals(13, balance.getEnergiaConsumida());
        assertEquals(3, balance.getDesgasteProducido());
        assertEquals(0, balance.getEnergiaPerdida());
        assertEquals(0, balance.getEnergiaGanada());
        assertEquals(6, informe.getCombustibleConsumido(), "Los consumos también se consultan en el informe");
        assertEquals("Disponible", informe.getEstadoMotorFinal());
        assertTrue(informe.isNaveOperativa());
        assertTrue(informe.getObservaciones().contains("(cumplida)"));
        assertThrows(UnsupportedOperationException.class, () -> informe.getAcciones().add("acción ajena"));
        assertThrows(UnsupportedOperationException.class, () -> informe.getRuta().add(CENTRO));
    }

    @Test
    @DisplayName("E1-05: la Bitácora registra la travesía en orden temporal (turnos, consumos y cierre)")
    public void testRegistroEnBitacora() throws Exception {
        asistente.asignarTripulacion(tripulacion);

        realizar(new MisionRecoleccion(tablero, destino, new Random(SEMILLA)));

        assertFalse(eventosDeTipo(TipoEvento.MISION).isEmpty());
        assertEquals(3, eventosDeTipo(TipoEvento.MOTOR).size(), "Un salto normal por casilla");
        List<Evento> eventos = asistente.getEventos();
        for (int i = 1; i < eventos.size(); i++) {
            assertFalse(eventos.get(i).getFechaHora().isBefore(eventos.get(i - 1).getFechaHora()));
        }
    }

    // --- Escenario B: recursos insuficientes ---

    @Test
    @DisplayName("Escenario B: sin el combustible mínimo la misión se rechaza sin cambios en la nave ni en el tablero")
    public void testEscenarioBCombustible() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(59, 0, 0); // queda 1 de combustible
        Mision mision = new MisionRecoleccion(tablero, destino);
        asistente.encomendarMision(mision);

        RecursoInsuficienteException e = assertThrows(RecursoInsuficienteException.class, asistente::ejecutarMision);

        assertEquals(TipoRecurso.COMBUSTIBLE, e.getRecurso());
        assertEquals(1, e.getDisponible());
        assertEquals(mision.getCombustibleMinimo(), e.getCantidadSolicitada());
        assertEquals(80, asistente.getEnergia());
        assertEquals(CENTRO, tablero.getPosicionNave(), "La nave no se movió");
        assertNull(mision.getObjetivo(), "No se fijó el objetivo");
        assertEquals(EtapaMision.CREADA, mision.getEtapa());
        assertNull(mision.getInforme());
        assertUltimoEventoEsError("rechazada");
    }

    @Test
    @DisplayName("Escenario B: M-01 exige energía para los suministros; M-03 (sin acción final) puede realizarse")
    public void testEscenarioBEnergia() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(0, 70, 0); // quedan 10 de energía
        MisionIntercepcion intercepcion = new MisionIntercepcion(tablero, destino, new Random(SEMILLA));
        asistente.encomendarMision(intercepcion);

        RecursoInsuficienteException e = assertThrows(RecursoInsuficienteException.class, asistente::ejecutarMision);

        assertEquals(TipoRecurso.ENERGIA, e.getRecurso());
        assertEquals(Mision.ENERGIA_ESCANEO_LARGO + Mision.ENERGIA_ESCANEO_CORTO + MisionIntercepcion.ENERGIA_ADICIONAL
                + intercepcion.getEnergiaAEntregar(), e.getCantidadSolicitada());
        assertEquals(60, asistente.getCombustible(), "No se consumió combustible");
        asistente.cancelarMision();
        tablero.moverNave(new Posicion(5, 4));
        assertTrue(realizar(new MisionRetorno(tablero, destino)).isExitosa());
    }

    @Test
    @DisplayName("Escenario B: una misión rechazada puede realizarse después de cargar combustible")
    public void testReintentoTrasCarga() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(59, 0, 0);
        Mision mision = new MisionRecoleccion(tablero, destino, new Random(SEMILLA));
        mision.configurarPausaPorTurno(0);
        asistente.encomendarMision(mision);
        assertThrows(RecursoInsuficienteException.class, asistente::ejecutarMision);

        asistente.cargarCombustible(10);
        InformeMision informe = asistente.ejecutarMision();

        assertTrue(informe.isExitosa());
        assertEquals(5, asistente.getCombustible());
    }

    @Test
    @DisplayName("Aclaración R4: una nave sin tripulación no está disponible y la misión se rechaza")
    public void testNaveSinTripulacion() {
        asistente.encomendarMision(new MisionIntercepcion(tablero, destino));

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
        Mision mision = new MisionRecoleccion(tablero, destino, new Random(SEMILLA));
        mision.configurarPausaPorTurno(0);
        asistente.encomendarMision(mision);

        NaveNoDisponibleException e = assertThrows(NaveNoDisponibleException.class, asistente::ejecutarMision);
        assertTrue(e.requiereMantenimiento());

        asistente.realizarMantenimiento();
        assertTrue(asistente.ejecutarMision().isExitosa());
    }

    @Test
    @DisplayName("Enunciado E2: una nave destruida no está disponible y la misión se rechaza")
    public void testMisionRechazadaConNaveDestruida() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.enfrentarseCon(new NaveFactory().crearNave(TipoNave.COMBATE)); // Exploradora pierde.
        asistente.encomendarMision(new MisionIntercepcion(tablero, destino));

        NaveNoDisponibleException e = assertThrows(NaveNoDisponibleException.class, asistente::ejecutarMision);

        assertTrue(e.isNaveDestruida());
        assertTrue(e.getMessage().contains("destruida: sí"));
        assertTrue(asistente.tieneMisionPendiente());
    }

    // --- Contratos incumplidos (aserciones) ---

    @Test
    @DisplayName("Rechazo por contrato: una misión necesita el tablero y el Destino")
    public void testMisionSinTablero() {
        assertThrows(AssertionError.class, () -> new MisionRetorno(null, destino));
        assertThrows(AssertionError.class, () -> new MisionRetorno(tablero, null));
        assertThrows(AssertionError.class, () -> new MisionRecoleccion(tablero, destino, null));
    }

    @Test
    @DisplayName("Rechazo por contrato: la pausa no es negativa y sólo se configura antes de realizar la misión")
    public void testConfigurarPausa() throws Exception {
        Mision mision = new MisionRetorno(tablero, destino);
        assertEquals(Mision.PAUSA_POR_TURNO_MS, mision.getPausaPorTurno());
        assertThrows(AssertionError.class, () -> mision.configurarPausaPorTurno(-1));

        asistente.asignarTripulacion(tripulacion);
        tablero.moverNave(new Posicion(5, 4));
        realizar(mision);

        assertThrows(AssertionError.class, () -> mision.configurarPausaPorTurno(0));
    }

    @Test
    @DisplayName("Rechazo por contrato: una misión no puede realizarse dos veces")
    public void testMisionRealizadaDosVeces() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        Mision mision = new MisionRecoleccion(tablero, destino, new Random(SEMILLA));
        realizar(mision);
        asistente.encomendarMision(new MisionRetorno(tablero, destino));

        assertThrows(AssertionError.class, mision::realizar);
    }

    @Test
    @DisplayName("Rechazo por contrato (R4): una misión no puede encomendarse a dos asistentes")
    public void testMisionEnDosAsistentes() {
        Mision mision = new MisionIntercepcion(tablero, destino);
        asistente.encomendarMision(mision);
        AsistenteComando otroAsistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.CARGUERO));

        assertThrows(AssertionError.class, () -> otroAsistente.encomendarMision(mision));
    }

    @Test
    @DisplayName("Rechazo por contrato: no se puede realizar una misión que no fue encomendada a un asistente")
    public void testMisionSinAsistente() {
        assertThrows(AssertionError.class, () -> new MisionRetorno(tablero, destino).realizar());
        assertThrows(AssertionError.class, asistente::ejecutarMision);
    }

    @Test
    @DisplayName("Rechazo por contrato (R2): la misión sólo se encomienda y se realiza a través del asistente")
    public void testMisionSinPasarPorElAsistente() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        Mision sinEncomendar = new MisionRetorno(tablero, destino);
        assertThrows(AssertionError.class, () -> sinEncomendar.asignarAsistente(asistente));
        assertNull(sinEncomendar.getAsistente());
        assertFalse(asistente.tieneMisionPendiente());

        Mision mision = new MisionRetorno(tablero, destino);
        mision.configurarPausaPorTurno(0);
        asistente.encomendarMision(mision);
        assertThrows(AssertionError.class, mision::realizar);

        assertEquals(EtapaMision.CREADA, mision.getEtapa(), "La misión no se realizó");
        assertEquals(60, asistente.getCombustible(), "La nave no cambió");
        tablero.moverNave(new Posicion(5, 4));
        assertTrue(asistente.ejecutarMision().isExitosa(), "El asistente sigue pudiendo realizarla");
    }

    @Test
    @DisplayName("Rechazo por contrato: una misión cancelada no puede realizarse")
    public void testMisionCancelada() {
        Mision mision = new MisionIntercepcion(tablero, destino);
        asistente.encomendarMision(mision);
        asistente.cancelarMision();

        assertThrows(AssertionError.class, mision::realizar);
        assertEquals(EtapaMision.CREADA, mision.getEtapa());
    }

    // --- Auxiliares ---

    private InformeMision realizar(Mision mision) throws Exception {
        mision.configurarPausaPorTurno(0);
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

    private void assertUltimoEvento(String texto) {
        List<Evento> eventos = asistente.getEventos();
        assertTrue(eventos.get(eventos.size() - 1).getDescripcion().contains(texto));
    }

    private void assertUltimoEventoEsError(String texto) {
        List<Evento> eventos = asistente.getEventos();
        Evento ultimo = eventos.get(eventos.size() - 1);
        assertEquals(TipoEvento.ERROR, ultimo.getTipo());
        assertTrue(ultimo.getDescripcion().contains(texto));
    }
}
