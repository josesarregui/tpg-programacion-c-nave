package modelo.nave;

import excepcion.CapacidadExcedidaException;
import excepcion.RecursoInsuficienteException;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de E1-01 y E1-09 a nivel de la nave: asignación de la tripulación, capacidad operativa
 * según el mantenimiento y operaciones sobre recursos delegadas sin cambios parciales.
 */
public class NaveTest {

    private Nave nave;
    private Tripulacion tripulacion;

    @BeforeEach
    public void inicializar() throws Exception {
        nave = new NaveFactory().crearNave(TipoNave.EXPLORADORA);
        tripulacion = new Tripulacion(List.of(
                new Capitan("Kirk", 3, Origen.TERRICOLA),
                new Consejero("Troi", 2, Origen.VULCANO),
                new Teniente("Uhura", 5, Origen.MARCIANO),
                new Alferez("Chekov", 1, Origen.TERRICOLA),
                new Alferez("Sulu", 0, Origen.MARCIANO)));
    }

    @Test
    @DisplayName("Escenario A: una nave recién creada no opera hasta que se le asigna una tripulación válida")
    public void testAsignarTripulacion() {
        assertFalse(nave.estaListaParaOperar());

        nave.asignarTripulacion(tripulacion);

        assertSame(tripulacion, nave.getTripulacion());
        assertTrue(nave.estaListaParaOperar());
    }

    @Test
    @DisplayName("Rechazo: asignar una tripulación nula es un error de programación (aserción)")
    public void testAsignarTripulacionNula() {
        assertThrows(AssertionError.class, () -> nave.asignarTripulacion(null));
    }

    @Test
    @DisplayName("Guía I.8: una nave que requiere mantenimiento no está lista para operar hasta realizarlo")
    public void testMantenimientoYCapacidadOperativa() throws Exception {
        nave.asignarTripulacion(tripulacion);

        nave.consumirRecursos(0, 0, 80);
        assertTrue(nave.requiereMantenimiento());
        assertFalse(nave.estaListaParaOperar());

        nave.realizarMantenimiento();
        assertEquals(0, nave.getDesgaste());
        assertTrue(nave.estaListaParaOperar());
    }

    @Test
    @DisplayName("Aclaración R4: una nave con el motor fuera de Disponible no está lista para operar")
    public void testMotorNoDisponible() {
        nave.asignarTripulacion(tripulacion);

        nave.getMotor().prepararSalto();
        assertFalse(nave.estaListaParaOperar());

        nave.getMotor().iniciarWarp();
        nave.getMotor().desactivarWarp();
        assertFalse(nave.estaListaParaOperar(), "En Enfriamiento el motor todavía no está disponible.");

        nave.getMotor().enfriar();
        assertTrue(nave.estaListaParaOperar());
    }

    @Test
    @DisplayName("Recursos antes y después: cargas y consumo se reflejan en la nave")
    public void testOperacionesSobreRecursos() throws Exception {
        nave.cargarCombustible(10);
        nave.cargarEnergia(20);
        nave.consumirRecursos(4, 5, 4);

        assertEquals(66, nave.getCombustible());
        assertEquals(95, nave.getEnergia());
        assertEquals(4, nave.getDesgaste());
    }

    @Test
    @DisplayName("Escenario D: una carga rechazada en la nave conserva el estado anterior")
    public void testCargaRechazadaEnLaNave() {
        assertThrows(CapacidadExcedidaException.class, () -> nave.cargarEnergia(50));

        assertEquals(60, nave.getCombustible());
        assertEquals(80, nave.getEnergia());
        assertEquals(0, nave.getDesgaste());
    }

    @Test
    @DisplayName("Escenario B: una nave sin combustible suficiente rechaza la operación sin cambios parciales")
    public void testRecursosInsuficientesEnLaNave() throws Exception {
        nave.consumirRecursos(58, 0, 0);

        assertThrows(RecursoInsuficienteException.class, () -> nave.consumirRecursos(4, 0, 4));

        assertEquals(2, nave.getCombustible());
        assertEquals(80, nave.getEnergia());
        assertEquals(0, nave.getDesgaste());
    }
}
