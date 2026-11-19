package modelo.universo;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import modelo.asistente.Asistente;
import modelo.asistente.AsistenteComando;
import modelo.nave.NaveFactory;
import modelo.nave.ResultadoBatalla;
import modelo.nave.TipoNave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TableroYDestinoTest {

    private NaveFactory naveFactory;

    @BeforeEach
    public void setUp() {
        naveFactory = new NaveFactory();
    }

    @Test
    @DisplayName("Tablero inicializa la nave en el centro y objetivo nulo")
    public void testInicializacionTablero() throws Exception {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        elementos.add(new Asteroide());
        elementos.add(new EstacionDeCarga());

        Tablero tablero = new Tablero(10, 10, elementos);

        assertEquals(10, tablero.getX());
        assertEquals(10, tablero.getY());

        Posicion posInicial = tablero.getPosicionNave();
        assertEquals(5, posInicial.getX());
        assertEquals(5, posInicial.getY());
        assertNull(tablero.getObjetivo());

        Posicion objetivo = tablero.obtenerObjetivo();
        assertNotNull(objetivo);
        assertEquals(objetivo, tablero.getObjetivo());
    }

    @Test
    @DisplayName("Tablero notifica a observadores al mover la nave")
    public void testObserverNotificacionesAlMoverNave() throws Exception {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        Tablero tablero = new Tablero(10, 10, elementos);

        ArrayList<String> mensajesRecibidos = new ArrayList<>();
        tablero.agregarObservador(mensajesRecibidos::add);

        tablero.moverNave(new Posicion(6, 6));

        assertEquals(1, mensajesRecibidos.size());
        assertTrue(mensajesRecibidos.get(0).contains("(6, 6)"));
        assertEquals(6, tablero.getPosicionNave().getX());
        assertEquals(6, tablero.getPosicionNave().getY());
    }

    @Test
    @DisplayName("Destino ejecuta turno de movimiento aleatorio y notifica a observadores")
    public void testDestinoTurnoMovimiento() throws Exception {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        elementos.add(new Asteroide());
        elementos.add(new EstacionDeCarga());

        Tablero tablero = new Tablero(10, 10, elementos);
        Destino destino = new Destino(tablero);

        ArrayList<String> avisos = new ArrayList<>();
        tablero.agregarObservador(avisos::add);

        destino.turnoDestino();

        assertFalse(avisos.isEmpty());
        assertTrue(avisos.get(0).contains("Finalizó el turno de Destino"));
    }

    @Test
    @DisplayName("Las naves enemigas del tablero son naves de la fábrica y la nave propia interactúa con ellas")
    public void testNavesDeLaFabricaComoElementosDelTablero() throws Exception {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        elementos.add(naveFactory.crearNave(TipoNave.EXPLORADORA));
        elementos.add(naveFactory.crearNave(TipoNave.CARGUERO));
        elementos.add(naveFactory.crearNave(TipoNave.COMBATE));

        Tablero tablero = new Tablero(10, 10, elementos);

        int naves = 0;
        for (ElementoDelUniverso[] fila : tablero.verTablero()) {
            for (ElementoDelUniverso elemento : fila) {
                naves += elemento == null ? 0 : 1;
            }
        }
        assertEquals(3, naves);

        // Batalla: una Exploradora propia gana al Carguero enemigo y sigue operativa.
        Asistente asistente = new AsistenteComando(naveFactory.crearNave(TipoNave.EXPLORADORA));
        elementos.get(1).interactuarCon(asistente);
        assertFalse(asistente.naveDestruida());
        assertTrue(asistente.getEventos().get(asistente.getEventos().size() - 1).getDescripcion()
                .contains(ResultadoBatalla.GANA.getDescripcion()));
    }

    // ---- Contrato del módulo de la cátedra que usan los radares (Documentación de Destino y Tablero) ----

    @Test
    @DisplayName("Radar de corto alcance en los bordes: fuera del tablero hay null, los vecinos ocupan su casilla y la nave está en [1][1]")
    public void testObservarAlrededoresEnLosBordes() throws Exception {
        Asteroide derecha = new Asteroide();
        EstacionDeCarga abajo = new EstacionDeCarga();
        Asteroide diagonal = new Asteroide();
        Asteroide esquinaOpuesta = new Asteroide();
        Map<Posicion, ElementoDelUniverso> ubicaciones = new LinkedHashMap<>();
        ubicaciones.put(new Posicion(0, 1), derecha);
        ubicaciones.put(new Posicion(1, 0), abajo);
        ubicaciones.put(new Posicion(1, 1), diagonal);
        ubicaciones.put(new Posicion(8, 8), esquinaOpuesta);
        TableroDePrueba tablero = new TableroDePrueba(10, 10, ubicaciones);

        tablero.moverNave(new Posicion(0, 0));
        ElementoDelUniverso[][] alrededores = tablero.observarAlrededores();

        assertEquals(3, alrededores.length);
        for (int i = 0; i < 3; i++) {
            assertEquals(3, alrededores[i].length);
            assertNull(alrededores[0][i], "La fila -1 está fuera del tablero.");
            assertNull(alrededores[i][0], "La columna -1 está fuera del tablero.");
        }
        assertNull(alrededores[1][1], "La casilla de la nave está libre.");
        assertSame(derecha, alrededores[1][2]);
        assertSame(abajo, alrededores[2][1]);
        assertSame(diagonal, alrededores[2][2]);

        alrededores[1][2] = null;
        assertSame(derecha, tablero.observarAlrededores()[1][2], "Modificar la matriz no modifica el tablero.");

        tablero.moverNave(new Posicion(9, 9));
        alrededores = tablero.observarAlrededores();
        for (int i = 0; i < 3; i++) {
            assertNull(alrededores[2][i], "La fila 10 está fuera del tablero.");
            assertNull(alrededores[i][2], "La columna 10 está fuera del tablero.");
        }
        assertSame(esquinaOpuesta, alrededores[0][0]);
    }

    @Test
    @DisplayName("Radar de largo alcance: verTablero es una matriz nueva de filas x columnas, sin la nave ni el objetivo")
    public void testVerTablero() throws Exception {
        Asteroide asteroide = new Asteroide();
        EstacionDeCarga estacion = new EstacionDeCarga();
        Map<Posicion, ElementoDelUniverso> ubicaciones = new LinkedHashMap<>();
        ubicaciones.put(new Posicion(0, 7), asteroide);
        ubicaciones.put(new Posicion(5, 0), estacion);
        TableroDePrueba tablero = new TableroDePrueba(6, 8, ubicaciones);
        Posicion objetivo = tablero.obtenerObjetivo();

        ElementoDelUniverso[][] matriz = tablero.verTablero();

        assertEquals(6, matriz.length, "Primera dimensión: filas (x).");
        int ocupadas = 0;
        for (ElementoDelUniverso[] fila : matriz) {
            assertEquals(8, fila.length, "Segunda dimensión: columnas (y).");
            for (ElementoDelUniverso elemento : fila) {
                ocupadas += elemento == null ? 0 : 1;
            }
        }
        assertEquals(2, ocupadas, "Sólo los elementos: ni la nave ni el objetivo.");
        assertSame(asteroide, matriz[0][7]);
        assertSame(estacion, matriz[5][0]);
        assertNull(matriz[tablero.getPosicionNave().getX()][tablero.getPosicionNave().getY()]);
        assertNull(matriz[objetivo.getX()][objetivo.getY()]);

        matriz[0][7] = null;
        assertSame(asteroide, tablero.verTablero()[0][7], "Modificar la matriz no modifica el tablero.");
    }

    @Test
    @DisplayName("Construcción: dimensiones fuera de 5 a 100 y más de x · y / 4 · 3 elementos se rechazan con las excepciones de la cátedra")
    public void testDimensionesYCapacidad() throws Exception {
        ArrayList<ElementoDelUniverso> vacia = new ArrayList<>();
        assertThrows(DimensionesDeTableroInconsistentesException.class, () -> new Tablero(4, 10, vacia));
        assertThrows(DimensionesDeTableroInconsistentesException.class, () -> new Tablero(10, 4, vacia));
        assertThrows(DimensionesDeTableroInconsistentesException.class, () -> new Tablero(101, 10, vacia));
        assertThrows(DimensionesDeTableroInconsistentesException.class, () -> new Tablero(10, 101, vacia));
        assertEquals(5, new Tablero(5, 100, vacia).getX());

        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            elementos.add(new Asteroide());
        }
        assertEquals(18, contarElementos(new Tablero(5, 5, elementos)), "5 x 5 admite 25 / 4 · 3 = 18 elementos.");

        elementos.add(new Asteroide());
        CantidadDeElementosSuperaCasillaException e = assertThrows(CantidadDeElementosSuperaCasillaException.class,
                () -> new Tablero(5, 5, elementos));
        assertEquals(18, e.getCantidadMaxima());
    }

    @Test
    @DisplayName("Objetivo: obtenerObjetivo elige una casilla libre a distancia de Chebyshev 2 o más de la nave, la guarda y notifica; getObjetivo no notifica")
    public void testObtenerObjetivo() throws Exception {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            elementos.add(new Asteroide());
        }
        Tablero tablero = new Tablero(10, 10, elementos);
        ArrayList<String> avisos = new ArrayList<>();
        tablero.agregarObservador(avisos::add);

        for (int intento = 1; intento <= 20; intento++) {
            Posicion objetivo = tablero.obtenerObjetivo();
            Posicion nave = tablero.getPosicionNave();

            assertNull(tablero.verTablero()[objetivo.getX()][objetivo.getY()], "El objetivo está en una casilla libre.");
            assertTrue(Math.max(Math.abs(objetivo.getX() - nave.getX()), Math.abs(objetivo.getY() - nave.getY())) >= 2);
            assertEquals(objetivo, tablero.getObjetivo());
            assertEquals(intento, avisos.size());
            assertEquals("Se estableció el objetivo en la posición (" + objetivo.getX() + ", " + objetivo.getY() + ").",
                    avisos.get(intento - 1));
        }
        tablero.getObjetivo();
        assertEquals(20, avisos.size(), "Consultar el objetivo no notifica.");
    }

    @Test
    @DisplayName("Turno de Destino: mueve a lo sumo la mitad, a casillas vecinas dentro del tablero y nunca al objetivo, conserva los elementos y avisa \"M de N\"")
    public void testTurnoDeDestinoRespetaElTablero() throws Exception {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            elementos.add(new Asteroide());
        }
        Tablero tablero = new Tablero(10, 10, elementos);
        Posicion objetivo = tablero.obtenerObjetivo();
        Destino destino = new Destino(tablero);
        ArrayList<String> avisos = new ArrayList<>();
        tablero.agregarObservador(avisos::add);

        for (int turno = 1; turno <= 30; turno++) {
            Map<ElementoDelUniverso, Posicion> antes = posicionesPorElemento(tablero);

            destino.turnoDestino();

            Map<ElementoDelUniverso, Posicion> despues = posicionesPorElemento(tablero);
            assertEquals(40, despues.size(), "No se pierde ni se duplica ningún elemento.");
            int movidos = 0;
            for (Map.Entry<ElementoDelUniverso, Posicion> entrada : despues.entrySet()) {
                Posicion anterior = antes.get(entrada.getKey());
                Posicion actual = entrada.getValue();
                assertNotNull(anterior);
                assertNotEquals(objetivo, actual, "Ningún elemento ocupa el objetivo.");
                assertTrue(actual.getX() >= 0 && actual.getX() < 10 && actual.getY() >= 0 && actual.getY() < 10);
                int distancia = Math.max(Math.abs(actual.getX() - anterior.getX()), Math.abs(actual.getY() - anterior.getY()));
                assertTrue(distancia <= 1, "Cada elemento se mueve, como mucho, a una casilla vecina.");
                movidos += distancia;
            }
            assertTrue(movidos <= 20, "Se selecciona la mitad entera de los elementos.");
            assertEquals(turno, avisos.size());
            assertEquals("Finalizó el turno de Destino: se movieron " + movidos + " de 20 elementos seleccionados.",
                    avisos.get(turno - 1));
        }
    }

    private static int contarElementos(Tablero tablero) {
        return tablero.obtenerElementos().size();
    }

    /** Posición de cada elemento, comparando los elementos por identidad (cada asteroide es un objeto distinto). */
    private static Map<ElementoDelUniverso, Posicion> posicionesPorElemento(Tablero tablero) {
        Map<ElementoDelUniverso, Posicion> posiciones = new IdentityHashMap<>();
        for (Map.Entry<Posicion, ElementoDelUniverso> ocupada : tablero.obtenerElementos().entrySet()) {
            posiciones.put(ocupada.getValue(), ocupada.getKey());
        }
        return posiciones;
    }

}
