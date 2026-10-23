package controlador.usuario;

import excepcion.PersistenciaException;
import modelo.usuario.ServicioUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistencia.ConexionBD;
import persistencia.UsuarioDAOJDBC;
import vista.usuario.IVistaLogin;
import vista.usuario.IVistaRegistro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de la separación MVC del acceso al sistema: los controladores sólo conocen a la vista por su interfaz,
 * por eso se prueban con vistas de prueba sin ventanas Swing (como la InterfazTextualConversor del apunte,
 * otra vista para el mismo controlador).
 */
public class ControladorLoginTest {

    private static int numeroDeBase = 0;

    private UsuarioDAOJDBC dao;
    private ServicioUsuarios servicio;

    @BeforeEach
    public void inicializar() throws Exception {
        numeroDeBase++;
        dao = new UsuarioDAOJDBC(
                new ConexionBD("jdbc:h2:mem:controlador" + numeroDeBase + ";DB_CLOSE_DELAY=-1"));
        dao.crearTablaSiNoExiste();
        servicio = new ServicioUsuarios(dao);
        servicio.registrar("kirk", "enterprise");
    }

    @Test
    @DisplayName("Login: con credenciales correctas el controlador inicia sesión y muestra la bienvenida")
    public void loginCorrecto() {
        VistaLoginDePrueba vista = new VistaLoginDePrueba("kirk", "enterprise");
        ControladorLogin controlador = new ControladorLogin(vista, servicio);

        controlador.actionPerformed(evento(IVistaLogin.INGRESAR));

        assertNull(vista.ultimoError);
        assertTrue(vista.ultimoMensaje.contains("kirk"));
        assertEquals("kirk", servicio.getUsuarioEnSesion().getNombreUsuario());
    }

    @Test
    @DisplayName("Login: con credenciales incorrectas el controlador muestra el error y vacía la contraseña")
    public void loginIncorrecto() {
        VistaLoginDePrueba vista = new VistaLoginDePrueba("kirk", "otra");
        ControladorLogin controlador = new ControladorLogin(vista, servicio);

        controlador.actionPerformed(evento(IVistaLogin.INGRESAR));

        assertEquals("Usuario o contraseña incorrectos.", vista.ultimoError);
        assertTrue(vista.contrasenaLimpiada);
        assertFalse(servicio.haySesionIniciada());
    }

    @Test
    @DisplayName("Registro: un nombre repetido se informa en la vista y la pantalla no se cierra")
    public void registroRepetido() {
        VistaRegistroDePrueba vista = new VistaRegistroDePrueba("kirk", "nueva");
        ControladorRegistro controlador = new ControladorRegistro(vista, servicio);

        controlador.actionPerformed(evento(IVistaRegistro.REGISTRAR));

        assertEquals("El usuario \"kirk\" ya está registrado.", vista.ultimoError);
        assertFalse(vista.cerrada);
    }

    @Test
    @DisplayName("Registro: un nombre con espacios se informa en la vista sin guardar nada")
    public void registroNombreInvalido() throws PersistenciaException {
        VistaRegistroDePrueba vista = new VistaRegistroDePrueba("jean luc", "clave");
        ControladorRegistro controlador = new ControladorRegistro(vista, servicio);

        controlador.actionPerformed(evento(IVistaRegistro.REGISTRAR));

        assertEquals("El nombre de usuario no puede contener espacios.", vista.ultimoError);
        assertFalse(vista.cerrada);
        assertNull(dao.buscarPorNombre("jean luc"));
    }

    private static ActionEvent evento(String comando) {
        return new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, comando);
    }

    // --- Vistas de prueba: implementan las interfaces de vista sin crear ventanas ---

    private static class VistaDePrueba {
        private final String nombre;
        private final String contrasena;
        String ultimoError;
        String ultimoMensaje;
        boolean cerrada;

        VistaDePrueba(String nombre, String contrasena) {
            this.nombre = nombre;
            this.contrasena = contrasena;
        }

        public void setControlador(ActionListener controlador) {
        }

        public void arrancar() {
        }

        public void cerrar() {
            cerrada = true;
        }

        public String getNombreUsuario() {
            return nombre;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }

        public void mostrarMensaje(String mensaje) {
            ultimoMensaje = mensaje;
        }

        public void limpiarCampos() {
        }
    }

    private static class VistaLoginDePrueba extends VistaDePrueba implements IVistaLogin {
        boolean contrasenaLimpiada;

        VistaLoginDePrueba(String nombre, String contrasena) {
            super(nombre, contrasena);
        }

        @Override
        public void limpiarContrasena() {
            contrasenaLimpiada = true;
        }
    }

    private static class VistaRegistroDePrueba extends VistaDePrueba implements IVistaRegistro {
        VistaRegistroDePrueba(String nombre, String contrasena) {
            super(nombre, contrasena);
        }
    }
}
