package modelo.warp;

import excepcion.EstadoMotorInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotorWarpTest {

    private MotorWarp motor;

    @BeforeEach
    void setUp() {
        this.motor = new MotorWarp();
    }

    @Test
    @DisplayName("El motor debe iniciar en estado Disponible")
    void testEstadoInicialEsDisponible() {
        assertInstanceOf(DisponibleState.class, this.motor.getEstado());
    }

    @Test
    @DisplayName("Escenario C: Recorrer la secuencia válida completa de estados")
    void testCicloCompletoValidoEscenarioC() {
        // 1. Estado inicial: Disponible
        assertInstanceOf(DisponibleState.class, this.motor.getEstado());

        // 2. Preparar salto -> PreparandoSaltoState
        this.motor.prepararSalto();
        assertInstanceOf(PreparandoSaltoState.class, this.motor.getEstado());

        // 3. Iniciar warp -> EnWarpState
        this.motor.iniciarWarp();
        assertInstanceOf(EnWarpState.class, this.motor.getEstado());

        // 4. Desactivar warp -> EnfriamientoState
        this.motor.desactivarWarp();
        assertInstanceOf(EnfriamientoState.class, this.motor.getEstado());

        // 5. Enfriar -> DisponibleState (cierre del ciclo)
        this.motor.enfriar();
        assertInstanceOf(DisponibleState.class, this.motor.getEstado());
    }

    @Test
    @DisplayName("Escenario C: Rechazar transiciones inválidas desde Disponible")
    void testTransicionesInvalidasDesdeDisponible() {
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.iniciarWarp());
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.desactivarWarp());
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.enfriar());

        // Verifica que el estado no cambió tras los intentos fallidos
        assertInstanceOf(DisponibleState.class, this.motor.getEstado());
    }

    @Test
    @DisplayName("Rechazar transiciones inválidas desde PreparandoSalto")
    void testTransicionesInvalidasDesdePreparandoSalto() {
        this.motor.prepararSalto();

        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.prepararSalto());
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.desactivarWarp());
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.enfriar());

        assertInstanceOf(PreparandoSaltoState.class, this.motor.getEstado());
    }

    @Test
    @DisplayName("Rechazar transiciones inválidas desde EnWarp")
    void testTransicionesInvalidasDesdeEnWarp() {
        this.motor.prepararSalto();
        this.motor.iniciarWarp();

        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.prepararSalto());
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.iniciarWarp());
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.enfriar());

        assertInstanceOf(EnWarpState.class, this.motor.getEstado());
    }

    @Test
    @DisplayName("Rechazar transiciones inválidas desde Enfriamiento")
    void testTransicionesInvalidasDesdeEnfriamiento() {
        this.motor.prepararSalto();
        this.motor.iniciarWarp();
        this.motor.desactivarWarp();

        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.prepararSalto());
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.iniciarWarp());
        assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.desactivarWarp());

        assertInstanceOf(EnfriamientoState.class, this.motor.getEstado());
    }

    @Test
    @DisplayName("Aclaración R4: el motor sólo está disponible en el estado Disponible")
    void testEstaDisponibleSoloEnDisponible() {
        assertTrue(this.motor.estaDisponible());

        this.motor.prepararSalto();
        assertFalse(this.motor.estaDisponible());

        this.motor.iniciarWarp();
        assertFalse(this.motor.estaDisponible());

        this.motor.desactivarWarp();
        assertFalse(this.motor.estaDisponible());

        this.motor.enfriar();
        assertTrue(this.motor.estaDisponible());
    }

    @Test
    @DisplayName("Escenario C: la excepción de una transición inválida guarda el estado en que se rechazó")
    void testExcepcionGuardaElEstadoActual() {
        this.motor.prepararSalto();

        EstadoMotorInvalidoException excepcion =
                assertThrows(EstadoMotorInvalidoException.class, () -> this.motor.enfriar());

        assertEquals("Preparando salto", excepcion.getEstadoActual());
        assertEquals("MotorWarp [estado=Preparando salto]", this.motor.toString());
    }

    @Test
    @DisplayName("Diseño por Contrato: setEstado no debe admitir referencias nulas")
    void testSetEstadoRechazaNull() {
        assertThrows(NullPointerException.class, () -> this.motor.setEstado(null));
    }
}