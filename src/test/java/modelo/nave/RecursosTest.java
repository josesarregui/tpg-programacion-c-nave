package modelo.nave;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.RecursoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de E1-09 y de los Escenarios B y D: cargas, consumos y mantenimiento mantienen
 * los recursos en rango, y toda operación rechazada conserva el estado anterior.
 */
public class RecursosTest {

    private Recursos recursos;

    @BeforeEach
    public void inicializar() {
        // Misma configuración que la Exploradora.
        recursos = new Recursos(60, 80, 0);
    }

    private void verificarSinCambios(int combustible, int energia, int desgaste) {
        assertEquals(combustible, recursos.getCombustible(), "El combustible no debía cambiar.");
        assertEquals(energia, recursos.getEnergia(), "La energía no debía cambiar.");
        assertEquals(desgaste, recursos.getDesgaste(), "El desgaste no debía cambiar.");
    }

    // --- Cargas ---

    @Test
    @DisplayName("E1-09: cargar combustible y energía suma la cantidad indicada")
    public void testCargasValidas() throws Exception {
        recursos.cargarCombustible(15);
        recursos.cargarEnergia(5);

        assertEquals(75, recursos.getCombustible());
        assertEquals(85, recursos.getEnergia());
    }

    @Test
    @DisplayName("Se puede cargar exactamente hasta la capacidad máxima")
    public void testCargaHastaElMaximo() throws Exception {
        recursos.cargarCombustible(40);
        recursos.cargarEnergia(20);

        assertEquals(Recursos.CAPACIDAD_MAXIMA_COMBUSTIBLE, recursos.getCombustible());
        assertEquals(Recursos.CAPACIDAD_MAXIMA_ENERGIA, recursos.getEnergia());
    }

    @Test
    @DisplayName("Escenario D: una carga que excede la capacidad se rechaza y el estado anterior se conserva")
    public void testCargaQueExcedeLaCapacidad() {
        CapacidadExcedidaException excepcion =
                assertThrows(CapacidadExcedidaException.class, () -> recursos.cargarCombustible(41));

        assertEquals(TipoRecurso.COMBUSTIBLE, excepcion.getRecurso());
        assertEquals(60, excepcion.getValorActual());
        assertEquals(41, excepcion.getCantidadSolicitada());
        assertEquals(100, excepcion.getMaximo());
        verificarSinCambios(60, 80, 0);

        assertThrows(CapacidadExcedidaException.class, () -> recursos.cargarEnergia(21));
        verificarSinCambios(60, 80, 0);
    }

    @Test
    @DisplayName("Escenario D: una carga enorme no desborda el int ni deja recursos negativos")
    public void testCargaQueDesbordaElEntero() {
        assertThrows(CapacidadExcedidaException.class, () -> recursos.cargarCombustible(Integer.MAX_VALUE));
        assertThrows(CapacidadExcedidaException.class, () -> recursos.cargarEnergia(Integer.MAX_VALUE));
        assertThrows(CapacidadExcedidaException.class, () -> recursos.consumir(0, 0, Integer.MAX_VALUE));
        verificarSinCambios(60, 80, 0);
    }

    @Test
    @DisplayName("Rechazo: no se puede cargar ni consumir una cantidad negativa")
    public void testCantidadNegativa() {
        CantidadInvalidaException excepcion =
                assertThrows(CantidadInvalidaException.class, () -> recursos.cargarEnergia(-5));

        assertEquals(TipoRecurso.ENERGIA, excepcion.getRecurso());
        assertEquals(-5, excepcion.getCantidadRecibida());
        assertThrows(CantidadInvalidaException.class, () -> recursos.cargarCombustible(-1));
        assertThrows(CantidadInvalidaException.class, () -> recursos.consumir(4, -1, 4));
        verificarSinCambios(60, 80, 0);
    }

    // --- Consumo ---

    @Test
    @DisplayName("Costo de una operación: consume combustible y energía y suma desgaste")
    public void testConsumoValido() throws Exception {
        recursos.consumir(4, 5, 4);

        assertEquals(56, recursos.getCombustible());
        assertEquals(75, recursos.getEnergia());
        assertEquals(4, recursos.getDesgaste());
    }

    @Test
    @DisplayName("Escenario B: con combustible insuficiente se rechaza sin cambios parciales")
    public void testCombustibleInsuficiente() {
        RecursoInsuficienteException excepcion =
                assertThrows(RecursoInsuficienteException.class, () -> recursos.consumir(61, 5, 4));

        assertEquals(TipoRecurso.COMBUSTIBLE, excepcion.getRecurso());
        assertEquals(60, excepcion.getDisponible());
        assertEquals(61, excepcion.getCantidadSolicitada());
        verificarSinCambios(60, 80, 0);
    }

    @Test
    @DisplayName("Escenario B: con energía insuficiente tampoco se descuenta el combustible")
    public void testEnergiaInsuficiente() {
        RecursoInsuficienteException excepcion =
                assertThrows(RecursoInsuficienteException.class, () -> recursos.consumir(4, 81, 4));

        assertEquals(TipoRecurso.ENERGIA, excepcion.getRecurso());
        verificarSinCambios(60, 80, 0);
    }

    @Test
    @DisplayName("Escenario B: si el desgaste superaría el máximo, no se descuenta el combustible")
    public void testDesgasteQueSuperaElMaximo() throws Exception {
        recursos.consumir(0, 0, 98);

        CapacidadExcedidaException excepcion =
                assertThrows(CapacidadExcedidaException.class, () -> recursos.consumir(4, 0, 4));

        assertEquals(TipoRecurso.DESGASTE, excepcion.getRecurso());
        verificarSinCambios(60, 80, 98);
    }

    // --- Mantenimiento ---

    @Test
    @DisplayName("Ficha de Inicio: con desgaste 80 o más requiere mantenimiento; con 79, no")
    public void testUmbralDeMantenimiento() throws Exception {
        recursos.consumir(0, 0, 79);
        assertFalse(recursos.requiereMantenimiento());

        recursos.consumir(0, 0, 1);
        assertTrue(recursos.requiereMantenimiento());
    }

    @Test
    @DisplayName("Ficha de Inicio: realizar mantenimiento lleva el desgaste a 0")
    public void testRealizarMantenimiento() throws Exception {
        recursos.consumir(0, 0, 85);

        recursos.realizarMantenimiento();

        assertEquals(0, recursos.getDesgaste());
        assertFalse(recursos.requiereMantenimiento());
        assertEquals(60, recursos.getCombustible(), "El mantenimiento no modifica otros recursos.");
    }

    @Test
    @DisplayName("El mantenimiento también puede hacerse antes de llegar al umbral")
    public void testMantenimientoPreventivo() throws Exception {
        recursos.consumir(0, 0, 30);

        recursos.realizarMantenimiento();

        assertEquals(0, recursos.getDesgaste());
    }
}
