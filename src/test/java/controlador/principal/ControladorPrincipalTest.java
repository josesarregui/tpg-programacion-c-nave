package controlador.principal;

import controlador.VistasDePrueba.CrearTripulacionDePrueba;
import controlador.VistasDePrueba.DetallesTripulacionDePrueba;
import controlador.VistasDePrueba.FabricaDePrueba;
import controlador.VistasDePrueba.PrincipalDePrueba;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import modelo.liquidacion.ConceptoHaber;
import modelo.liquidacion.LiquidadorHaberes;
import modelo.liquidacion.ReciboHaberes;
import modelo.nave.TipoNave;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Tripulante;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistencia.UsuarioDAO;
import persistencia.UsuarioDTO;
import vista.principal.IVistaPrincipal;
import vista.principal.panelmenu.IVistaPanelMenu;
import vista.principal.panelnave.panelcrearnave.IVistaCrearNave;

import static controlador.VistasDePrueba.evento;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Secciones de Creación y de Tripulantes (Enunciado E2, Vistas, puntos 2 y 3) a través de ControladorPrincipal y
 * sus subcontroladores, con vistas de prueba que implementan las interfaces {@code IVista...}, sin Swing.
 */
public class ControladorPrincipalTest {

    private CentroDeControl centro;
    private PrincipalDePrueba vista;
    private ControladorPrincipal controlador;

    @BeforeEach
    public void inicializar() throws Exception {
        ServicioUsuarios servicio = new ServicioUsuarios(new UsuarioDAOMemoria());
        servicio.registrar("kirk", "enterprise");
        Usuario operador = servicio.iniciarSesion("kirk", "enterprise");
        centro = new CentroDeControl();
        vista = new PrincipalDePrueba();
        controlador = new ControladorPrincipal(vista, operador, servicio, centro, new FabricaDePrueba());
    }

    @Test
    @DisplayName("Creación: la nave se crea con NaveFactory, se asocia a un AsistenteComando y se registra en el centro")
    public void crearNave() {
        assertEquals("kirk", vista.operadorMostrado);
        assertFalse(centro.hayNaveEnUso());
        assertFalse(vista.tripulacionHabilitada);

        crearNave(IVistaCrearNave.SELECCIONAR_EXPLORADORA);

        assertEquals(TipoNave.EXPLORADORA, vista.nave.crear.tipoSeleccionado);
        assertTrue(centro.hayNaveEnUso());
        assertEquals(1, centro.getCantidadNaves());
        assertSame(controlador.getAsistente(), centro.getAsistenteEnUso());
        assertEquals(TipoNave.EXPLORADORA, controlador.getAsistente().getTipoNave());
        assertTrue(vista.tripulacionHabilitada);
        assertTrue(vista.nave.mostrandoDetalle);
        assertEquals("Exploradora", vista.nave.detalles.tipoMostrado);
        assertSame(controlador.getAsistente(), vista.nave.detalles.getObservado(), "La vista de detalles se suscribe sola.");
    }

    @Test
    @DisplayName("Creación: aceptar sin elegir el tipo de nave muestra el rechazo y no registra nada")
    public void crearNaveSinTipo() {
        vista.nave.crear.controlador.actionPerformed(evento(IVistaCrearNave.ACEPTAR));

        assertEquals("Debe seleccionar una nave.", vista.nave.crear.ultimoError);
        assertFalse(centro.hayNaveEnUso());
        assertFalse(controlador.tieneAsistente());
        assertFalse(vista.tripulacionHabilitada);
    }

    @Test
    @DisplayName("Creación: se crean Capitán, Consejero, Teniente y Alférez y la tripulación se asigna a la nave")
    public void crearTripulacion() {
        crearNave(IVistaCrearNave.SELECCIONAR_CARGUERA);
        CrearTripulacionDePrueba crear = vista.tripulacion.crear;

        crear.armarTripulacionCompleta();

        assertNull(crear.ultimoError);
        assertEquals(5, crear.lista.size());
        assertTrue(crear.lista.get(1).contains("2 consejos"), "El consejero muestra sus consejos iniciales.");
        assertTrue(controlador.getAsistente().tieneTripulacion());
        assertEquals(5, controlador.getAsistente().getCantidadTripulantes());
        assertEquals("James Kirk", controlador.getAsistente().getCapitan().getNombre());
        assertTrue(vista.controlNaveHabilitado);
        assertNotNull(controlador.getControladorControl(), "Se habilita la sección Control de la Nave.");
        assertTrue(vista.tripulacion.mostrandoDetalle);
    }

