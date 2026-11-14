package modelo.warp;

import excepcion.EstadoMotorInvalidoException;
import excepcion.RecursoInsuficienteException;
import modelo.nave.TipoRecurso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del Motor Warp según el Anexo ("Navegación y Motor Warp"). El motor se prueba con un suministro de
 * prueba, sin nave: así se controlan exactamente los recursos de cada caso.
 */
class MotorWarpTest {

    /** Suministro de prueba: recursos en variables, con el desgaste limitado a 100 como en la nave. */
    private static class SuministroDePrueba implements SuministroMotor {
        int combustible;
        int energia;
        int desgaste;

        SuministroDePrueba(int combustible, int energia, int desgaste) {
            this.combustible = combustible;
            this.energia = energia;
            this.desgaste = desgaste;
        }

        @Override
        public int getCombustible() {
            return combustible;
        }

        @Override
        public int getEnergia() {
            return energia;
        }

        @Override
        public void consumirCombustible(int cantidad) {
            combustible -= cantidad;
        }

        @Override
        public void consumirEnergia(int cantidad) {
            energia -= cantidad;
        }

        @Override
        public void sumarDesgaste(int cantidad) {
            desgaste = Math.min(100, desgaste + cantidad);
        }
    }

    private SuministroDePrueba suministro;
    private MotorWarp motor;

    @BeforeEach
    void setUp() {
        suministro = new SuministroDePrueba(60, 80, 0);
        motor = new MotorWarp(suministro);
    }

    private void assertRecursos(int combustible, int energia, int desgaste) {
        assertEquals(combustible, suministro.combustible, "combustible");
        assertEquals(energia, suministro.energia, "energía");
        assertEquals(desgaste, suministro.desgaste, "desgaste");
    }

    /** Lleva el motor al estado indicado con la cantidad de turnos warp necesaria. */
    private void avanzarTurnosWarp(int turnos) {
        for (int i = 0; i < turnos; i++) {
            motor.saltarWarp();
        }
    }

    @Test
    @DisplayName("El motor se crea Disponible")
    void testEstadoInicial() {
        assertEquals("Disponible", motor.getEstado());
        assertTrue(motor.estaDisponible());
    }

    // --- Salto normal ---

    @Test
    @DisplayName("Anexo: el salto normal avanza 1 casillero, consume 2 de combustible y el motor sigue Disponible")
    void testSaltoNormal() throws Exception {
        int casilleros = motor.saltar();

        assertEquals(1, casilleros);
        assertEquals("Disponible", motor.getEstado());
        assertRecursos(58, 80, 0);
    }

    @Test
    @DisplayName("Salto normal sin combustible suficiente: se rechaza y nada cambia")
    void testSaltoNormalSinCombustible() {
        suministro.combustible = 1;

        RecursoInsuficienteException e = assertThrows(RecursoInsuficienteException.class, motor::saltar);

        assertEquals(TipoRecurso.COMBUSTIBLE, e.getRecurso());
        assertEquals("Disponible", motor.getEstado());
        assertRecursos(1, 80, 0);
    }

    // --- Maniobra warp ---

    @Test
    @DisplayName("Escenario C / Anexo: ciclo completo Disponible -> Preparando (2 turnos) -> En warp -> Enfriamiento -> Disponible")
    void testCicloWarpCompleto() {
        assertEquals(0, motor.saltarWarp());
        assertEquals("Preparando salto", motor.getEstado());
        assertFalse(motor.estaDisponible());

        assertEquals(0, motor.saltarWarp());
        assertEquals("En warp", motor.getEstado());
        assertRecursos(60, 80, 0);

        assertEquals(2, motor.saltarWarp(), "El salto warp avanza 2 casilleros.");
        assertEquals("Enfriamiento", motor.getEstado());
        assertRecursos(60, 75, 0);

        assertEquals(0, motor.saltarWarp());
        assertEquals("Disponible", motor.getEstado());
        assertTrue(motor.estaDisponible());
        assertRecursos(60, 75, 3);
    }

    @Test
    @DisplayName("Anexo: el salto warp no consume combustible (decisión del grupo)")
    void testWarpNoConsumeCombustible() {
        avanzarTurnosWarp(4);

        assertEquals(60, suministro.combustible);
    }

    @Test
    @DisplayName("Anexo: sin 5 de energía la preparación no avanza y el motor sigue preparando")
    void testPreparacionSinEnergia() {
        suministro.energia = 4;

        avanzarTurnosWarp(3);
        assertEquals("Preparando salto", motor.getEstado());
        assertRecursos(60, 4, 0);

        suministro.energia = 10;
        assertEquals(0, motor.saltarWarp());
        assertEquals("Preparando salto", motor.getEstado(), "Hacen falta dos turnos de preparación con energía.");
        assertEquals(0, motor.saltarWarp());
        assertEquals("En warp", motor.getEstado());
    }

    @Test
    @DisplayName("Anexo: si al saltar ya no hay energía, el salto se aborta y el motor vuelve a preparar")
    void testSaltoWarpAbortado() {
        avanzarTurnosWarp(2);
        assertEquals("En warp", motor.getEstado());
        suministro.energia = 4;

        assertEquals(0, motor.saltarWarp(), "Un salto abortado no avanza.");
        assertEquals("Preparando salto", motor.getEstado());
        assertRecursos(60, 4, 0);
    }

    @Test
    @DisplayName("El enfriamiento con desgaste alto deja el desgaste en el máximo (100)")
    void testEnfriamientoConDesgasteAlto() {
        suministro.desgaste = 99;

        avanzarTurnosWarp(4);

        assertEquals("Disponible", motor.getEstado());
        assertEquals(100, suministro.desgaste);
    }

    // --- Transiciones inválidas (Escenario C) ---

    @Test
    @DisplayName("Escenario C: el salto normal se rechaza en Preparando salto, sin cambios")
    void testSaltoNormalInvalidoAlPreparar() {
        avanzarTurnosWarp(1);

        EstadoMotorInvalidoException e = assertThrows(EstadoMotorInvalidoException.class, motor::saltar);

        assertEquals("Preparando salto", e.getEstadoActual());
        assertEquals("Preparando salto", motor.getEstado());
        assertRecursos(60, 80, 0);
    }

    @Test
    @DisplayName("Escenario C: el salto normal se rechaza en En warp, sin cambios")
    void testSaltoNormalInvalidoEnWarp() {
        avanzarTurnosWarp(2);

        EstadoMotorInvalidoException e = assertThrows(EstadoMotorInvalidoException.class, motor::saltar);

        assertEquals("En warp", e.getEstadoActual());
        assertEquals("En warp", motor.getEstado());
        assertRecursos(60, 80, 0);
    }

    @Test
    @DisplayName("Escenario C: el salto normal se rechaza en Enfriamiento, sin cambios")
    void testSaltoNormalInvalidoEnEnfriamiento() {
        avanzarTurnosWarp(3);

        EstadoMotorInvalidoException e = assertThrows(EstadoMotorInvalidoException.class, motor::saltar);

        assertEquals("Enfriamiento", e.getEstadoActual());
        assertEquals("Enfriamiento", motor.getEstado());
        assertRecursos(60, 75, 0);
    }
}
