package modelo.nave;

import excepcion.CapacidadExcedidaException;
import excepcion.RecursoInsuficienteException;
import modelo.asistente.AsistenteComando;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el patrón Observer en el modelo de la nave (E2 / E3).
 * Verifica que los observadores se registren, desregistren y reciban notificaciones
 * ante cambios en los recursos, tripulación y motor, sin notificar cuando las operaciones fallan.
 */
public class ObservadorNaveTest {

    private Nave nave;
    private AsistenteComando asistente;

    @BeforeEach
    public void inicializar() {
        nave = new NaveFactory().crearNave(TipoNave.EXPLORADORA);
        asistente = new AsistenteComando(nave);
    }

    @Test
    @DisplayName("Observer: registro y eliminación de observadores en la nave")
    public void testRegistroYEliminacionDeObservadores() {
        AtomicInteger notificaciones = new AtomicInteger(0);
        ObservadorNave observador = n -> notificaciones.incrementAndGet();

        assertEquals(0, nave.getObservadores().size());

        nave.agregarObservador(observador);
        assertEquals(1, nave.getObservadores().size());

        // No duplicar observadores idénticos
        nave.agregarObservador(observador);
        assertEquals(1, nave.getObservadores().size());

        nave.eliminarObservador(observador);
        assertEquals(0, nave.getObservadores().size());
    }

    @Test
    @DisplayName("Observer: agregar observador nulo arroja AssertionError")
    public void testAgregarObservadorNulo() {
        assertThrows(AssertionError.class, () -> nave.agregarObservador(null));
    }

    @Test
    @DisplayName("Observer: notifica en tiempo real al cargar combustible")
    public void testNotificacionAlCargarCombustible() throws Exception {
        AtomicInteger combustibleRecibido = new AtomicInteger(-1);
        nave.agregarObservador(n -> combustibleRecibido.set(n.getCombustible()));

        nave.cargarCombustible(15);
        assertEquals(75, combustibleRecibido.get());
    }

    @Test
    @DisplayName("Observer: notifica en tiempo real al cargar energía")
    public void testNotificacionAlCargarEnergia() throws Exception {
        AtomicInteger energiaRecibida = new AtomicInteger(-1);
        nave.agregarObservador(n -> energiaRecibida.set(n.getEnergia()));

        nave.cargarEnergia(10);
        assertEquals(90, energiaRecibida.get());
    }

    @Test
    @DisplayName("Observer: notifica en tiempo real al consumir recursos")
    public void testNotificacionAlConsumirRecursos() throws Exception {
        List<Integer> estados = new ArrayList<>();
        nave.agregarObservador(n -> {
            estados.clear();
            estados.add(n.getCombustible());
            estados.add(n.getEnergia());
            estados.add(n.getDesgaste());
        });

        nave.consumirRecursos(10, 20, 15);
        assertEquals(List.of(50, 60, 15), estados);
    }

    @Test
    @DisplayName("Observer: notifica en tiempo real al realizar mantenimiento")
    public void testNotificacionAlRealizarMantenimiento() throws Exception {
        nave.consumirRecursos(0, 0, 80);
        assertTrue(nave.requiereMantenimiento());

        AtomicInteger desgasteObservado = new AtomicInteger(-1);
        nave.agregarObservador(n -> desgasteObservado.set(n.getDesgaste()));

        nave.realizarMantenimiento();
        assertEquals(0, desgasteObservado.get());
        assertFalse(nave.requiereMantenimiento());
    }

    @Test
    @DisplayName("Observer: notifica al asignar tripulación")
    public void testNotificacionAlAsignarTripulacion() throws Exception {
        AtomicInteger notificaciones = new AtomicInteger(0);
        nave.agregarObservador(n -> notificaciones.incrementAndGet());

        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Picard", 10, Origen.TERRICOLA));
        integrantes.add(new Consejero("Troi", 5, Origen.VULCANO));
        integrantes.add(new Teniente("Worf", 4, Origen.MARCIANO));
        integrantes.add(new Alferez("Crusher", 1, Origen.TERRICOLA));
        integrantes.add(new Alferez("Data", 0, Origen.MARCIANO));
        Tripulacion tripulacion = new Tripulacion(integrantes);

        nave.asignarTripulacion(tripulacion);
        assertEquals(1, notificaciones.get());
        assertTrue(nave.estaListaParaOperar());
    }

    @Test
    @DisplayName("Observer: una operación rechazada por excepción NO notifica a los observadores")
    public void testOperacionRechazadaNoNotifica() {
        AtomicInteger notificaciones = new AtomicInteger(0);
        nave.agregarObservador(n -> notificaciones.incrementAndGet());

        // Carga que excede el máximo permitido
        assertThrows(CapacidadExcedidaException.class, () -> nave.cargarCombustible(50));
        assertEquals(0, notificaciones.get(), "No debe notificar si la carga fue rechazada");

        // Consumo insuficiente
        assertThrows(RecursoInsuficienteException.class, () -> nave.consumirRecursos(100, 0, 0));
        assertEquals(0, notificaciones.get(), "No debe notificar si el consumo fue rechazado");
    }

    @Test
    @DisplayName("Observer: suscripción y notificación a través de AsistenteComando")
    public void testDelegacionDesdeAsistenteComando() throws Exception {
        AtomicInteger combustibleObservado = new AtomicInteger(-1);
        ObservadorNave observador = n -> combustibleObservado.set(n.getCombustible());

        asistente.agregarObservador(observador);
        asistente.cargarCombustible(10);
        assertEquals(70, combustibleObservado.get());

        // Desuscribir a través del asistente
        asistente.eliminarObservador(observador);
        asistente.cargarCombustible(5);
        assertEquals(70, combustibleObservado.get(), "Tras eliminar el observador, no debe recibir más notificaciones");
    }

    @Test
    @DisplayName("Observer: transiciones de motor notifican a los observadores")
    public void testNotificacionMotorSalto() throws Exception {
        AtomicInteger notificaciones = new AtomicInteger(0);
        asistente.agregarObservador(n -> notificaciones.incrementAndGet());

        asistente.prepararSalto();
        assertEquals(1, notificaciones.get());

        asistente.saltar();
        assertEquals(2, notificaciones.get());
    }
}
