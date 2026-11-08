package controlador.concurrencia;

import modelo.concurrencia.GeneradoraDeEnergia;
import modelo.concurrencia.NaveConsumidora;
import modelo.concurrencia.PlataformaDeCarga;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.concurrencia.IVistaConcurrencia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class ControladorConcurrenciaTest {

    private static class VistaConcurrenciaMock implements IVistaConcurrencia {
        ActionListener controlador;
        final List<String> eventos = new ArrayList<>();
        int ultimasBaterias = -1;
        int ultimasGeneradoras = -1;
        int ultimasNaves = -1;
        boolean consolaLimpiada = false;
        String ultimoError;

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public void agregarEvento(String evento) {
            this.eventos.add(evento);
        }

        @Override
        public void limpiarConsola() {
            this.consolaLimpiada = true;
            this.eventos.clear();
        }

        @Override
        public void notificarEvento(String mensaje, int bateriasActuales) {
            this.eventos.add(mensaje);
            this.ultimasBaterias = bateriasActuales;
        }

        @Override
        public void actualizarBaterias(int bateriasActuales, int capacidadMaxima) {
            this.ultimasBaterias = bateriasActuales;
        }

        @Override
        public void actualizarEntidades(int generadorasActivas, int navesActivas) {
            this.ultimasGeneradoras = generadorasActivas;
            this.ultimasNaves = navesActivas;
        }

        @Override
        public void mostrarError(String mensaje) {
            this.ultimoError = mensaje;
        }

        @Override
        public void agregarGeneradora(String idGeneradora) {
        }

        @Override
        public void agregarNaveConsumidora(String idNave) {
        }

        @Override
        public void removerGeneradora(String idGeneradora) {
        }

        @Override
        public void removerNaveConsumidora(String idNave) {
        }

        @Override
        public void limpiarEntidades() {
        }

        @Override
        public void setOyenteDetenerGeneradora(Consumer<String> oyente) {
        }

        @Override
        public void setOyenteDetenerNave(Consumer<String> oyente) {
        }
    }

    @Test
    @DisplayName("Inicialización vincula vista y controlador con contadores iniciales")
    public void testInicializacion() {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataforma = new PlataformaDeCarga(4);

        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataforma);

        assertNotNull(vistaMock.controlador);
        assertEquals(0, vistaMock.ultimasBaterias);
        assertEquals(0, vistaMock.ultimasGeneradoras);
        assertEquals(0, vistaMock.ultimasNaves);
        ctrl.detenerTodo();
    }

    @Test
    @DisplayName("Acción CREAR_GENERADORA y CREAR_NAVE instancian Runnable e inician los hilos")
    public void testCreacionDinamica() throws InterruptedException {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataforma = new PlataformaDeCarga(5);
        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataforma);

        ctrl.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaConcurrencia.COMANDO_CREAR_GENERADORA));
        assertEquals(1, ctrl.getGeneradoras().size());
        assertEquals(1, ctrl.getHilos().size());
        GeneradoraDeEnergia gen = ctrl.getGeneradoras().get(0);
        assertTrue(gen.isActivo());
        assertTrue(ctrl.getHilos().get(0).isAlive());

        ctrl.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaConcurrencia.COMANDO_CREAR_NAVE));
        assertEquals(1, ctrl.getNaves().size());
        assertEquals(2, ctrl.getHilos().size());
        NaveConsumidora nave = ctrl.getNaves().get(0);
        assertTrue(nave.isActivo());
        assertTrue(ctrl.getHilos().get(1).isAlive());

        ctrl.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaConcurrencia.COMANDO_LIMPIAR_CONSOLA));
        assertTrue(vistaMock.consolaLimpiada);

        // Verificamos que el modelo (Plataforma) notifica directamente a la Vista
        plataforma.depositar("Prueba-Directa");
        assertFalse(vistaMock.eventos.isEmpty());
        assertTrue(vistaMock.eventos.get(vistaMock.eventos.size() - 1).contains("Prueba-Directa"));

        ctrl.detenerTodo();
        assertFalse(gen.isActivo());
        assertFalse(nave.isActivo());
    }

    @Test
    @DisplayName("Controlador captura excepciones y envía mensaje de error a la vista sin relanzarla")
    public void testCapturaDeExcepcionHaciaVista() {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataformaFalla = new PlataformaDeCarga(3) {
            @Override
            public synchronized void notificarIngreso(String tipo, String id) {
                throw new IllegalStateException("Simulación de fallo en plataforma");
            }
        };

        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataformaFalla);

        // Al ejecutar la acción, no debe lanzarse excepción hacia afuera
        assertDoesNotThrow(() -> {
            ctrl.actionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaConcurrencia.COMANDO_CREAR_GENERADORA));
        });

        // La excepción fue capturada por el controlador y enviada a la vista como
        // mensaje
        assertNotNull(vistaMock.ultimoError);
        assertEquals("Simulación de fallo en plataforma", vistaMock.ultimoError);

        ctrl.detenerTodo();
    }

    @Test
    @DisplayName("detenerGeneradora y detenerNave detienen e interrumpen el hilo individualmente")
    public void testDetencionIndividual() {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataforma = new PlataformaDeCarga(4);
        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataforma);

        GeneradoraDeEnergia gen = ctrl.crearGeneradora();
        NaveConsumidora nave = ctrl.crearNave();

        assertEquals(1, ctrl.getGeneradorasActivas());
        assertEquals(1, ctrl.getNavesActivas());
        assertTrue(gen.isActivo());
        assertTrue(nave.isActivo());

        // Detenemos la generadora
        boolean detuvoGen = ctrl.detenerGeneradora(gen.getIdGeneradora());
        assertTrue(detuvoGen);
        assertFalse(gen.isActivo());
        assertEquals(0, ctrl.getGeneradorasActivas());

        // Detenemos la nave
        boolean detuvoNave = ctrl.detenerNave(nave.getIdNave());
        assertTrue(detuvoNave);
        assertFalse(nave.isActivo());
        assertEquals(0, ctrl.getNavesActivas());

        // Intentar detener id inexistente devuelve false
        assertFalse(ctrl.detenerGeneradora("ID_FANTASMA"));
        assertFalse(ctrl.detenerNave("ID_FANTASMA"));

        ctrl.detenerTodo();
    }
}
