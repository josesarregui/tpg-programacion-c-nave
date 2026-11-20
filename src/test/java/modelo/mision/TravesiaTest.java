package modelo.mision;

import modelo.asistente.AsistenteComando;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import modelo.universo.Asteroide;
import modelo.universo.Destino;
import modelo.universo.DestinoProgramado;
import modelo.universo.DestinoSinMovimiento;
import modelo.universo.ElementoDelUniverso;
import modelo.universo.EstacionDeCarga;
import modelo.universo.FabricaDeUniverso;
import modelo.universo.Posicion;
import modelo.universo.Tablero;
import modelo.universo.TableroDePrueba;
import modelo.warp.MotorWarp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del ciclo de travesía (Enunciado E2, Fase 2) sobre tableros de 10 x 10 con elementos ubicados por la
 * prueba: interacciones con asteroides, estaciones y naves, destrucción durante el recorrido, recálculo de la ruta
 * con el radar de corto alcance, salto warp de dos casillas, verificación en destino de M-01, transición entre
 * casillas válidas e interrupción del hilo. También prueba el {@link Navegador}.
 */
public class TravesiaTest {

    private static final Posicion CENTRO = new Posicion(5, 5);
    private static final Posicion OBJETIVO = new Posicion(5, 8);

    private AsistenteComando asistente;

    @BeforeEach
    public void inicializar() throws Exception {
        // Exploradora: combustible 60, energía 80, desgaste 0.
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Janeway", 5, Origen.TERRICOLA));
        integrantes.add(new Consejero("Tuvok", 8, Origen.VULCANO));
        integrantes.add(new Teniente("Paris", 3, Origen.MARCIANO));
        integrantes.add(new Alferez("Kim", 0, Origen.TERRICOLA));
        integrantes.add(new Alferez("Torres", 1, Origen.MARCIANO));
        asistente.asignarTripulacion(new Tripulacion(integrantes));
    }

    // --- Interacciones (Fase 2, A.1) ---

