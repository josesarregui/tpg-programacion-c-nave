package modelo.nave;

import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import modelo.universo.Asteroide;
import modelo.warp.MotorWarp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del estado de destrucción (Enunciado E2, Fase 2): la nave se destruye, de forma definitiva, si el
 * desgaste alcanza 100, la energía queda en 0 o el combustible queda en 0, ya sea por un consumo, un impacto,
 * el Motor Warp o una entrega de suministros. Con la nave destruida la tripulación muere, la nave deja de estar
 * lista para operar y la Bitácora registra la causa.
 */
public class DestruccionNaveTest {

    // Exploradora: combustible 60, energía 80, desgaste 0 (Ficha de Inicio, punto 2).
    private AsistenteComando asistente;

    @BeforeEach
    public void inicializar() throws Exception {
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Kirk", 3, Origen.TERRICOLA));
        integrantes.add(new Consejero("Troi", 2, Origen.VULCANO));
        integrantes.add(new Teniente("Uhura", 5, Origen.MARCIANO));
        integrantes.add(new Alferez("Chekov", 1, Origen.TERRICOLA));
        integrantes.add(new Alferez("Sulu", 0, Origen.MARCIANO));
        asistente.asignarTripulacion(new Tripulacion(integrantes));
    }

    @Test
    @DisplayName("Una nave recién creada no está destruida y su tripulación está con vida")
    public void testNaveNueva() {
        assertFalse(asistente.naveDestruida());
        assertTrue(asistente.getNave().tripulacionConVida());
        assertTrue(asistente.naveListaParaOperar());
    }

    @Test
    @DisplayName("Sin tripulación asignada no hay tripulación con vida, aunque la nave no esté destruida")
    public void testSinTripulacion() {
        Nave nave = new NaveFactory().crearNave(TipoNave.CARGUERO);
        assertFalse(nave.estaDestruida());
        assertFalse(nave.tripulacionConVida());
    }

    @Test
    @DisplayName("Combustible en 0 por un consumo: la nave se destruye")
    public void testCombustibleCeroPorConsumo() throws Exception {
        asistente.consumirRecursos(60, 0, 0);

        assertEquals(0, asistente.getCombustible());
        assertDestruidaPor("el consumo de recursos");
    }

    @Test
    @DisplayName("Energía en 0 por un consumo: la nave se destruye")
    public void testEnergiaCeroPorConsumo() throws Exception {
        asistente.consumirRecursos(0, 80, 0);

        assertEquals(0, asistente.getEnergia());
        assertDestruidaPor("el consumo de recursos");
    }

    @Test
    @DisplayName("Desgaste en 100 por un consumo: la nave se destruye")
    public void testDesgasteMaximoPorConsumo() throws Exception {
        asistente.consumirRecursos(0, 0, 100);

        assertEquals(100, asistente.getDesgaste());
        assertDestruidaPor("el consumo de recursos");
    }

    @Test
    @DisplayName("Un consumo que deja recursos por encima de 0 y desgaste por debajo de 100 no destruye la nave")
    public void testConsumoQueNoDestruye() throws Exception {
        asistente.consumirRecursos(59, 79, 99);

        assertFalse(asistente.naveDestruida());
        assertTrue(asistente.getNave().tripulacionConVida());
    }

    @Test
    @DisplayName("Energía en 0 por el impacto de un asteroide: la nave se destruye")
    public void testEnergiaCeroPorImpacto() throws Exception {
        asistente.consumirRecursos(0, 80 - Asteroide.DANIO_ENERGIA, 0);

        new Asteroide().interactuarCon(asistente);

        assertEquals(0, asistente.getEnergia());
        assertDestruidaPor("el impacto");
    }

    @Test
    @DisplayName("Combustible en 0 por un salto normal del Motor Warp: la nave se destruye")
    public void testCombustibleCeroPorSaltoNormal() throws Exception {
        asistente.consumirRecursos(60 - MotorWarp.COMBUSTIBLE_SALTO_NORMAL, 0, 0);

        asistente.saltar();

        assertEquals(0, asistente.getCombustible());
        assertDestruidaPor("el salto normal");
    }

    @Test
    @DisplayName("Energía en 0 por el salto warp: la nave se destruye")
    public void testEnergiaCeroPorSaltoWarp() throws Exception {
        asistente.consumirRecursos(0, 80 - MotorWarp.ENERGIA_SALTO_WARP, 0);

        // Disponible -> Preparando (turno 1) -> En warp (turno 2) -> salto: consume la energía restante.
        asistente.saltarWarp();
        asistente.saltarWarp();
        assertFalse(asistente.naveDestruida());
        assertEquals(MotorWarp.CASILLEROS_SALTO_WARP, asistente.saltarWarp());

        assertEquals(0, asistente.getEnergia());
        assertDestruidaPor("la maniobra warp");
    }

    @Test
    @DisplayName("Desgaste en 100 por el enfriamiento del Motor Warp: la nave se destruye")
    public void testDesgasteMaximoPorEnfriamiento() throws Exception {
        asistente.consumirRecursos(0, 0, 100 - MotorWarp.DESGASTE_ENFRIAMIENTO);

        asistente.saltarWarp();
        asistente.saltarWarp();
        asistente.saltarWarp();
        assertFalse(asistente.naveDestruida());
        asistente.saltarWarp(); // Enfriamiento: suma el desgaste y vuelve a Disponible.

        assertEquals(100, asistente.getDesgaste());
        assertDestruidaPor("la maniobra warp");
    }

    @Test
    @DisplayName("Energía en 0 por una entrega de suministros: la nave se destruye")
    public void testEnergiaCeroPorEntregaDeSuministros() throws Exception {
        asistente.entregarSuministros(0, 80);

        assertEquals(0, asistente.getEnergia());
        assertDestruidaPor("la entrega de suministros");
    }

    @Test
    @DisplayName("La destrucción es definitiva: recargar o hacer mantenimiento no repara la nave")
    public void testDestruccionDefinitiva() throws Exception {
        asistente.consumirRecursos(0, 0, 100);

        asistente.realizarMantenimiento();
        asistente.reabastecer(30, 30);
        asistente.cargarCombustible(1);

        assertTrue(asistente.naveDestruida());
        assertFalse(asistente.naveListaParaOperar());
        assertFalse(asistente.getNave().tripulacionConVida());
        assertTrue(asistente.getNave().toString().contains("destruida"));
    }

    @Test
    @DisplayName("La destrucción se registra una sola vez, en la orden que la provocó")
    public void testDestruccionSeRegistraUnaVez() throws Exception {
        asistente.consumirRecursos(0, 80, 0);
        asistente.recibirImpacto(Asteroide.DANIO_ENERGIA);

        long registros = asistente.getEventos().stream()
                .filter(evento -> evento.getDescripcion().startsWith("Nave destruida")).count();
        assertEquals(1, registros);
    }

    private void assertDestruidaPor(String causa) {
        assertTrue(asistente.naveDestruida());
        assertFalse(asistente.getNave().tripulacionConVida(), "Al destruirse la nave, la tripulación muere");
        assertFalse(asistente.naveListaParaOperar());
        List<Evento> eventos = asistente.getEventos();
        String ultimo = eventos.get(eventos.size() - 1).getDescripcion();
        assertTrue(ultimo.contains("Nave destruida por " + causa), ultimo);
        assertTrue(ultimo.contains("La tripulación murió"), ultimo);
    }
}