    @Test
    @DisplayName("Creación: los rechazos del modelo se muestran en la vista y no cambian la nave")
    public void rechazosDeTripulacion() {
        crearNave(IVistaCrearNave.SELECCIONAR_COMBATE);
        CrearTripulacionDePrueba crear = vista.tripulacion.crear;

        crear.agregar("   ", Cargo.TENIENTE, Origen.TERRICOLA, "3");
        assertNotNull(crear.ultimoError, "Nombre vacío: lo rechaza el modelo.");
        assertTrue(crear.lista.isEmpty());

        crear.agregar("Sisko", Cargo.CAPITAN, Origen.TERRICOLA, "8");
        crear.ultimoError = null;
        crear.agregar("Picard", Cargo.CAPITAN, Origen.TERRICOLA, "9");
        assertEquals("La tripulación ya tiene un/a capitán/a.", crear.ultimoError);
        assertEquals(1, crear.lista.size());

        crear.ultimoError = null;
        crear.aceptar();
        assertNotNull(crear.ultimoError, "Tripulación incompleta: la rechaza el modelo al aceptar.");
        assertFalse(controlador.getAsistente().tieneTripulacion());
        assertFalse(vista.controlNaveHabilitado);
        assertNull(controlador.getControladorControl());
    }

    @Test
    @DisplayName("Tripulantes: la nómina muestra a cada tripulante con el sueldo de LiquidadorHaberes y su desglose")
    public void nominaConSueldosYDesglose() throws Exception {
        crearNave(IVistaCrearNave.SELECCIONAR_EXPLORADORA);
        vista.tripulacion.crear.armarTripulacionCompleta();
        DetallesTripulacionDePrueba nomina = vista.tripulacion.detalles;

        assertEquals(5, nomina.cantidad);
        assertEquals("James Kirk", nomina.capitan);
        assertEquals(5, nomina.filas.size());
        assertNull(nomina.ultimoError);

        LiquidadorHaberes liquidador = new LiquidadorHaberes();
        List<Tripulante> tripulantes = controlador.getAsistente().getTripulantes();
        double totalEsperado = 0;
        for (int i = 0; i < tripulantes.size(); i++) {
            Tripulante tripulante = tripulantes.get(i);
            ReciboHaberes esperado = liquidador.liquidar(tripulante, YearMonth.now());
            ReciboHaberes mostrado = nomina.recibos.get(i);

            assertTrue(nomina.filas.get(i).contains(tripulante.getNombre()));
            assertEquals(esperado.getTotal(), nomina.sueldos.get(i), 0.001, "Sueldo de " + tripulante.getNombre());
            assertSame(tripulante, mostrado.getTripulante());
            assertEquals(conceptos(esperado), conceptos(mostrado), "Desglose de " + tripulante.getNombre());
            assertTrue(mostrado.getConceptos().size() >= 3, "Cargo, antigüedad y origen como mínimo.");
            assertEquals(mostrado.getTotal(), suma(mostrado), 0.001, "El total es la suma de los conceptos.");
            totalEsperado += esperado.getTotal();
        }
        assertEquals(totalEsperado, nomina.costoNomina, 0.001);
    }

    @Test
    @DisplayName("Menú: Tripulación y Control de la Nave se rechazan hasta tener nave y tripulación")
    public void menuRespetaElOrdenDeCreacion() {
        ActionListener menu = vista.controlador;

        menu.actionPerformed(evento(IVistaPanelMenu.TRIPULACION));
        assertEquals("Primero debe crear una nave.", vista.ultimoError);

        crearNave(IVistaCrearNave.SELECCIONAR_EXPLORADORA);
        menu.actionPerformed(evento(IVistaPanelMenu.CONTROL_NAVE));
        assertEquals("Primero debe configurar una tripulación.", vista.ultimoError);
        assertNotEquals(IVistaPrincipal.PANEL_CONTROL_NAVE, vista.panelActual);

        vista.tripulacion.crear.armarTripulacionCompleta();
        menu.actionPerformed(evento(IVistaPanelMenu.CONTROL_NAVE));
        assertEquals(IVistaPrincipal.PANEL_CONTROL_NAVE, vista.panelActual);

        menu.actionPerformed(evento(IVistaPanelMenu.VISOR));
        assertEquals(IVistaPrincipal.PANEL_VISOR, vista.panelActual);
    }

    private void crearNave(String comandoTipo) {
        ActionListener crear = vista.nave.crear.controlador;
        crear.actionPerformed(evento(comandoTipo));
        crear.actionPerformed(evento(IVistaCrearNave.ACEPTAR));
    }

    private static List<String> conceptos(ReciboHaberes recibo) {
        List<String> descripcion = new ArrayList<>();
        for (ConceptoHaber concepto : recibo.getConceptos()) {
            descripcion.add(concepto.getTipo() + ":" + concepto.getDescripcion() + ":" + concepto.getImporte());
        }
        return descripcion;
    }

    private static double suma(ReciboHaberes recibo) {
        double total = 0;
        for (ConceptoHaber concepto : recibo.getConceptos()) {
            total += concepto.getImporte();
        }
        return total;
    }

    /** DAO en memoria, para no depender de la base en estas pruebas. */
    static class UsuarioDAOMemoria implements UsuarioDAO {
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
}
