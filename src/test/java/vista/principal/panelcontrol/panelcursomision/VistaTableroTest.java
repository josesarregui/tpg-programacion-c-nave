package vista.principal.panelcontrol.panelcursomision;

import java.awt.GraphicsEnvironment;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.Asteroide;
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
    @DisplayName("Dejar de observar: el tablero ya no la notifica")
    public void dejarDeObservar() throws Exception {
        VistaTablero vista = observando();

        vista.dejarDeObservar();
        tablero.moverNave(new Posicion(9, 9));
        esperarEdt();

        assertEquals(new Posicion(10, 10), vista.getPosicionNaveMostrada());
    }
}
