package controlador.usuario;

import controlador.VistasDePrueba.FabricaDePrueba;
import controlador.VistasDePrueba.LoginDePrueba;
import controlador.VistasDePrueba.PrincipalDePrueba;
import controlador.VistasDePrueba.RegistroDePrueba;
import excepcion.PersistenciaException;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistencia.ConexionBD;
import persistencia.UsuarioDAOJDBC;
import vista.principal.IVistaPrincipal;
import vista.usuario.IVistaLogin;
import vista.usuario.IVistaRegistro;

import static controlador.VistasDePrueba.evento;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de la separación MVC del acceso al sistema: los controladores sólo conocen a las vistas por sus
 * interfaces y las piden a la fábrica de vistas, por eso se prueban con vistas de prueba sin ventanas Swing (como la
 * InterfazTextualConversor del apunte, otra vista para el mismo controlador).
 */
public class ControladorLoginTest {

    private static int numeroDeBase = 0;

    private UsuarioDAOJDBC dao;
    private ServicioUsuarios servicio;
    private FabricaDePrueba fabrica;
    private CentroDeControl centro;

    @BeforeEach
    public void inicializar() throws Exception {
        numeroDeBase++;
        dao = new UsuarioDAOJDBC(
                new ConexionBD("jdbc:h2:mem:controlador" + numeroDeBase + ";DB_CLOSE_DELAY=-1"));
        dao.crearTablaSiNoExiste();
        servicio = new ServicioUsuarios(dao);
        servicio.registrar("kirk", "enterprise");
        fabrica = new FabricaDePrueba();
        centro = new CentroDeControl();
    }

    @Test
    @DisplayName("Login: con credenciales correctas el controlador inicia sesión y muestra la bienvenida")
    public void loginCorrecto() {
        LoginDePrueba vista = new LoginDePrueba("kirk", "enterprise");
        new ControladorLogin(vista, servicio, centro, fabrica);

        vista.controlador.actionPerformed(evento(IVistaLogin.INGRESAR));

        assertNull(vista.ultimoError);
        assertTrue(vista.ultimoMensaje.contains("kirk"));
        assertEquals("kirk", servicio.getUsuarioEnSesion().getNombreUsuario());
    }

    @Test
    @DisplayName("Transición: al autenticar, se cierra el login y se abre la ventana principal con el operador")
    public void loginAbreVentanaPrincipal() {
        LoginDePrueba vista = new LoginDePrueba("kirk", "enterprise");
        new ControladorLogin(vista, servicio, centro, fabrica);

        vista.controlador.actionPerformed(evento(IVistaLogin.INGRESAR));

        assertTrue(vista.cerrada, "El login se cierra.");
        assertEquals(1, fabrica.principales.size());
        PrincipalDePrueba principal = fabrica.ultimaPrincipal();
        assertTrue(principal.arrancada, "La ventana principal queda visible.");
        assertEquals("kirk", principal.operadorMostrado);
        assertNotNull(principal.controlador, "El menú tiene su controlador.");
        assertEquals(IVistaPrincipal.PANEL_NAVE, principal.panelActual);
        assertNotNull(principal.visor.getObservada(), "El Visor ya observa su plataforma.");
    }

    @Test
    @DisplayName("Login: con credenciales incorrectas el controlador muestra el error, vacía la contraseña y no abre nada")
    public void loginIncorrecto() {
        LoginDePrueba vista = new LoginDePrueba("kirk", "otra");
        new ControladorLogin(vista, servicio, centro, fabrica);

        vista.controlador.actionPerformed(evento(IVistaLogin.INGRESAR));

        assertEquals("Usuario o contraseña incorrectos.", vista.ultimoError);
        assertTrue(vista.contrasenaLimpiada);
        assertFalse(servicio.haySesionIniciada());
        assertFalse(vista.cerrada);
        assertTrue(fabrica.principales.isEmpty());
    }

    @Test
    @DisplayName("Selección de vista: desde el login se abre el registro y desde el registro se vuelve al login")
    public void navegacionEntreLoginYRegistro() {
        LoginDePrueba vista = new LoginDePrueba("", "");
        new ControladorLogin(vista, servicio, centro, fabrica);

        vista.controlador.actionPerformed(evento(IVistaLogin.IR_A_REGISTRO));

        assertTrue(vista.cerrada);
        assertEquals(1, fabrica.registros.size());
        RegistroDePrueba registro = fabrica.registros.get(0);
        assertTrue(registro.arrancada);

        registro.controlador.actionPerformed(evento(IVistaRegistro.VOLVER));

        assertTrue(registro.cerrada);
        assertEquals(1, fabrica.logins.size());
        assertTrue(fabrica.ultimoLogin().arrancada);
        assertNotNull(fabrica.ultimoLogin().controlador);
    }

    @Test
    @DisplayName("Registro: un nombre repetido se informa en la vista y la pantalla no se cierra")
    public void registroRepetido() {
        RegistroDePrueba vista = new RegistroDePrueba("kirk", "nueva");
        new ControladorRegistro(vista, servicio, centro, fabrica);

        vista.controlador.actionPerformed(evento(IVistaRegistro.REGISTRAR));

        assertEquals("El usuario \"kirk\" ya está registrado.", vista.ultimoError);
        assertFalse(vista.cerrada);
    }

    @Test
    @DisplayName("Registro: un nombre con espacios se informa en la vista sin guardar nada")
    public void registroNombreInvalido() throws PersistenciaException {
        RegistroDePrueba vista = new RegistroDePrueba("jean luc", "clave");
        new ControladorRegistro(vista, servicio, centro, fabrica);

        vista.controlador.actionPerformed(evento(IVistaRegistro.REGISTRAR));

        assertEquals("El nombre de usuario no puede contener espacios.", vista.ultimoError);
        assertFalse(vista.cerrada);
        assertNull(dao.buscarPorNombre("jean luc"));
    }

    @Test
    @DisplayName("Registro: un alta correcta vuelve al login")
    public void registroCorrectoVuelveAlLogin() throws PersistenciaException {
        RegistroDePrueba vista = new RegistroDePrueba("picard", "enterprise");
        new ControladorRegistro(vista, servicio, centro, fabrica);

        vista.controlador.actionPerformed(evento(IVistaRegistro.REGISTRAR));

        assertNull(vista.ultimoError);
        assertTrue(vista.cerrada);
        assertNotNull(dao.buscarPorNombre("picard"));
        assertTrue(fabrica.ultimoLogin().arrancada);
    }
}
