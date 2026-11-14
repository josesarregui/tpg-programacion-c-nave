package modelo.nave;

import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import modelo.universo.ElementoDelUniverso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del Enunciado E2, punto A (Batallas): la nave implementa ElementoDelUniverso, el resultado se
 * resuelve por Double Dispatch según la tabla de enfrentamientos (nave propia vs. enemiga) y la derrota
 * destruye a la nave propia junto con su tripulación, con registro en la Bitácora.
 */
public class BatallaTest {

    private NaveFactory fabrica;

    @BeforeEach
    public void inicializar() {
        fabrica = new NaveFactory();
    }

    // --- Double Dispatch: matriz completa de enfrentamientos ---

    @ParameterizedTest(name = "{0} propia vs. {1} enemiga: {2}")
    @CsvSource({
            "EXPLORADORA, EXPLORADORA, PACTO_DE_PAZ",
            "EXPLORADORA, CARGUERO,    GANA",
            "EXPLORADORA, COMBATE,     PIERDE",
            "CARGUERO,    EXPLORADORA, PIERDE",
            "CARGUERO,    CARGUERO,    PACTO_DE_PAZ",
            "CARGUERO,    COMBATE,     GANA",
            "COMBATE,     EXPLORADORA, GANA",
            "COMBATE,     CARGUERO,    PIERDE",
            "COMBATE,     COMBATE,     PACTO_DE_PAZ"
    })
    @DisplayName("Enunciado E2: la tabla de enfrentamientos se cumple por Double Dispatch")
    public void testMatrizDeEnfrentamientos(TipoNave propia, TipoNave enemiga, ResultadoBatalla esperado) {
        assertEquals(esperado, fabrica.crearNave(propia).enfrentarseCon(fabrica.crearNave(enemiga)));
    }

    @ParameterizedTest(name = "{0} vs. {1}")
    @CsvSource({
            "EXPLORADORA, CARGUERO",
            "CARGUERO,    COMBATE",
            "COMBATE,     EXPLORADORA"
    })
    @DisplayName("La tabla es antisimétrica: si una nave gana a otra, la otra pierde contra ella")
    public void testAntisimetria(TipoNave ganadora, TipoNave perdedora) {
        assertEquals(ResultadoBatalla.GANA, fabrica.crearNave(ganadora).enfrentarseCon(fabrica.crearNave(perdedora)));
        assertEquals(ResultadoBatalla.PIERDE, fabrica.crearNave(perdedora).enfrentarseCon(fabrica.crearNave(ganadora)));
    }

    @Test
    @DisplayName("Resolver la batalla no modifica ninguna de las dos naves: sólo devuelve el resultado")
    public void testEnfrentarseNoModificaLasNaves() {
        Nave propia = fabrica.crearNave(TipoNave.EXPLORADORA);
        Nave enemiga = fabrica.crearNave(TipoNave.COMBATE);
        String propiaAntes = propia.toString();
        String enemigaAntes = enemiga.toString();

        assertEquals(ResultadoBatalla.PIERDE, propia.enfrentarseCon(enemiga));

        assertFalse(propia.estaDestruida());
        assertFalse(enemiga.estaDestruida());
        assertEquals(propiaAntes, propia.toString());
        assertEquals(enemigaAntes, enemiga.toString());
    }

    @Test
    @DisplayName("Sólo la derrota destruye a la nave propia")
    public void testResultadosDestruyenSoloEnDerrota() {
        assertTrue(ResultadoBatalla.PIERDE.destruyeNavePropia());
        assertFalse(ResultadoBatalla.GANA.destruyeNavePropia());
        assertFalse(ResultadoBatalla.PACTO_DE_PAZ.destruyeNavePropia());
    }

    // --- Nave como ElementoDelUniverso ---

    @ParameterizedTest
    @EnumSource(TipoNave.class)
    @DisplayName("Una nave de la fábrica es un elemento del universo con nombre y símbolo de su tipo")
    public void testNaveComoElementoDelUniverso(TipoNave tipo) {
        Nave nave = fabrica.crearNave(tipo);
        ElementoDelUniverso elemento = nave;

        assertEquals(tipo.getDescripcion() + " #" + nave.getId(), elemento.getNombre());
        assertEquals(tipo.getSimbolo(), elemento.getSimbolo());
        assertEquals(elemento.getSimbolo(), fabrica.crearNave(tipo).getSimbolo(), "Mismo símbolo para la misma clase");
    }

    @Test
    @DisplayName("Cada tipo de nave tiene un símbolo propio, distinto del de asteroides y estaciones")
    public void testSimbolosDistintos() {
        assertNotEquals(TipoNave.EXPLORADORA.getSimbolo(), TipoNave.CARGUERO.getSimbolo());
        assertNotEquals(TipoNave.CARGUERO.getSimbolo(), TipoNave.COMBATE.getSimbolo());
        assertNotEquals(TipoNave.COMBATE.getSimbolo(), TipoNave.EXPLORADORA.getSimbolo());
        for (TipoNave tipo : TipoNave.values()) {
            assertNotEquals(modelo.universo.Asteroide.SIMBOLO, tipo.getSimbolo());
            assertNotEquals(modelo.universo.EstacionDeCarga.SIMBOLO, tipo.getSimbolo());
        }
    }

