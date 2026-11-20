package vista.principal.panelcontrol.panelcursomision;

import java.awt.GraphicsEnvironment;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.SwingUtilities;
import modelo.asistente.AsistenteComando;
import modelo.mision.Mision;
import modelo.mision.MisionRecoleccion;
import modelo.mision.MisionRetorno;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.universo.Asteroide;
import modelo.universo.Destino;
import modelo.universo.DestinoSinMovimiento;
import modelo.universo.ElementoDelUniverso;
import modelo.universo.EstacionDeCarga;
import modelo.universo.Observador;
import modelo.universo.Posicion;
import modelo.universo.TableroDePrueba;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static vista.EdtDePrueba.conEdtBloqueado;
import static vista.EdtDePrueba.esperarEdt;

/**
 * Vista del tablero de 20 x 20: muestra los símbolos que da cada elemento por polimorfismo, la nave y el objetivo, y
 * se repinta en el EDT cuando el tablero la notifica desde otro hilo.
 */
public class VistaTableroTest {

    private TableroDePrueba tablero;

    @BeforeEach
    public void inicializar() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
        Map<Posicion, ElementoDelUniverso> ubicaciones = new LinkedHashMap<>();
        ubicaciones.put(new Posicion(2, 3), new Asteroide("Asteroide 1"));
        ubicaciones.put(new Posicion(4, 5), new EstacionDeCarga("Estación 1"));
        ubicaciones.put(new Posicion(6, 7), new NaveFactory().crearNave(TipoNave.COMBATE));
        tablero = new TableroDePrueba(20, 20, ubicaciones);
    }

    private VistaTablero observando() throws Exception {
        VistaTablero vista = new VistaTablero();
        SwingUtilities.invokeAndWait(() -> vista.observar(tablero));
        esperarEdt();
        return vista;
    }

    @Test
    @DisplayName("Observar: se suscribe sola y muestra la grilla de 20 x 20 con los símbolos y la nave en el centro")
    public void observarMuestraElEstadoInicial() throws Exception {
        VistaTablero vista = observando();
        Observador comoObservador = vista;
        IVistaTablero comoVista = vista;
        assertSame(comoVista, comoObservador);

        assertEquals(20, vista.getFilas());
        assertEquals(Asteroide.SIMBOLO, vista.getSimboloEn(2, 3));
        assertEquals(EstacionDeCarga.SIMBOLO, vista.getSimboloEn(4, 5));
        assertEquals(TipoNave.COMBATE.getSimbolo(), vista.getSimboloEn(6, 7));
        assertEquals("", vista.getSimboloEn(0, 0));
        assertEquals(new Posicion(10, 10), vista.getPosicionNaveMostrada());
        assertNull(vista.getObjetivoMostrado());
    }

    @Test
    @DisplayName("Observer: consulta el tablero en el hilo que notifica y repinta en el EDT, sin bloquear la misión")
    public void actualizaEnElEdt() throws Exception {
        VistaTablero vista = observando();

        conEdtBloqueado(
                () -> tablero.moverNave(new Posicion(10, 11)),
                () -> assertEquals(new Posicion(10, 10), vista.getPosicionNaveMostrada(),
                        "Con el EDT bloqueado la grilla todavía no cambió."));

        assertEquals(new Posicion(10, 11), vista.getPosicionNaveMostrada());
        assertTrue(vista.getMensajeMostrado().contains("(10, 11)"), "Muestra el mensaje recibido (push).");
    }

    @Test
    @DisplayName("M-03: marca como objetivo la base de la misión que sigue, no el que el tablero conserva de la M-02")
    public void muestraElObjetivoDeLaMisionSeguida() throws Exception {
        TableroDePrueba vacio = new TableroDePrueba(20, 20);
        vacio.fijarObjetivo(new Posicion(1, 7));
        Destino destino = new DestinoSinMovimiento(vacio);
        VistaTablero vista = new VistaTablero();
        SwingUtilities.invokeAndWait(() -> vista.observar(vacio));
        AsistenteComando asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
        asistente.asignarTripulacion(new Tripulacion(List.of(
                new Capitan("James Kirk", 15, Origen.TERRICOLA),
                new Teniente("Spock", 12, Origen.VULCANO),
                new Alferez("Hikaru Sulu", 5, Origen.TERRICOLA),
                new Alferez("Pavel Chekov", 2, Origen.MARCIANO),
                new Alferez("Nyota Uhura", 3, Origen.TERRICOLA))));

        realizar(asistente, vista, new MisionRecoleccion(vacio, destino, new Random(1)));
        esperarEdt();
        assertEquals(new Posicion(1, 7), vista.getObjetivoMostrado(), "M-02: el objetivo que eligió el tablero.");

        realizar(asistente, vista, new MisionRetorno(vacio, destino));
        esperarEdt();

        assertEquals(new Posicion(1, 7), vacio.getObjetivo(), "El tablero sigue guardando el objetivo de la M-02.");
        assertEquals(new Posicion(10, 10), vista.getPosicionNaveMostrada());
        assertEquals(new Posicion(10, 10), vista.getObjetivoMostrado(), "M-03: la base de origen.");
    }

    /**
     * Encomienda la misión, la vista la sigue antes de ejecutarla (como al lanzarla el controlador) y la realiza en
     * este hilo, que hace de hilo de la misión.
     */
    private static void realizar(AsistenteComando asistente, VistaTablero vista, Mision mision) throws Exception {
        mision.configurarPausaPorTurno(0);
        asistente.encomendarMision(mision);
        vista.seguirMision(mision);
        assertTrue(asistente.ejecutarMision().isExitosa());
    }

    @Test
    @DisplayName("Dejar de observar: el tablero ya no la notifica")
    public void dejarDeObservar() throws Exception {
        VistaTablero vista = observando();

        vista.dejarDeObservar();
        tablero.moverNave(new Posicion(9, 9));
        esperarEdt();

        assertEquals(new Posicion(10, 10), vista.getPosicionNaveMostrada());
    }
}
