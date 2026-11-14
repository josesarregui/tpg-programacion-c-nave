package modelo.universo;

import java.util.ArrayList;
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

}
