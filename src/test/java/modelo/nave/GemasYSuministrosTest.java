package modelo.nave;

import excepcion.CantidadInvalidaException;
import excepcion.RecursoInsuficienteException;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de las operaciones mínimas de la nave para M-01 (entrega de suministros a una nave aliada) y M-02
 * (recolección de gemas), Enunciado E2, con sus contratos y su registro en la Bitácora. La lógica de las
 * misiones sobre el tablero se incorpora en el Paso 3.
 */
public class GemasYSuministrosTest {

    // Carguero: combustible 100, energía 60, desgaste 0 (Ficha de Inicio, punto 2).
    private AsistenteComando asistente;
    private Nave nave;

    @BeforeEach
    public void inicializar() {
        nave = new NaveFactory().crearNave(TipoNave.CARGUERO);
        asistente = new AsistenteComando(nave);
    }

    // --- Gemas (M-02) ---

    @Test
    @DisplayName("M-02: la nave empieza sin gemas y acumula las que se le acreditan")
    public void testAcreditarGemas() {
        assertEquals(0, asistente.getGemas());

        asistente.acreditarGemas(3);
        asistente.acreditarGemas(2);

        assertEquals(5, asistente.getGemas());
        assertEquals(5, nave.getGemas());
        Evento ultimo = asistente.getEventos().get(asistente.getEventos().size() - 1);
        assertEquals(TipoEvento.RECURSO, ultimo.getTipo());
        assertTrue(ultimo.getDescripcion().contains("total 5"));
    }

    @Test
    @DisplayName("Contrato: acreditar una cantidad de gemas no positiva es un error de programación")
    public void testAcreditarGemasNoPositivas() {
        assertThrows(AssertionError.class, () -> nave.acreditarGemas(0));
        assertThrows(AssertionError.class, () -> nave.acreditarGemas(-1));
        assertEquals(0, nave.getGemas());
    }

    @Test
    @DisplayName("Contrato: una nave destruida no puede recibir gemas")
    public void testAcreditarGemasANaveDestruida() {
        nave.aplicarResultado(ResultadoBatalla.PIERDE);

        assertThrows(AssertionError.class, () -> asistente.acreditarGemas(1));
        assertThrows(AssertionError.class, () -> nave.acreditarGemas(1));
    }

    // --- Suministros (M-01) ---

    @Test
    @DisplayName("M-01: entregar suministros descuenta combustible y energía y acumula lo entregado")
    public void testEntregarSuministros() throws Exception {
        asistente.entregarSuministros(20, 10);
        asistente.entregarSuministros(5, 0);

        assertEquals(75, asistente.getCombustible());
        assertEquals(50, asistente.getEnergia());
        assertEquals(0, asistente.getDesgaste(), "La entrega no suma desgaste");
        assertEquals(25, nave.getCombustibleEntregado());
        assertEquals(10, nave.getEnergiaEntregada());
        assertFalse(asistente.naveDestruida());
        Evento ultimo = asistente.getEventos().get(asistente.getEventos().size() - 1);
        assertEquals(TipoEvento.RECURSO, ultimo.getTipo());
        assertTrue(ultimo.getDescripcion().startsWith("Entrega de suministros"));
    }

    @Test
    @DisplayName("Escenario B: una entrega que no alcanza se rechaza sin cambios parciales y queda registrada")
    public void testEntregaInsuficiente() {
        RecursoInsuficienteException e = assertThrows(RecursoInsuficienteException.class,
                () -> asistente.entregarSuministros(10, 61));

        assertEquals(TipoRecurso.ENERGIA, e.getRecurso());
        assertEquals(100, asistente.getCombustible(), "El combustible no se descuenta si la energía no alcanza");
        assertEquals(60, asistente.getEnergia());
        assertEquals(0, nave.getCombustibleEntregado());
        assertEquals(0, nave.getEnergiaEntregada());
        assertUltimoError("Entrega de suministros rechazada");
    }

    @Test
    @DisplayName("Una entrega con cantidades negativas se rechaza sin cambios y queda registrada")
    public void testEntregaNegativa() {
        assertThrows(CantidadInvalidaException.class, () -> asistente.entregarSuministros(-1, 0));

        assertEquals(100, asistente.getCombustible());
        assertEquals(0, nave.getCombustibleEntregado());
        assertUltimoError("Entrega de suministros rechazada");
    }

    @Test
    @DisplayName("Contrato: una nave destruida no puede entregar suministros")
    public void testEntregaDeNaveDestruida() {
        nave.aplicarResultado(ResultadoBatalla.PIERDE);

        assertThrows(AssertionError.class, () -> asistente.entregarSuministros(1, 1));
        assertThrows(AssertionError.class, () -> nave.entregarSuministros(1, 1));
    }

    private void assertUltimoError(String texto) {
        List<Evento> eventos = asistente.getEventos();
        Evento ultimo = eventos.get(eventos.size() - 1);
        assertEquals(TipoEvento.ERROR, ultimo.getTipo());
        assertTrue(ultimo.getDescripcion().contains(texto));
    }
}
