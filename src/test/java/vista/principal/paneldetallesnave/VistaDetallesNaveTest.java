package vista.principal.paneldetallesnave;

import modelo.asistente.AsistenteComando;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.ObservadorNave;
import modelo.nave.TipoNave;
import controlador.nave.ControladorDetallesNave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Pruebas unitarias para VistaDetallesNave como observador en el patrón Observer.
 * Verifica que implemente ObservadorNave y que sus componentes gráficos reflejen
 * en tiempo real las modificaciones sobre los recursos y estado de la nave.
 */
public class VistaDetallesNaveTest {

    @BeforeEach
    public void verificarEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Las pruebas de componentes Swing requieren entorno gráfico.");
    }

    private void esperarEDT() throws Exception {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeAndWait(() -> {});
        }
    }

    @Test
    @DisplayName("Observer: VistaDetallesNave implementa la interfaz ObservadorNave")
    public void testImplementaObservadorNave() {
        VistaDetallesNave vista = new VistaDetallesNave();
        assertTrue(vista instanceof ObservadorNave, "VistaDetallesNave debe implementar ObservadorNave.");
    }

    @Test
    @DisplayName("Observer: notificarCambio actualiza los textos de los recursos en la vista")
    public void testNotificarCambioActualizaRecursos() throws Exception {
        Nave nave = new NaveFactory().crearNave(TipoNave.EXPLORADORA);
        VistaDetallesNave vista = new VistaDetallesNave();

        // Notificación inicial
        vista.notificarCambio(nave);
        esperarEDT();

        assertEquals("Combustible: 60", vista.getTextoCombustible());
        assertEquals("Energía: 80", vista.getTextoEnergia());
        assertEquals("Desgaste: 0", vista.getTextoDesgaste());
        assertEquals("Mantenimiento: No requerido", vista.getTextoMantenimiento());
        assertEquals("Motor Warp: Disponible", vista.getTextoEstadoMotor());

        // Modificamos recursos
        nave.cargarCombustible(10);
        nave.consumirRecursos(5, 10, 15);

        // Notificamos a la vista
        vista.notificarCambio(nave);
        esperarEDT();

        assertEquals("Combustible: 65", vista.getTextoCombustible());
        assertEquals("Energía: 70", vista.getTextoEnergia());
        assertEquals("Desgaste: 15", vista.getTextoDesgaste());
    }

    @Test
    @DisplayName("Observer: suscripción automática mediante ControladorDetallesNave actualiza en tiempo real")
    public void testActualizacionEnTiempoRealConControlador() throws Exception {
        Nave nave = new NaveFactory().crearNave(TipoNave.EXPLORADORA);
        AsistenteComando asistente = new AsistenteComando(nave);
        VistaDetallesNave vista = new VistaDetallesNave();

        // Al instanciar el controlador, suscribe la vista a la nave
        ControladorDetallesNave controlador = new ControladorDetallesNave(vista, asistente);
        controlador.actualizarVista();

        assertEquals("Combustible: 60", vista.getTextoCombustible());
        assertEquals("Energía: 80", vista.getTextoEnergia());

        // Operamos a través del asistente (simulando una misión, recarga o mantenimiento)
        asistente.cargarCombustible(20);
        esperarEDT();

        // La vista debe haberse actualizado inmediatamente sin llamar a actualizarVista() manualmente
        assertEquals("Combustible: 80", vista.getTextoCombustible());

        asistente.cargarEnergia(10);
        esperarEDT();
        assertEquals("Energía: 90", vista.getTextoEnergia());

        // Desgaste y mantenimiento en tiempo real
        asistente.consumirRecursos(10, 10, 80);
        esperarEDT();
        assertEquals("Desgaste: 80", vista.getTextoDesgaste());
        assertEquals("Mantenimiento: Requerido", vista.getTextoMantenimiento());

        asistente.realizarMantenimiento();
        esperarEDT();
        assertEquals("Desgaste: 0", vista.getTextoDesgaste());
        assertEquals("Mantenimiento: No requerido", vista.getTextoMantenimiento());
    }
}