    // --- Encuentro en el tablero: interactuarCon a través del asistente ---

    @Test
    @DisplayName("Derrota: la nave propia queda destruida, la tripulación muere y la Bitácora lo registra")
    public void testDerrotaDestruyeLaNave() throws Exception {
        AsistenteComando asistente = asistenteTripulado(TipoNave.EXPLORADORA);
        Nave enemiga = fabrica.crearNave(TipoNave.COMBATE);
        int eventosAntes = asistente.getEventos().size();

        enemiga.interactuarCon(asistente);

        assertTrue(asistente.naveDestruida());
        assertFalse(asistente.getNave().tripulacionConVida());
        assertFalse(asistente.naveListaParaOperar());
        assertFalse(enemiga.estaDestruida(), "El resultado se aplica sólo a la nave propia");

        List<Evento> eventos = asistente.getEventos();
        assertEquals(eventosAntes + 2, eventos.size());
        Evento batalla = eventos.get(eventosAntes);
        Evento destruccion = eventos.get(eventosAntes + 1);
        assertEquals(TipoEvento.RELEVANTE, batalla.getTipo());
        assertTrue(batalla.getDescripcion().contains("Batalla contra " + enemiga.getNombre()));
        assertTrue(batalla.getDescripcion().contains(ResultadoBatalla.PIERDE.getDescripcion()));
        assertTrue(destruccion.getDescripcion().contains("Nave destruida por la derrota contra " + enemiga.getNombre()));
        assertTrue(destruccion.getDescripcion().contains("La tripulación murió"));
    }

    @ParameterizedTest(name = "Carguero propio vs. {0} enemiga")
    @CsvSource({"CARGUERO, PACTO_DE_PAZ", "COMBATE, GANA"})
    @DisplayName("Victoria o pacto de paz: la nave sigue operativa y la Bitácora registra el resultado")
    public void testVictoriaOPazNoDestruye(TipoNave enemiga, ResultadoBatalla esperado) throws Exception {
        AsistenteComando asistente = asistenteTripulado(TipoNave.CARGUERO);
        int eventosAntes = asistente.getEventos().size();

        assertEquals(esperado, asistente.enfrentarseCon(fabrica.crearNave(enemiga)));

        assertFalse(asistente.naveDestruida());
        assertTrue(asistente.getNave().tripulacionConVida());
        assertTrue(asistente.naveListaParaOperar());
        assertEquals(eventosAntes + 1, asistente.getEventos().size());
        assertTrue(asistente.getEventos().get(eventosAntes).getDescripcion().contains(esperado.getDescripcion()));
    }

    @Test
    @DisplayName("Una nave destruida sigue destruida aunque luego se le aplique una victoria")
    public void testDestruccionDefinitiva() {
        Nave nave = fabrica.crearNave(TipoNave.COMBATE);

        nave.aplicarResultado(ResultadoBatalla.PIERDE);
        nave.aplicarResultado(ResultadoBatalla.GANA);

        assertTrue(nave.estaDestruida());
    }

    // --- Contratos incumplidos (aserciones) ---

    @Test
    @DisplayName("Contrato: una nave destruida no puede librar otra batalla")
    public void testBatallaConNaveDestruida() throws Exception {
        AsistenteComando asistente = asistenteTripulado(TipoNave.EXPLORADORA);
        asistente.enfrentarseCon(fabrica.crearNave(TipoNave.COMBATE));
        Nave otra = fabrica.crearNave(TipoNave.CARGUERO);

        assertThrows(AssertionError.class, () -> otra.interactuarCon(asistente));
    }

    @Test
    @DisplayName("Contrato: la nave no puede enfrentarse consigo misma, ni con una enemiga nula")
    public void testBatallaConsigoMismaONula() throws Exception {
        AsistenteComando asistente = asistenteTripulado(TipoNave.EXPLORADORA);

        assertThrows(AssertionError.class, () -> asistente.getNave().interactuarCon(asistente));
        assertThrows(AssertionError.class, () -> asistente.enfrentarseCon(null));
        assertThrows(AssertionError.class, () -> fabrica.crearNave(TipoNave.COMBATE).interactuarCon(null));
    }

    private AsistenteComando asistenteTripulado(TipoNave tipo) throws Exception {
        AsistenteComando asistente = new AsistenteComando(fabrica.crearNave(tipo));
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Kirk", 3, Origen.TERRICOLA));
        integrantes.add(new Consejero("Troi", 2, Origen.VULCANO));
        integrantes.add(new Teniente("Uhura", 5, Origen.MARCIANO));
        integrantes.add(new Alferez("Chekov", 1, Origen.TERRICOLA));
        integrantes.add(new Alferez("Sulu", 0, Origen.MARCIANO));
        asistente.asignarTripulacion(new Tripulacion(integrantes));
        return asistente;
    }
}
