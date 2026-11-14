package modelo.universo;

import modelo.asistente.Asistente;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del Enunciado E2, punto B: el asteroide descuenta un monto fijo de energía, la energía no baja
 * de 0 y el impacto queda registrado en la Bitácora.
 */
public class AsteroideTest {

    private Asistente asistente;
    private ElementoDelUniverso asteroide;

    @BeforeEach
    public void inicializar() {
        // Combate: combustible 80, energía 100, desgaste 0.
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.COMBATE));
        asteroide = new Asteroide();
    }

    @Test
    @DisplayName("E2-B: el asteroide se identifica con su símbolo y nombre")
    public void testIdentificacion() {
        assertEquals(Asteroide.SIMBOLO, asteroide.getSimbolo());
        assertEquals("Asteroide", asteroide.getNombre());
        assertEquals("Ceres", new Asteroide("Ceres").getNombre());
    }

    @Test
    @DisplayName("E2-B: el impacto descuenta DANIO_ENERGIA de energía sin tocar combustible ni desgaste")
    public void testImpactoDescuentaEnergia() {
        asteroide.interactuarCon(asistente);

        assertEquals(100 - Asteroide.DANIO_ENERGIA, asistente.getEnergia());
        assertEquals(80, asistente.getCombustible());
        assertEquals(0, asistente.getDesgaste());
    }

    @Test
    @DisplayName("E2-B: impactos sucesivos acumulan la pérdida de energía")
    public void testImpactosSucesivos() {
        asteroide.interactuarCon(asistente);
        asteroide.interactuarCon(asistente);

        assertEquals(100 - 2 * Asteroide.DANIO_ENERGIA, asistente.getEnergia());
    }

    @Test
    @DisplayName("E2-B: si la energía no alcanza, queda en 0 y no se viola el invariante")
    public void testEnergiaNoBajaDeCero() throws Exception {
        asistente.consumirRecursos(0, 90, 0);

        asteroide.interactuarCon(asistente);

        assertEquals(0, asistente.getEnergia());
    }

    @Test
    @DisplayName("E2-B: con energía exactamente igual al daño, queda en 0")
    public void testEnergiaIgualAlDanio() throws Exception {
        asistente.consumirRecursos(0, 100 - Asteroide.DANIO_ENERGIA, 0);

        asteroide.interactuarCon(asistente);

        assertEquals(0, asistente.getEnergia());
    }

    @Test
    @DisplayName("E1-05: el impacto queda registrado en la Bitácora (encuentro y recursos)")
    public void testImpactoRegistradoEnBitacora() {
        int eventosAntes = asistente.getEventos().size();

        asteroide.interactuarCon(asistente);

        List<Evento> eventos = asistente.getEventos();
        assertEquals(eventosAntes + 2, eventos.size());
        Evento encuentro = eventos.get(eventosAntes);
        Evento recurso = eventos.get(eventosAntes + 1);
        assertEquals(TipoEvento.RELEVANTE, encuentro.getTipo());
        assertTrue(encuentro.getDescripcion().contains("Asteroide"));
        assertEquals(TipoEvento.RECURSO, recurso.getTipo());
        assertTrue(recurso.getDescripcion().contains(String.valueOf(Asteroide.DANIO_ENERGIA)));
    }

    @Test
    @DisplayName("E1-05: si la energía no alcanza, la Bitácora registra la pérdida real y la destrucción")
    public void testBitacoraRegistraPerdidaReal() throws Exception {
        asistente.consumirRecursos(0, 90, 0);

        asteroide.interactuarCon(asistente);

        List<Evento> eventos = asistente.getEventos();
        assertTrue(eventos.get(eventos.size() - 2).getDescripcion().contains("pérdida de 10 de energía"));
        assertTrue(eventos.get(eventos.size() - 1).getDescripcion().contains("Nave destruida por el impacto"));
        assertTrue(asistente.naveDestruida());
    }
}
