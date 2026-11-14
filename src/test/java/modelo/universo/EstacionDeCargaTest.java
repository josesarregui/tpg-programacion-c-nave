package modelo.universo;

import modelo.asistente.Asistente;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del Enunciado E2, punto C: la estación recarga combustible y energía sin superar la capacidad
 * máxima de 100 y el acople queda registrado en la Bitácora.
 */
public class EstacionDeCargaTest {

    private final ElementoDelUniverso estacion = new EstacionDeCarga();

    private Asistente asistenteDe(TipoNave tipo) {
        return new AsistenteComando(new NaveFactory().crearNave(tipo));
    }

    @Test
    @DisplayName("E2-C: la estación se identifica con su símbolo y nombre")
    public void testIdentificacion() {
        assertEquals(EstacionDeCarga.SIMBOLO, estacion.getSimbolo());
        assertEquals("Estación de carga", estacion.getNombre());
        assertEquals("Orbital 7", new EstacionDeCarga("Orbital 7").getNombre());
    }

    @Test
    @DisplayName("E2-C: con espacio libre recarga el monto fijo de combustible y energía")
    public void testRecargaMontoFijo() throws Exception {
        // Exploradora: combustible 60, energía 80 -> se baja la energía a 60.
        Asistente asistente = asistenteDe(TipoNave.EXPLORADORA);
        asistente.consumirRecursos(0, 20, 0);

        estacion.interactuarCon(asistente);

        assertEquals(60 + EstacionDeCarga.RECARGA_COMBUSTIBLE, asistente.getCombustible());
        assertEquals(60 + EstacionDeCarga.RECARGA_ENERGIA, asistente.getEnergia());
        assertEquals(0, asistente.getDesgaste());
    }

    @Test
    @DisplayName("E2-C: la recarga respeta el tope máximo de 100 unidades")
    public void testRecargaRespetaTope() {
        // Combate: combustible 80, energía 100.
        Asistente asistente = asistenteDe(TipoNave.COMBATE);

        estacion.interactuarCon(asistente);

        assertEquals(100, asistente.getCombustible());
        assertEquals(100, asistente.getEnergia());
    }

    @Test
    @DisplayName("E2-C: acoples sucesivos nunca superan el tope")
    public void testAcoplesSucesivos() {
        Asistente asistente = asistenteDe(TipoNave.EXPLORADORA);

        estacion.interactuarCon(asistente);
        estacion.interactuarCon(asistente);

        assertEquals(100, asistente.getCombustible());
        assertEquals(100, asistente.getEnergia());
    }

    @Test
    @DisplayName("E1-05: el acople queda registrado en la Bitácora con lo realmente cargado")
    public void testAcopleRegistradoEnBitacora() {
        Asistente asistente = asistenteDe(TipoNave.COMBATE);
        int eventosAntes = asistente.getEventos().size();

        estacion.interactuarCon(asistente);

        List<Evento> eventos = asistente.getEventos();
        assertEquals(eventosAntes + 2, eventos.size());
        Evento encuentro = eventos.get(eventosAntes);
        Evento recurso = eventos.get(eventosAntes + 1);
        assertEquals(TipoEvento.RELEVANTE, encuentro.getTipo());
        assertTrue(encuentro.getDescripcion().contains("Estación de carga"));
        assertEquals(TipoEvento.RECURSO, recurso.getTipo());
        assertTrue(recurso.getDescripcion().contains("+20 de combustible y +0 de energía"));
    }

    @Test
    @DisplayName("E2: la vista obtiene el símbolo de cualquier elemento por polimorfismo")
    public void testSimbolosPolimorficos() {
        List<ElementoDelUniverso> elementos = new ArrayList<>();
        elementos.add(new Asteroide());
        elementos.add(new EstacionDeCarga());

        StringBuilder fila = new StringBuilder();
        for (ElementoDelUniverso elemento : elementos) {
            fila.append(elemento.getSimbolo());
        }

        assertEquals(Asteroide.SIMBOLO + EstacionDeCarga.SIMBOLO, fila.toString());
    }
}
