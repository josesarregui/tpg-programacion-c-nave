package modelo.asistente;

import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.nave.TipoRecurso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de E1-03 y E1-05: el asistente delega las órdenes en la nave y en el Motor Warp,
 * registra en su Bitácora las operaciones y los rechazos (Escenarios C y D) y propaga el error al invocante.
 */
public class AsistenteComandoTest {

    private AsistenteComando asistente;

    @BeforeEach
    public void inicializar() {
        // Carguero: combustible 100, energía 60, desgaste 0 (Ficha de Inicio, punto 2).
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.CARGUERO));
    }

    @Test
    @DisplayName("El asistente registra su puesta en servicio en la Bitácora")
    public void testCreacion() {
        assertEquals(1, asistente.getEventos().size());
        assertEquals(TipoEvento.SISTEMA, asistente.getEventos().get(0).getTipo());
        assertNull(asistente.getMisionEncomendada());
    }

    @Test
    @DisplayName("Escenario C: la transición inválida se rechaza, no cambia el motor y queda registrada")
    public void testTransicionInvalidaRegistrada() {
        EstadoMotorInvalidoException e = assertThrows(EstadoMotorInvalidoException.class, asistente::saltar);

        assertEquals("Disponible", e.getEstadoActual());
        assertEquals("Disponible", asistente.getEstadoMotor());
        assertUltimoEvento(TipoEvento.ERROR, "Disponible");
    }

    @Test
    @DisplayName("Escenario C: preparar el salto y saltar registra cada cambio del motor en orden")
    public void testSaltoRegistrado() {
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

    @Test
    @DisplayName("Rechazo por contrato: un asistente debe operar una nave")
    public void testAsistenteSinNave() {
        assertThrows(AssertionError.class, () -> new AsistenteComando(null));
    }

    private void assertUltimoEvento(TipoEvento tipo, String texto) {
        List<Evento> eventos = asistente.getEventos();
        Evento ultimo = eventos.get(eventos.size() - 1);
        assertEquals(tipo, ultimo.getTipo());
        assertTrue(ultimo.getDescripcion().contains(texto));
    }
}
