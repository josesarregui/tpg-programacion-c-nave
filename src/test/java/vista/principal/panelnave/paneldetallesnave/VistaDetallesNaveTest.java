package vista.principal.panelnave.paneldetallesnave;

import modelo.asistente.Asistente;
import modelo.asistente.AsistenteComando;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.Observador;
import controlador.nave.ControladorDetallesNave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Pruebas unitarias para VistaDetallesNave como observador del asistente (patrón Observer, Versión 2 de la
 * cátedra). Verifica que la vista se suscriba sola, que ante cada notificación consulte el estado del
 * asistente (pull) y que sus componentes gráficos reflejen los cambios en el hilo de eventos de Swing.
 */
public class VistaDetallesNaveTest {

    @BeforeEach
    public void verificarEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Las pruebas de componentes Swing requieren entorno gráfico.");
    }

    /** Espera a que el hilo de eventos de Swing procese las actualizaciones encoladas con invokeLater. */
    private void esperarEDT() throws Exception {
        javax.swing.SwingUtilities.invokeAndWait(() -> { });
    }

    private Asistente crearAsistente() {
        return new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
    }

    @Test
    @DisplayName("Observer: VistaDetallesNave es un Observador de la cátedra")
    public void testImplementaObservador() {
        VistaDetallesNave vista = new VistaDetallesNave();
        // La asignación lo verifica en compilación, sin consultar el tipo en ejecución.
        Observador observador = vista;
        assertSame(vista, observador);
    }

    @Test
    @DisplayName("Observer: la vista observa al asistente y consulta su estado ante cada notificación")
    public void testObservarActualizaRecursos() throws Exception {
        Asistente asistente = crearAsistente();
        VistaDetallesNave vista = new VistaDetallesNave();
        vista.observar(asistente);

        asistente.cargarCombustible(10);
        esperarEDT();
        assertEquals("Combustible: 70", vista.getTextoCombustible());
        assertEquals("Energía: 80", vista.getTextoEnergia());
        assertEquals("Desgaste: 0", vista.getTextoDesgaste());
        assertEquals("Mantenimiento: No requerido", vista.getTextoMantenimiento());
        assertEquals("Motor Warp: Disponible", vista.getTextoEstadoMotor());

        asistente.consumirRecursos(5, 10, 15);
        esperarEDT();
        assertEquals("Combustible: 65", vista.getTextoCombustible());
        assertEquals("Energía: 70", vista.getTextoEnergia());
        assertEquals("Desgaste: 15", vista.getTextoDesgaste());
    }

    @Test
    @DisplayName("Observer: una notificación desde otro hilo (la misión) actualiza la vista en el EDT")
    public void testNotificacionDesdeOtroHilo() throws Exception {
        Asistente asistente = crearAsistente();
        VistaDetallesNave vista = new VistaDetallesNave();
        vista.observar(asistente);

        Thread mision = new Thread(() -> asistente.saltarWarp());
        mision.start();
        mision.join(1000);
        esperarEDT();

        assertEquals("Motor Warp: " + asistente.getEstadoMotor(), vista.getTextoEstadoMotor());
    }

    @Test
    @DisplayName("Observer: tras dejar de observar, la vista no se actualiza; al observar otro asistente, sólo sigue a ése")
    public void testDejarDeObservarYCambiarDeAsistente() throws Exception {
        Asistente primero = crearAsistente();
        Asistente segundo = crearAsistente();
        VistaDetallesNave vista = new VistaDetallesNave();

        vista.observar(primero);
        vista.observar(segundo);
        primero.cargarCombustible(30);
        esperarEDT();
        assertNotEquals("Combustible: 90", vista.getTextoCombustible(), "La vista no debe seguir al asistente anterior.");

        segundo.cargarEnergia(5);
        esperarEDT();
        assertEquals("Energía: 85", vista.getTextoEnergia());

        vista.dejarDeObservar();
        segundo.cargarEnergia(5);
        esperarEDT();
        assertEquals("Energía: 85", vista.getTextoEnergia(), "Tras dejar de observar no debe actualizarse.");
    }

    @Test
    @DisplayName("Observer: ControladorDetallesNave entrega el asistente y la vista se actualiza en tiempo real")
    public void testActualizacionEnTiempoRealConControlador() throws Exception {
        Asistente asistente = crearAsistente();
        VistaDetallesNave vista = new VistaDetallesNave();

        // Al instanciar el controlador, la vista pasa a observar al asistente
        ControladorDetallesNave controlador = new ControladorDetallesNave(vista, asistente);

        controlador.actualizarVista();

        assertEquals("Combustible: 60", vista.getTextoCombustible());
        assertEquals("Energía: 80", vista.getTextoEnergia());

        // Operamos a través del asistente (simulando una misión, recarga o mantenimiento)
        asistente.cargarCombustible(20);
        esperarEDT();

        // La vista debe haberse actualizado sin llamar a actualizarVista() manualmente
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

        // Al desuscribir, la vista deja de seguir al asistente
        String energiaAntes = vista.getTextoEnergia();
        controlador.desuscribir();
        asistente.cargarEnergia(5);
        esperarEDT();
        assertEquals(energiaAntes, vista.getTextoEnergia(), "Tras desuscribir no debe actualizarse.");
    }
}
