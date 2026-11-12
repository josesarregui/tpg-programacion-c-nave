package modelo.destinoytablero;

import java.util.ArrayList;
import modelo.nave.Carguero;
import modelo.nave.Combate;
import modelo.nave.Exploradora;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.*;
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
    @DisplayName("Double Dispatch en combates espaciales: matriz de enfrentamientos completa")
    public void testDoubleDispatchCombates() {
        Exploradora propiaExp = (Exploradora) naveFactory.crearNave(TipoNave.EXPLORADORA);
        Carguero propiaCarg = (Carguero) naveFactory.crearNave(TipoNave.CARGUERO);
        Combate propiaComb = (Combate) naveFactory.crearNave(TipoNave.COMBATE);

        ExploradoraEnemiga enemigaExp = new ExploradoraEnemiga();
        CargueroEnemigo enemigaCarg = new CargueroEnemigo();
        CombateEnemigo enemigaComb = new CombateEnemigo();

        // 1. Nave Propia: Exploradora
        assertEquals(ResultadoCombate.PACTO_DE_PAZ, propiaExp.enfrentar(enemigaExp));
        assertEquals(ResultadoCombate.GANA, propiaExp.enfrentar(enemigaCarg));
        assertEquals(ResultadoCombate.PIERDE, propiaExp.enfrentar(enemigaComb));

        // 2. Nave Propia: Carguero
        assertEquals(ResultadoCombate.PIERDE, propiaCarg.enfrentar(enemigaExp));
        assertEquals(ResultadoCombate.PACTO_DE_PAZ, propiaCarg.enfrentar(enemigaCarg));
        assertEquals(ResultadoCombate.GANA, propiaCarg.enfrentar(enemigaComb));

        // 3. Nave Propia: Combate
        assertEquals(ResultadoCombate.GANA, propiaComb.enfrentar(enemigaExp));
        assertEquals(ResultadoCombate.PIERDE, propiaComb.enfrentar(enemigaCarg));
        assertEquals(ResultadoCombate.PACTO_DE_PAZ, propiaComb.enfrentar(enemigaComb));
    }

    @Test
    @DisplayName("Interacción con Asteroide descuenta energía y suma desgaste")
    public void testInteraccionAsteroide() {
        Combate nave = (Combate) naveFactory.crearNave(TipoNave.COMBATE);
        int energiaInicial = nave.getEnergia();
        int desgasteInicial = nave.getDesgaste();

        Asteroide asteroide = new Asteroide();
        ResultadoInteraccion resultado = asteroide.interactuar(nave);

        assertNotNull(resultado);
        assertTrue(resultado.getEnergiaModificada() < 0);
        assertTrue(resultado.getDesgasteModificado() > 0);
        assertEquals(energiaInicial - Asteroide.ENERGIA_DESCONTADA, nave.getEnergia());
        assertEquals(desgasteInicial + Asteroide.DESGASTE_SUMADO, nave.getDesgaste());
    }

    @Test
    @DisplayName("Interacción con Estación de Carga recarga combustible y energía")
    public void testInteraccionEstacionDeCarga() throws Exception {
        Exploradora nave = (Exploradora) naveFactory.crearNave(TipoNave.EXPLORADORA);
        nave.consumirRecursos(20, 20, 0);

        int combustibleAntes = nave.getCombustible();
        int energiaAntes = nave.getEnergia();

        EstacionDeCarga estacion = new EstacionDeCarga();
        ResultadoInteraccion resultado = estacion.interactuar(nave);

        assertNotNull(resultado);
        assertTrue(nave.getCombustible() > combustibleAntes);
        assertTrue(nave.getEnergia() > energiaAntes);
        assertFalse(resultado.isNaveDestruida());
    }
}
