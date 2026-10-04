package modelo.nave;

import modelo.warp.DisponibleState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de E1-07 y de la Ficha de Inicio (punto 2): la fábrica crea los tres tipos de nave
 * con su configuración inicial, en un estado válido y detrás de la abstracción común Nave.
 */
public class NaveFactoryTest {

    private NaveFactory fabrica;

    @BeforeEach
    public void inicializar() {
        fabrica = new NaveFactory();
    }

    @Test
    @DisplayName("Ficha de Inicio: la Exploradora empieza con combustible 60, energía 80 y desgaste 0")
    public void testCrearExploradora() {
        Nave nave = fabrica.crearNave(TipoNave.EXPLORADORA);

        assertEquals(TipoNave.EXPLORADORA, nave.getTipo());
        assertEquals(60, nave.getCombustible());
        assertEquals(80, nave.getEnergia());
        assertEquals(0, nave.getDesgaste());
    }

    @Test
    @DisplayName("Ficha de Inicio: el Carguero empieza con combustible 100, energía 60 y desgaste 0")
    public void testCrearCarguero() {
        Nave nave = fabrica.crearNave(TipoNave.CARGUERO);

        assertEquals(TipoNave.CARGUERO, nave.getTipo());
        assertEquals(100, nave.getCombustible());
        assertEquals(60, nave.getEnergia());
        assertEquals(0, nave.getDesgaste());
    }

    @Test
    @DisplayName("Ficha de Inicio: la nave de Combate empieza con combustible 80, energía 100 y desgaste 0")
    public void testCrearCombate() {
        Nave nave = fabrica.crearNave(TipoNave.COMBATE);

        assertEquals(TipoNave.COMBATE, nave.getTipo());
        assertEquals(80, nave.getCombustible());
        assertEquals(100, nave.getEnergia());
        assertEquals(0, nave.getDesgaste());
    }

    @Test
    @DisplayName("Toda nave creada empieza en estado válido: motor Disponible, sin tripulación ni mantenimiento pendiente")
    public void testEstadoInicialValido() {
        for (TipoNave tipo : TipoNave.values()) {
            Nave nave = fabrica.crearNave(tipo);

            assertInstanceOf(DisponibleState.class, nave.getMotor().getEstado());
            assertNull(nave.getTripulacion());
            assertFalse(nave.requiereMantenimiento());
        }
    }

    @Test
    @DisplayName("Cada nave creada tiene una identidad única")
    public void testIdentidadUnica() {
        Nave primera = fabrica.crearNave(TipoNave.CARGUERO);
        Nave segunda = fabrica.crearNave(TipoNave.CARGUERO);

        assertTrue(primera.getId() > 0);
        assertNotEquals(primera.getId(), segunda.getId());
    }

    @Test
    @DisplayName("Rechazo: pedir una nave sin tipo es un error de programación (aserción)")
    public void testTipoNulo() {
        assertThrows(AssertionError.class, () -> fabrica.crearNave(null));
    }
}