    @Test
    @DisplayName("Asteroide en la casilla de la nave: el impacto queda en el balance como energía perdida")
    public void testImpactoDeAsteroide() throws Exception {
        TableroDePrueba tablero = tableroCon(CENTRO, new Asteroide());

        InformeMision informe = realizar(new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1)));

        assertTrue(informe.isExitosa());
        assertEquals(Asteroide.DANIO_ENERGIA, informe.getBalance().getEnergiaPerdida());
        assertEquals(80 - 13 - Asteroide.DANIO_ENERGIA, asistente.getEnergia());
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.contains("encuentro con Asteroide")));
    }

    @Test
    @DisplayName("Destrucción durante el recorrido: el impacto deja la energía en 0, la misión se aborta y falla")
    public void testDestruccionPorAsteroide() throws Exception {
        asistente.consumirRecursos(0, 60, 0); // energía 20: despegue 5 -> 15; el impacto de 25 la deja en 0
        TableroDePrueba tablero = tableroCon(CENTRO, new Asteroide());
        MisionRecoleccion mision = new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1));

        InformeMision informe = realizar(mision);

        assertTrue(asistente.naveDestruida());
        assertFalse(informe.isExitosa());
        assertFalse(informe.isNaveOperativa());
        assertEquals(CENTRO, tablero.getPosicionNave(), "La travesía terminó en el turno del impacto");
        assertEquals(15, informe.getBalance().getEnergiaPerdida());
        assertEquals(0, informe.getBalance().getGemasObtenidas());
        assertEquals(0, asistente.getGemas(), "El cierre no acredita gemas de una misión fallida");
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.startsWith("Ejecución abortada")));
    }

    @Test
    @DisplayName("Estación de carga en la casilla de la nave: el reabastecimiento queda como ganado, con tope 100")
    public void testEstacionDeCarga() throws Exception {
        TableroDePrueba tablero = tableroCon(CENTRO, new EstacionDeCarga());

        InformeMision informe = realizar(new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1)));

        assertTrue(informe.isExitosa());
        // Despegue: energía 75. La estación suma 30 de combustible (60 -> 90) y 25 de energía (tope 100).
        assertEquals(EstacionDeCarga.RECARGA_COMBUSTIBLE, informe.getBalance().getCombustibleGanado());
        assertEquals(25, informe.getBalance().getEnergiaGanada());
    }

    @Test
    @DisplayName("Batalla perdida en el recorrido (Double Dispatch): la nave y su tripulación mueren y la misión falla")
    public void testBatallaPerdida() throws Exception {
        TableroDePrueba tablero = tableroCon(CENTRO, new NaveFactory().crearNave(TipoNave.COMBATE));

        InformeMision informe = realizar(new MisionIntercepcion(tablero, new DestinoSinMovimiento(tablero), new Random(1)));

        assertTrue(asistente.naveDestruida());
        assertFalse(asistente.getNave().tripulacionConVida());
        assertFalse(informe.isExitosa());
        assertEquals(0, asistente.getNave().getCombustibleEntregado(), "Sin éxito no hay entrega en el cierre");
    }

    @Test
    @DisplayName("Batalla ganada en el recorrido: la nave sigue y completa la misión")
    public void testBatallaGanada() throws Exception {
        TableroDePrueba tablero = tableroCon(CENTRO, new NaveFactory().crearNave(TipoNave.CARGUERO));

        InformeMision informe = realizar(new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1)));

        assertFalse(asistente.naveDestruida());
        assertTrue(informe.isExitosa());
    }

    // --- Radares, planificación y transición entre casillas (Fase 1 y Fase 2, A.2 y A.3) ---

    @Test
    @DisplayName("Radar de largo alcance: la ruta inicial evita las amenazas y prioriza las estaciones")
    public void testRutaEvitaAmenazas() throws Exception {
        Map<Posicion, ElementoDelUniverso> ubicaciones = new LinkedHashMap<>();
        for (int fila = 4; fila <= 6; fila++) {
            ubicaciones.put(new Posicion(fila, 6), new Asteroide());
        }
        TableroDePrueba tablero = new TableroDePrueba(10, 10, ubicaciones);
        tablero.fijarObjetivo(OBJETIVO);

        InformeMision informe = realizar(new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1)));

        assertTrue(informe.isExitosa());
        assertEquals(0, informe.getBalance().getEnergiaPerdida(), "No pasó por ningún asteroide");
        for (Posicion paso : informe.getRuta()) {
            assertFalse(ubicaciones.containsKey(paso), "La ruta no atraviesa amenazas: " + paso);
        }
        assertRecorridoValido(informe.getRuta(), tablero);
    }

    @Test
    @DisplayName("Radar de corto alcance: un asteroide que Destino cruza en la ruta obliga a recalcular (2 de energía)")
    public void testRecalculoPorAmenaza() throws Exception {
        Posicion lejos = new Posicion(0, 0);
        TableroDePrueba tablero = tableroCon(lejos, new Asteroide());
        List<Posicion> ruta = new Navegador().calcularRuta(tablero.verTablero(), CENTRO, OBJETIVO);
        Posicion segundoPaso = ruta.get(1);
        Destino destino = new DestinoProgramado(tablero, 1, lejos, segundoPaso);

        InformeMision informe = realizar(new MisionRecoleccion(tablero, destino, new Random(1)));

        assertTrue(informe.isExitosa());
        assertFalse(informe.getRuta().contains(segundoPaso), "La nave esquivó el asteroide");
        assertEquals(0, informe.getBalance().getEnergiaPerdida());
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.contains("recálculo de la ruta")));
        assertEquals(5 + 3 + Mision.ENERGIA_RECALCULO + MisionRecoleccion.ENERGIA_ADICIONAL,
                informe.getBalance().getEnergiaConsumida());
        assertRecorridoValido(informe.getRuta(), tablero);
    }

    @Test
    @DisplayName("Transición entre casillas: desde un borde, cada paso queda dentro del tablero y es vecino del anterior")
    public void testTransicionDesdeElBorde() throws Exception {
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.moverNave(new Posicion(0, 9));

        InformeMision informe = realizar(new MisionRetorno(tablero, new DestinoSinMovimiento(tablero)));

        assertTrue(informe.isExitosa());
        assertEquals(new Posicion(0, 9), informe.getRuta().get(0));
        assertEquals(CENTRO, informe.getRuta().get(informe.getRuta().size() - 1));
        assertEquals(5, informe.getRuta().size() - 1, "Distancia de Chebyshev de (0, 9) a (5, 5)");
        assertRecorridoValido(informe.getRuta(), tablero);
    }

    @Test
    @DisplayName("Salto warp (Anexo): sin combustible para un salto normal, la maniobra avanza 2 casillas en un turno")
    public void testSaltoWarpDosCasillas() throws Exception {
        asistente.consumirRecursos(58, 0, 0); // combustible 2: un salto normal la destruiría
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.moverNave(new Posicion(5, 3));

        InformeMision informe = realizar(new MisionRetorno(tablero, new DestinoSinMovimiento(tablero)));

        assertTrue(informe.isExitosa());
        assertEquals(3, informe.getRuta().size(), "Partida más 2 casillas");
        assertRecorridoValido(informe.getRuta(), tablero);
        long saltosWarp = informe.getAcciones().stream().filter(a -> a.contains("Turno 3: salto warp a")).count();
        assertEquals(2, saltosWarp, "Las dos casillas se avanzan en el mismo turno");
        assertEquals("Disponible", informe.getEstadoMotorFinal(), "Tras el arribo se completa el enfriamiento");
        BalanceRecursos balance = informe.getBalance();
        assertEquals(0, balance.getCombustibleConsumido());
        // Despegue 5, tres escaneos de turno y uno por la segunda casilla, más el salto warp.
        assertEquals(5 + 4 + MotorWarp.ENERGIA_SALTO_WARP, balance.getEnergiaConsumida());
        assertEquals(2 + MotorWarp.DESGASTE_ENFRIAMIENTO, balance.getDesgasteProducido());
    }

    @Test
    @DisplayName("Sin combustible para saltar ni energía para la maniobra warp, la misión se aborta sin destruir la nave")
    public void testSinRecursosParaAvanzar() throws Exception {
        asistente.consumirRecursos(58, 69, 0); // combustible 2, energía 11
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.moverNave(new Posicion(5, 2));

        InformeMision informe = realizar(new MisionRetorno(tablero, new DestinoSinMovimiento(tablero)));

        assertFalse(informe.isExitosa());
        assertFalse(asistente.naveDestruida());
        assertEquals(new Posicion(5, 2), tablero.getPosicionNave());
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.contains("no tiene recursos para avanzar")));
    }

    // --- Verificación en destino (Fase 2, punto 3) ---

    @Test
    @DisplayName("M-01 fallida: si la nave consumió en el viaje los suministros a entregar, no hay entrega")
    public void testIntercepcionSinSuministrosAlLlegar() throws Exception {
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.fijarObjetivo(OBJETIVO);
        MisionIntercepcion mision = new MisionIntercepcion(tablero, new DestinoSinMovimiento(tablero), new Random(7));
        // Combustible justo para iniciar: los tres saltos (6) consumen parte de lo que debía entregar.
        asistente.consumirRecursos(60 - mision.getCombustibleMinimo(), 0, 0);

        InformeMision informe = realizar(mision);

        assertEquals(OBJETIVO, tablero.getPosicionNave(), "La nave llegó");
        assertFalse(informe.isExitosa());
        assertFalse(asistente.naveDestruida());
        assertEquals(mision.getCombustibleAEntregar() - 4, asistente.getCombustible(), "El cierre no descontó nada");
        assertEquals(0, asistente.getNave().getCombustibleEntregado());
        assertEquals(0, informe.getBalance().getCombustibleEntregado());
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.startsWith("Verificación en destino")));
    }

    // --- Concurrencia: interrupción de la pausa ---

    @Test
    @DisplayName("Thread.sleep interrumpido: se restaura la interrupción, termina la travesía y la misión falla")
    public void testTravesiaInterrumpida() throws Exception {
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.fijarObjetivo(OBJETIVO);
        Mision mision = new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1));
        mision.configurarPausaPorTurno(1);
        asistente.encomendarMision(mision);

        Thread.currentThread().interrupt();
        InformeMision informe = asistente.ejecutarMision();
        boolean interrumpido = Thread.interrupted(); // consulta y limpia la marca para las demás pruebas

        assertTrue(interrumpido, "La interrupción no se silencia");
        assertFalse(informe.isExitosa());
        assertEquals(2, informe.getRuta().size(), "Terminó en la pausa del primer turno");
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.contains("se interrumpió")));
    }

    @Test
    @DisplayName("Cancelación en el turno de llegada: la misión falla y el cierre no aplica nada")
    public void testCancelacionEnElTurnoDeLlegada() throws Exception {
        Posicion objetivoVecino = new Posicion(5, 6); // a una casilla de la nave: llega en el primer turno
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.fijarObjetivo(objetivoVecino);
        Mision mision = new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1));
        mision.configurarPausaPorTurno(1);
        asistente.encomendarMision(mision);

        Thread.currentThread().interrupt();
        InformeMision informe = asistente.ejecutarMision();
        boolean interrumpido = Thread.interrupted(); // consulta y limpia la marca para las demás pruebas

        assertTrue(interrumpido, "La interrupción no se silencia");
        assertEquals(objetivoVecino, tablero.getPosicionNave(), "La nave llegó en el turno de la cancelación");
        assertFalse(informe.isExitosa());
        assertEquals(0, informe.getBalance().getGemasObtenidas());
        assertEquals(0, asistente.getGemas(), "El cierre no acredita gemas");
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.contains("se interrumpió")));
        assertTrue(informe.getAcciones().stream().noneMatch(a -> a.startsWith("Ejecución: llegó")),
                "No se realiza la acción de la misión");
    }

    @Test
    @DisplayName("Cancelación durante la maniobra warp: se completa la maniobra y la nave queda lista para operar")
    public void testCancelacionDuranteLaManiobraWarp() throws Exception {
        asistente.consumirRecursos(58, 0, 0); // combustible 2: sólo puede avanzar con la maniobra warp
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.fijarObjetivo(new Posicion(5, 9));
        Mision mision = new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1));
        mision.configurarPausaPorTurno(1);
        asistente.encomendarMision(mision);

        Thread.currentThread().interrupt();
        InformeMision informe = asistente.ejecutarMision();
        boolean interrumpido = Thread.interrupted(); // consulta y limpia la marca para las demás pruebas

        assertTrue(interrumpido, "La interrupción no se silencia");
        assertFalse(informe.isExitosa());
        assertFalse(asistente.naveDestruida());
        assertEquals("Disponible", asistente.getEstadoMotor(), "La maniobra iniciada se completa");
        assertTrue(asistente.naveListaParaOperar(), "La nave puede realizar otra misión");
        assertEquals(3, informe.getRuta().size(), "Partida más las 2 casillas del salto warp");
        assertRecorridoValido(informe.getRuta(), tablero);
        assertEquals(1, informe.getAcciones().stream().filter(a -> a.contains("se interrumpió")).count());
    }

    // --- Recursos para avanzar sin destruir la nave ---

    @ParameterizedTest(name = "energía {0} tras el despegue")
    @ValueSource(ints = {7, 8, 9, 10, 11})
    @DisplayName("Sin combustible para un salto normal: la maniobra warp sólo se inicia si la energía alcanza para completarla")
    public void testManiobraWarpSinEnergiaParaCompletarla(int energiaTrasDespegue) throws Exception {
        // combustible 2 y la energía indicada después del despegue
        asistente.consumirRecursos(58, 80 - (energiaTrasDespegue + Mision.ENERGIA_ESCANEO_LARGO), 0);
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.fijarObjetivo(new Posicion(5, 9));

        InformeMision informe = realizar(new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1)));

        assertFalse(informe.isExitosa());
        assertFalse(asistente.naveDestruida(), "La misión no destruye la nave con sus propios consumos");
        assertEquals("Disponible", asistente.getEstadoMotor());
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.contains("no tiene recursos para avanzar")));
    }

    @Test
    @DisplayName("Sin energía para el escaneo de corto alcance, la misión se aborta antes de escanear, sin destruir la nave")
    public void testSinEnergiaParaEscanear() throws Exception {
        asistente.consumirRecursos(0, 80 - (6 + Mision.ENERGIA_ESCANEO_LARGO), 0); // 6 de energía tras el despegue
        TableroDePrueba tablero = new TableroDePrueba(20, 20);
        tablero.fijarObjetivo(new Posicion(10, 19)); // 9 casillas: no alcanza la energía para escanear cada turno

        InformeMision informe = realizar(new MisionRecoleccion(tablero, new DestinoSinMovimiento(tablero), new Random(1)));

        assertFalse(informe.isExitosa());
        assertFalse(asistente.naveDestruida(), "El escaneo no deja la energía en 0");
        assertEquals(1, asistente.getEnergia());
        assertEquals(6, informe.getRuta().size(), "Partida más una casilla por cada escaneo posible");
        assertRecorridoValido(informe.getRuta(), tablero);
        assertTrue(informe.getAcciones().stream().anyMatch(a -> a.contains("no tiene recursos para avanzar")));
    }

    // --- Universo de 20 x 20 (Guía 4.2) con el Destino de la cátedra ---

    @Test
    @DisplayName("Universo de 20 x 20 con el Destino real: la misión termina con un resultado e informe válidos")
    public void testUniversoReal() throws Exception {
        Tablero tablero = new FabricaDeUniverso().crearTablero();
        asistente.reabastecer(100, 100);

        InformeMision informe = realizar(new MisionRecoleccion(tablero, new Destino(tablero)));

        assertEquals(20, tablero.getX());
        assertNotNull(informe.getObjetivo());
        if (informe.isExitosa()) {
            assertEquals(informe.getObjetivo(), tablero.getPosicionNave());
            assertFalse(asistente.naveDestruida());
        }
        assertEquals(EtapaMision.CERRADA, asistente.getMisionesRealizadas().get(0).getEtapa());
        assertRecorridoValido(informe.getRuta(), tablero);
    }

    // --- Navegador ---

    @Test
    @DisplayName("Navegador: si no hay ruta sin amenazas, devuelve la más corta atravesándolas")
    public void testNavegadorSinRutaSegura() {
        ElementoDelUniverso[][] mapa = new ElementoDelUniverso[5][5];
        for (int fila = 0; fila < 5; fila++) {
            mapa[fila][2] = new Asteroide();
        }

        List<Posicion> ruta = new Navegador().calcularRuta(mapa, new Posicion(2, 0), new Posicion(2, 4));

        assertEquals(4, ruta.size());
        assertEquals(new Posicion(2, 4), ruta.get(ruta.size() - 1));
    }

    @Test
    @DisplayName("Navegador: sin desplazamiento, la ruta está vacía")
    public void testNavegadorMismaPosicion() {
        Posicion medio = new Posicion(2, 2);
        assertTrue(new Navegador().calcularRuta(new ElementoDelUniverso[5][5], medio, medio).isEmpty());
    }

    // --- Auxiliares ---

    private TableroDePrueba tableroCon(Posicion posicion, ElementoDelUniverso elemento) throws Exception {
        Map<Posicion, ElementoDelUniverso> ubicaciones = new LinkedHashMap<>();
        ubicaciones.put(posicion, elemento);
        TableroDePrueba tablero = new TableroDePrueba(10, 10, ubicaciones);
        tablero.fijarObjetivo(OBJETIVO);
        return tablero;
    }

    private InformeMision realizar(Mision mision) throws Exception {
        mision.configurarPausaPorTurno(0);
        asistente.encomendarMision(mision);
        return asistente.ejecutarMision();
    }

    /**
     * Cada posición está dentro del tablero y, salvo la primera, es vecina de la anterior.
     */
    private void assertRecorridoValido(List<Posicion> ruta, Tablero tablero) {
        for (int i = 0; i < ruta.size(); i++) {
            Posicion paso = ruta.get(i);
            assertTrue(paso.getX() >= 0 && paso.getX() < tablero.getX() && paso.getY() >= 0 && paso.getY() < tablero.getY(),
                    "Fuera del tablero: " + paso);
            if (i > 0) {
                Posicion anterior = ruta.get(i - 1);
                assertEquals(1, Math.max(Math.abs(paso.getX() - anterior.getX()), Math.abs(paso.getY() - anterior.getY())),
                        "Paso no vecino: " + anterior + " -> " + paso);
            }
        }
    }
}
