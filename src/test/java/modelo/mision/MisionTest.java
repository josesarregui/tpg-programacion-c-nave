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
 * imprevistos, cada misión cuesta (Anexo; Tabla de Recursos, IV): despegue 4 de combustible, 4 de desgaste y la
 * energía inicial de la misión más 5 del escaneo de largo alcance; por casilla, un escaneo corto (1 de energía), un
 * salto normal (2 de combustible) y 1 de desgaste.
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
        // Despegue (Anexo): 4 de combustible, 10 + 5 de energía y 4 de desgaste; 3 casillas: 6, 3 y 3.
        assertEquals(60 - 4 - 6, asistente.getCombustible());
        assertEquals(80 - 15 - 3, asistente.getEnergia());
        assertEquals(4 + 3, asistente.getDesgaste());
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
        assertEquals(50, informe.getCombustibleFinal(), "El informe muestra el estado antes del cierre");
        assertEquals(67, informe.getEnergiaFinal(), "Despegue 5 + 5 de energía y 3 escaneos");
        assertEquals(50 - combustible, asistente.getCombustible(), "El descuento se hace en el cierre");
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
    @DisplayName("Anexo: los suministros de M-01 van de 0 a 50 de cada recurso; con 0 la asistencia se completa sin entrega")
    public void testRangoSuministrosDelAnexo() throws Exception {
        MisionIntercepcion maxima = new MisionIntercepcion(tablero, destino, azarFijo(true));
        assertEquals(50, maxima.getCombustibleAEntregar());
        assertEquals(50, maxima.getEnergiaAEntregar());

        asistente.asignarTripulacion(tripulacion);
        MisionIntercepcion minima = new MisionIntercepcion(tablero, destino, azarFijo(false));
        assertEquals(0, minima.getCombustibleAEntregar());
        assertEquals(0, minima.getEnergiaAEntregar());

        InformeMision informe = realizar(minima);

        assertTrue(informe.isExitosa());
        assertEquals(0, asistente.getNave().getCombustibleEntregado());
        assertEquals(0, asistente.getNave().getEnergiaEntregada());
    }

    @Test
    @DisplayName("Anexo: M-02 recolecta entre 20 y 100 gemas")
    public void testRangoGemasDelAnexo() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        MisionRecoleccion mision = new MisionRecoleccion(tablero, destino, azarFijo(false));

        assertTrue(realizar(mision).isExitosa());

        assertEquals(20, mision.getGemasObtenidas(), "El mínimo del sorteo es 20");
        assertEquals(20, asistente.getGemas());
        assertEquals(MisionRecoleccion.GEMAS_MINIMAS, mision.getGemasObtenidas());
        assertEquals(100, MisionRecoleccion.GEMAS_MAXIMAS);
    }

    @Test
    @DisplayName("Anexo: el despegue cobra 4 de combustible, 4 de desgaste y la energía inicial de la misión (M-03: 0)")
    public void testConsumoInicialDelAnexo() throws Exception {
        asistente.asignarTripulacion(tripulacion);

        // La nave ya está en la base: M-03 no recorre casillas y sólo paga el despegue.
        InformeMision informe = realizar(new MisionRetorno(tablero, destino));

        assertTrue(informe.isExitosa());
        assertEquals(1, informe.getRuta().size(), "Sin turnos de travesía");
        assertEquals(60 - 4, informe.getCombustibleFinal());
        assertEquals(80 - Mision.ENERGIA_ESCANEO_LARGO, informe.getEnergiaFinal(), "M-03 no tiene energía inicial");
        assertEquals(4, informe.getDesgasteFinal());
        assertEquals(4, informe.getBalance().getCombustibleConsumido());
        assertEquals(4, informe.getBalance().getDesgasteProducido());
    }

    @Test
    @DisplayName("Anexo y Escenario B: sin combustible para el despegue y un salto normal, la misión se rechaza sin cambios")
    public void testEscenarioBCombustibleDelDespegue() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(55, 0, 0); // quedan 5: el despegue (4) y un salto normal (2) suman 6
        tablero.moverNave(new Posicion(5, 4));
        Mision mision = new MisionRetorno(tablero, destino);
        asistente.encomendarMision(mision);

        RecursoInsuficienteException e = assertThrows(RecursoInsuficienteException.class, asistente::ejecutarMision);

        assertEquals(TipoRecurso.COMBUSTIBLE, e.getRecurso());
        assertEquals(6, e.getCantidadSolicitada());
        assertEquals(5, asistente.getCombustible());
        assertEquals(0, asistente.getDesgaste());
        assertEquals(EtapaMision.CREADA, mision.getEtapa());
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
        assertEquals(40 + 4 + 3, informe.getDesgasteFinal(), "El informe muestra el desgaste del arribo");
        assertEquals(0, asistente.getDesgaste(), "El cierre repara el desgaste");
        assertEquals(100, asistente.getCombustible(), "El cierre recarga el combustible");
        assertEquals(100, asistente.getEnergia(), "El cierre recarga la energía");
    }

    @Test
    @DisplayName("Fase 3: el balance de M-03 incluye la recarga y la reparación que aplica el cierre")
    public void testBalanceDelRetorno() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(20, 30, 40); // 40 / 50 / 40
        tablero.moverNave(new Posicion(5, 2));

        InformeMision informe = realizar(new MisionRetorno(tablero, destino));

        // Al llegar: despegue 4 / 5 / 4 y 3 casillas 6 / 3 / 3, es decir 30 / 42 / 47.
        assertTrue(informe.isExitosa());
        assertEquals(30, informe.getCombustibleFinal());
        assertEquals(42, informe.getEnergiaFinal());
        assertEquals(47, informe.getDesgasteFinal());
        BalanceRecursos balance = informe.getBalance();
        assertEquals(100 - 30, balance.getCombustibleRecargado(), "Lo que falta para la capacidad máxima");
        assertEquals(100 - 42, balance.getEnergiaRecargada());
        assertEquals(47, balance.getDesgasteReparado(), "Todo el desgaste al llegar");
        assertEquals(0, balance.getCombustibleGanado(), "La recarga en la base no es una ganancia del trayecto");
        assertEquals(0, balance.getEnergiaGanada());
        // El balance coincide con lo que el cierre aplicó a la nave.
        assertEquals(informe.getCombustibleFinal() + balance.getCombustibleRecargado(), asistente.getCombustible());
        assertEquals(informe.getEnergiaFinal() + balance.getEnergiaRecargada(), asistente.getEnergia());
        assertEquals(informe.getDesgasteFinal() - balance.getDesgasteReparado(), asistente.getDesgaste());
        assertTrue(informe.getObservaciones().contains(
                "recarga en la base (combustible 70, energía 58, desgaste reparado 47)"));
    }

    @Test
    @DisplayName("Fase 3: las misiones que no recargan informan recarga y reparación en 0")
    public void testBalanceSinRecarga() throws Exception {
        asistente.asignarTripulacion(tripulacion);

        InformeMision informe = realizar(new MisionRecoleccion(tablero, destino, new Random(SEMILLA)));

        assertTrue(informe.isExitosa());
        assertEquals(0, informe.getBalance().getCombustibleRecargado());
        assertEquals(0, informe.getBalance().getEnergiaRecargada());
        assertEquals(0, informe.getBalance().getDesgasteReparado());
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
        assertEquals(10, balance.getCombustibleConsumido());
        assertEquals(18, balance.getEnergiaConsumida());
        assertEquals(7, balance.getDesgasteProducido());
        assertEquals(0, balance.getEnergiaPerdida());
        assertEquals(0, balance.getEnergiaGanada());
        assertEquals(10, informe.getCombustibleConsumido(), "Los consumos también se consultan en el informe");
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
    @DisplayName("Escenario B: M-01 exige energía para los suministros; M-03 (sin energía inicial) puede realizarse")
    public void testEscenarioBEnergia() throws Exception {
        asistente.asignarTripulacion(tripulacion);
        asistente.consumirRecursos(0, 70, 0); // quedan 10 de energía
        MisionIntercepcion intercepcion = new MisionIntercepcion(tablero, destino, new Random(SEMILLA));
        asistente.encomendarMision(intercepcion);

        RecursoInsuficienteException e = assertThrows(RecursoInsuficienteException.class, asistente::ejecutarMision);

        assertEquals(TipoRecurso.ENERGIA, e.getRecurso());
        assertEquals(Mision.ENERGIA_ESCANEO_LARGO + MisionIntercepcion.ENERGIA_INICIAL + Mision.ENERGIA_ESCANEO_CORTO
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
        assertEquals(11 - 4 - 6, asistente.getCombustible(), "Despegue y tres saltos normales");
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

    /**
     * Generador que siempre sortea el extremo indicado del rango, para probar los límites sin depender de una semilla.
     */
    private static Random azarFijo(boolean maximo) {
        return new Random() {
            @Override
            public int nextInt(int limite) {
                return maximo ? limite - 1 : 0;
            }
        };
    }

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
