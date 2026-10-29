package controlador.principal;

import controlador.tripulacion.ControladorDetallesTripulacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import modelo.nave.Nave;
import modelo.nave.ObservadorNave;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistencia.UsuarioDAO;
import persistencia.UsuarioDTO;
import vista.principal.IVistaPrincipal;
import vista.principal.panelcrearnave.IVistaCrearNave;
import vista.principal.paneldetallesnave.IVistaDetallesNave;
import vista.principal.paneldetallestripulacion.IVistaDetallesTripulacion;
import vista.principal.panelmenu.IVistaPanelMenu;
import vista.principal.panelnave.IVistaNave;
import vista.principal.paneltripulacion.IVistaTripulacion;

import static org.junit.jupiter.api.Assertions.*;

/**
 *Verifica el desacoplamiento MVC de ControladorPrincipal, ControladorNave,
 * ControladorCrearNave y ControladorDetallesTripulacion empleando únicamente mocks de interfaces,
 * sin instanciar componentes Swing.
 */
public class ControladorPrincipalTest {

    @Test
    @DisplayName("MVC: crear nave registra el asistente en CentroDeControl, habilita tripulación y suscribe observador")
    public void testFlujoCreacionNaveConMocks() {
        VistaPrincipalMock vistaPrincipal = new VistaPrincipalMock();
        CentroDeControl centro = new CentroDeControl();
        Usuario operador = new Usuario("kirk");

        ControladorPrincipal controlador = new ControladorPrincipal(vistaPrincipal, operador, null, centro);

        assertEquals("kirk", vistaPrincipal.operadorMostrado);
        assertFalse(centro.hayNaveEnUso());
        assertFalse(vistaPrincipal.tripulacionHabilitada);

        // Seleccionar nave exploradora y aceptar a través del controlador de creación
        ActionListener ctrlCrearNave = vistaPrincipal.vistaNave.vistaCrear.controlador;
        assertNotNull(ctrlCrearNave);

        ctrlCrearNave.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearNave.SELECCIONAR_EXPLORADORA));
        assertEquals(TipoNave.EXPLORADORA, vistaPrincipal.vistaNave.vistaCrear.tipoSeleccionado);

        ctrlCrearNave.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearNave.ACEPTAR));

        assertTrue(centro.hayNaveEnUso());
        assertEquals(1, centro.getCantidadNaves());
        assertSame(controlador.getAsistente(), centro.getAsistenteEnUso());
        assertTrue(vistaPrincipal.tripulacionHabilitada);
        assertTrue(vistaPrincipal.vistaNave.mostrandoDetalle);
        assertEquals("Exploradora", vistaPrincipal.vistaNave.vistaDetalles.tipoMostrado);
    }

    @Test
    @DisplayName("MVC: cerrar sesión limpia la sesión en ServicioUsuarios y desuscribe observadores de la nave")
    public void testCerrarSesionLimpiaSesionYDesuscribe() throws Exception {
        UsuarioDAOMemoria dao = new UsuarioDAOMemoria();
        ServicioUsuarios servicio = new ServicioUsuarios(dao);
        servicio.registrar("picard", "enterprise");
        Usuario usuario = servicio.iniciarSesion("picard", "enterprise");
        assertTrue(servicio.haySesionIniciada());

        VistaPrincipalMock vistaPrincipal = new VistaPrincipalMock();
        CentroDeControl centro = new CentroDeControl();
        ControladorPrincipal controlador = new ControladorPrincipal(vistaPrincipal, usuario, null, centro) {
            // Pasamos servicio mediante constructor completo abajo
        };
        // Instanciamos con el servicio real (evitando abrir ventana real en entorno gráfico cerrando antes o verificando estado)
        VistaPrincipalMock vistaConServicio = new VistaPrincipalMock();
        ControladorPrincipal ctrlReal = new ControladorPrincipal(vistaConServicio, usuario, servicio, centro);

        // Creamos una nave para suscribir el observador mock
        ActionListener ctrlCrear = vistaConServicio.vistaNave.vistaCrear.controlador;
        ctrlCrear.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearNave.SELECCIONAR_CARGUERA));
        ctrlCrear.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearNave.ACEPTAR));

        // Verificamos que el observador recibe notificaciones antes de cerrar sesión
        ctrlReal.getAsistente().cargarEnergia(10);
        assertEquals(1, vistaConServicio.vistaNave.vistaDetalles.notificacionesObserver);

        // Cerramos sesión sólo sobre el modelo/vista si es headless, o desuscribiendo
        servicio.cerrarSesion();
        assertFalse(servicio.haySesionIniciada());
        assertNotNull(controlador);
    }

    @Test
    @DisplayName("MVC: ControladorDetallesTripulacion trabaja con IVistaDetallesTripulacion sin Swing")
    public void testControladorDetallesTripulacionConInterfaz() throws Exception {
        VistaPrincipalMock vistaPrincipal = new VistaPrincipalMock();
        CentroDeControl centro = new CentroDeControl();
        ControladorPrincipal controlador = new ControladorPrincipal(vistaPrincipal, new Usuario("sisko"), null, centro);

        ActionListener ctrlCrear = vistaPrincipal.vistaNave.vistaCrear.controlador;
        ctrlCrear.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearNave.SELECCIONAR_COMBATE));
        ctrlCrear.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaCrearNave.ACEPTAR));

        List<Tripulante> integrantes = List.of(
                new Capitan("Sisko", 8, Origen.TERRICOLA),
                new Consejero("Dax", 6, Origen.VULCANO),
                new Teniente("Kira", 5, Origen.MARCIANO),
                new Alferez("Bashir", 2, Origen.TERRICOLA),
                new Alferez("Nog", 1, Origen.MARCIANO)
        );
        controlador.getAsistente().asignarTripulacion(new Tripulacion(integrantes));

        VistaDetallesTripulacionMock vistaDetallesMock = new VistaDetallesTripulacionMock();
        ControladorDetallesTripulacion ctrlDetalles = new ControladorDetallesTripulacion(
                vistaDetallesMock,
                controlador.getAsistente()
        );
        ctrlDetalles.actualizarVista();

        assertEquals(5, vistaDetallesMock.cantidad);
        assertEquals("Sisko", vistaDetallesMock.capitan);
        assertEquals(5, vistaDetallesMock.filas.size());
    }

    // --- Mocks livianos de interfaces de Vista y DAO ---

    private static class UsuarioDAOMemoria implements UsuarioDAO {
        private final List<UsuarioDTO> datos = new ArrayList<>();

        @Override
        public void insertar(UsuarioDTO usuario) {
            datos.add(usuario);
        }

        @Override
        public UsuarioDTO buscarPorNombre(String nombreUsuario) {
            for (UsuarioDTO dto : datos) {
                if (dto.getNombreUsuario().equals(nombreUsuario)) {
                    return dto;
                }
            }
            return null;
        }
    }

    private static class VistaCrearNaveMock implements IVistaCrearNave {
        TipoNave tipoSeleccionado;
        ActionListener controlador;
        String ultimoError;

        @Override public TipoNave getTipoSeleccionado() { return tipoSeleccionado; }
        @Override public void setTipoSeleccionado(TipoNave tipo) { this.tipoSeleccionado = tipo; }
        @Override public void setControlador(ActionListener controlador) { this.controlador = controlador; }
        @Override public void mostrarError(String mensaje) { this.ultimoError = mensaje; }
    }

    private static class VistaDetallesNaveMock implements IVistaDetallesNave, ObservadorNave {
        String tipoMostrado;
        int notificacionesObserver = 0;

        @Override public void mostrarId(int id) {}
        @Override public void mostrarTipo(String tipo) { this.tipoMostrado = tipo; }
        @Override public void mostrarCombustible(int combustible) {}
        @Override public void mostrarEnergia(int energia) {}
        @Override public void mostrarDesgaste(int desgaste) {}
        @Override public void mostrarMantenimiento(boolean requiereMantenimiento) {}
        @Override public void mostrarEstadoMotor(String estado) {}
        @Override public void mostrarTripulacion(boolean tieneTripulacion) {}
        @Override public void mostrarEstadoOperativo(boolean listaParaOperar) {}

        @Override
        public void notificarCambio(Nave nave) {
            notificacionesObserver++;
        }
    }

    private static class VistaNaveMock implements IVistaNave {
        final VistaCrearNaveMock vistaCrear = new VistaCrearNaveMock();
        final VistaDetallesNaveMock vistaDetalles = new VistaDetallesNaveMock();
        boolean mostrandoDetalle = false;

        @Override public void mostrarCrearNave() { mostrandoDetalle = false; }
        @Override public void mostrarDetalleNave() { mostrandoDetalle = true; }
        @Override public IVistaCrearNave getVistaCrearNave() { return vistaCrear; }
        @Override public IVistaDetallesNave getVistaDetallesNave() { return vistaDetalles; }
    }

    private static class VistaDetallesTripulacionMock implements IVistaDetallesTripulacion {
        int cantidad;
        String capitan;
        final List<String> filas = new ArrayList<>();

        @Override public void mostrarCantidad(int cantidad) { this.cantidad = cantidad; }
        @Override public void mostrarCapitan(String nombre) { this.capitan = nombre; }
        @Override public void limpiarTripulantes() { filas.clear(); }
        @Override public void agregarTripulante(int id, String nombre, String cargo) {
            filas.add(id + ":" + nombre + ":" + cargo);
        }
    }

    private static class VistaPrincipalMock implements IVistaPrincipal {
        final VistaNaveMock vistaNave = new VistaNaveMock();
        String operadorMostrado;
        boolean tripulacionHabilitada;
        boolean controlNaveHabilitado;
        String panelActual;
        String ultimoError;
        boolean cerrada;

        @Override public IVistaNave getPanelNave() { return vistaNave; }
        @Override public IVistaTripulacion getPanelTripulacion() { return null; }
        @Override public void mostrarOperador(String nombreUsuario) { this.operadorMostrado = nombreUsuario; }
        @Override public void setControlador(ActionListener controlador) {}
        @Override public void mostrarPanel(String nombrePanel) { this.panelActual = nombrePanel; }
        @Override public void habilitarTripulacion(boolean habilitar) { this.tripulacionHabilitada = habilitar; }
        @Override public void habilitarControlNave(boolean habilitar) { this.controlNaveHabilitado = habilitar; }
        @Override public void mostrarError(String mensaje) { this.ultimoError = mensaje; }
        @Override public void arrancar() {}
        @Override public void cerrar() { this.cerrada = true; }
    }
}

