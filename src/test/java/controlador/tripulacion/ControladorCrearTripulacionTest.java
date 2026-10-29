package controlador.tripulacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import modelo.asistente.Asistente;
import modelo.asistente.AsistenteComando;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Origen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.principal.panelcreartripulacion.IVistaCrearTripulacion;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ControladorCrearTripulacion con vista simulada (Mock).
 */
public class ControladorCrearTripulacionTest {

    private Nave nave;
    private Asistente asistente;

    @BeforeEach
    public void inicializar() {
        nave = new NaveFactory().crearNave(TipoNave.EXPLORADORA);
        asistente = new AsistenteComando(nave);
    }

    @Test
    @DisplayName("Controlador: agregar tripulante válido lo incorpora a la lista")
    public void testAgregarTripulanteValido() {
        VistaCrearTripulacionMock vista = new VistaCrearTripulacionMock("Spock", Cargo.CONSEJERO, Origen.VULCANO, "5");
        ControladorCrearTripulacion controlador = new ControladorCrearTripulacion(vista, asistente, null);

        controlador.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearTripulacion.AGREGAR));

        assertNull(vista.ultimoError);
        assertEquals(1, vista.tripulantesEnLista.size());
        assertTrue(vista.tripulantesEnLista.get(0).contains("Spock"));
        assertTrue(vista.camposLimpiados);
    }

    @Test
    @DisplayName("Controlador: agregar tripulante con nombre vacío muestra error del modelo")
    public void testAgregarTripulanteNombreVacio() {
        VistaCrearTripulacionMock vista = new VistaCrearTripulacionMock("", Cargo.CONSEJERO, Origen.VULCANO, "5");
        ControladorCrearTripulacion controlador = new ControladorCrearTripulacion(vista, asistente, null);

        controlador.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearTripulacion.AGREGAR));

        assertNotNull(vista.ultimoError);
        assertEquals(0, vista.tripulantesEnLista.size());
    }

    @Test
    @DisplayName("Controlador: agregar tripulante con antigüedad no numérica muestra error")
    public void testAgregarTripulanteAntiguedadInvalida() {
        VistaCrearTripulacionMock vista = new VistaCrearTripulacionMock("Spock", Cargo.CONSEJERO, Origen.VULCANO, "abc");
        ControladorCrearTripulacion controlador = new ControladorCrearTripulacion(vista, asistente, null);

        controlador.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearTripulacion.AGREGAR));

        assertNotNull(vista.ultimoError);
        assertEquals(0, vista.tripulantesEnLista.size());
    }

    @Test
    @DisplayName("Controlador: no permite agregar dos capitanes")
    public void testRechazarSegundoCapitan() {
        VistaCrearTripulacionMock vista = new VistaCrearTripulacionMock("Kirk", Cargo.CAPITAN, Origen.TERRICOLA, "5");
        ControladorCrearTripulacion controlador = new ControladorCrearTripulacion(vista, asistente, null);

        controlador.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearTripulacion.AGREGAR));
        assertNull(vista.ultimoError);
        assertEquals(1, vista.tripulantesEnLista.size());

        // Intentar agregar segundo capitán
        vista.nombre = "Picard";
        vista.cargo = Cargo.CAPITAN;
        controlador.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearTripulacion.AGREGAR));

        assertNotNull(vista.ultimoError);
        assertTrue(vista.ultimoError.contains("capitán"));
        assertEquals(1, vista.tripulantesEnLista.size());
    }

    @Test
    @DisplayName("Controlador: armar tripulacion completa (1 capitan + 4) y aceptar asigna la tripulacion al asistente")
    public void testAceptarTripulacionCompleta() {
        VistaCrearTripulacionMock vista = new VistaCrearTripulacionMock("Kirk", Cargo.CAPITAN, Origen.TERRICOLA, "5");
        ControladorCrearTripulacion controlador = new ControladorCrearTripulacion(vista, asistente, null);

        // Agregar Capitán
        controlador.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearTripulacion.AGREGAR));

        // Agregar 4 adicionales
        Object[][] adicionales = {
                {"Spock", Cargo.CONSEJERO, Origen.VULCANO, "4"},
                {"McCoy", Cargo.TENIENTE, Origen.TERRICOLA, "3"},
                {"Sulu", Cargo.ALFEREZ, Origen.MARCIANO, "1"},
                {"Chekov", Cargo.ALFEREZ, Origen.TERRICOLA, "0"}
        };
        for (Object[] t : adicionales) {
            vista.nombre = (String) t[0];
            vista.cargo = (Cargo) t[1];
            vista.origen = (Origen) t[2];
            vista.textoAntiguedad = (String) t[3];
            controlador.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearTripulacion.AGREGAR));
        }

        assertEquals(5, vista.tripulantesEnLista.size());
        assertFalse(asistente.tieneTripulacion());

        // Aceptar tripulación
        controlador.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearTripulacion.ACEPTAR));

        assertNull(vista.ultimoError);
        assertTrue(asistente.tieneTripulacion());
        assertEquals(5, asistente.getCantidadTripulantes());
        assertEquals("Kirk", asistente.getCapitan().getNombre());
    }

    private static class VistaCrearTripulacionMock implements IVistaCrearTripulacion {
        String nombre;
        Cargo cargo;
        Origen origen;
        String textoAntiguedad;
        String ultimoError;
        String ultimoMensaje;
        boolean camposLimpiados = false;
        final List<String> tripulantesEnLista = new ArrayList<>();

        VistaCrearTripulacionMock(String nombre, Cargo cargo, Origen origen, String textoAntiguedad) {
            this.nombre = nombre;
            this.cargo = cargo;
            this.origen = origen;
            this.textoAntiguedad = textoAntiguedad;
        }

        @Override public String getNombre() { return nombre; }
        @Override public Cargo getCargo() { return cargo; }
        @Override public Origen getOrigen() { return origen; }
        @Override public String getTextoAntiguedad() { return textoAntiguedad; }

        @Override
        public void agregarTripulanteLista(String descripcion) {
            tripulantesEnLista.add(descripcion);
        }

        @Override
        public void limpiarCampos() {
            camposLimpiados = true;
        }

        @Override
        public void mostrarError(String mensaje) {
            this.ultimoError = mensaje;
        }

        @Override
        public void mostrarMensaje(String mensaje) {
            this.ultimoMensaje = mensaje;
        }

        @Override
        public void setControlador(ActionListener controlador) {}
    }
}
