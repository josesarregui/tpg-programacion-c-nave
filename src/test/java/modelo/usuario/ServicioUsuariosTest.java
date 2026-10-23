package modelo.usuario;

import excepcion.ContrasenaInvalidaException;
import excepcion.LoginInvalidoException;
import excepcion.PersistenciaException;
import excepcion.UsuarioExistenteException;
import excepcion.UsuarioInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistencia.ConexionBD;
import persistencia.UsuarioDAOJDBC;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del control de acceso de la E2: reglas del registro y del inicio de sesión aplicadas por el
 * servicio, con excepciones propias, y credenciales que se conservan para futuras ejecuciones.
 * Usa el DAO real sobre una base H2 en memoria propia de cada prueba.
 */
public class ServicioUsuariosTest {

    private static int numeroDeBase = 0;

    private ConexionBD conexion;
    private ServicioUsuarios servicio;

    @BeforeEach
    public void inicializar() throws PersistenciaException {
        numeroDeBase++;
        conexion = new ConexionBD("jdbc:h2:mem:servicio" + numeroDeBase + ";DB_CLOSE_DELAY=-1");
        UsuarioDAOJDBC dao = new UsuarioDAOJDBC(conexion);
        dao.crearTablaSiNoExiste();
        servicio = new ServicioUsuarios(dao);
    }

    // --- Registro ---

    @Test
    @DisplayName("Registro: un usuario nuevo queda registrado y luego puede iniciar sesión")
    public void registrarEIniciarSesion() throws Exception {
        Usuario registrado = servicio.registrar("janeway", "voyager");
        assertEquals("janeway", registrado.getNombreUsuario());
        assertFalse(servicio.haySesionIniciada(), "Registrarse no inicia sesión.");

        Usuario enSesion = servicio.iniciarSesion("janeway", "voyager");

        assertEquals(registrado, enSesion);
        assertEquals(enSesion, servicio.getUsuarioEnSesion());
    }

    @Test
    @DisplayName("Registro: un nombre ya registrado se rechaza con UsuarioExistenteException y no se modifica")
    public void registrarRepetido() throws Exception {
        servicio.registrar("picard", "clave1");

        UsuarioExistenteException e = assertThrows(UsuarioExistenteException.class,
                () -> servicio.registrar("picard", "clave2"));
        assertEquals("picard", e.getNombreUsuario());
        assertNotNull(servicio.iniciarSesion("picard", "clave1"), "La contraseña original se conserva.");
    }

    @Test
    @DisplayName("Registro: nombre vacío, con espacios o de más de 30 caracteres se rechaza con UsuarioInvalidoException")
    public void registrarNombreInvalido() {
        assertThrows(UsuarioInvalidoException.class, () -> servicio.registrar("   ", "clave"));
        assertThrows(UsuarioInvalidoException.class, () -> servicio.registrar(null, "clave"));
        assertThrows(UsuarioInvalidoException.class, () -> servicio.registrar("jean luc", "clave"));
        UsuarioInvalidoException e = assertThrows(UsuarioInvalidoException.class,
                () -> servicio.registrar("a".repeat(31), "clave"));
        assertEquals("a".repeat(31), e.getNombreRecibido());
    }

    @Test
    @DisplayName("Registro: contraseña vacía o de más de 30 caracteres se rechaza con ContrasenaInvalidaException")
    public void registrarContrasenaInvalida() {
        assertThrows(ContrasenaInvalidaException.class, () -> servicio.registrar("data", ""));
        assertThrows(ContrasenaInvalidaException.class, () -> servicio.registrar("data", "   "));
        assertThrows(ContrasenaInvalidaException.class, () -> servicio.registrar("data", "x".repeat(31)));
        assertThrows(LoginInvalidoException.class, () -> servicio.iniciarSesion("data", "x"),
                "Un registro rechazado no guarda nada.");
    }

    @Test
    @DisplayName("Registro: nombre y contraseña de exactamente 30 caracteres se aceptan (límite de las columnas)")
    public void registrarEnElLimite() throws Exception {
        servicio.registrar("n".repeat(30), "c".repeat(30));
        assertNotNull(servicio.iniciarSesion("n".repeat(30), "c".repeat(30)));
    }

    // --- Inicio de sesión ---

    @Test
    @DisplayName("Login: contraseña incorrecta y usuario inexistente dan el mismo mensaje y no inician sesión")
    public void loginInvalido() throws Exception {
        servicio.registrar("worf", "klingon");

        LoginInvalidoException contrasenaMala = assertThrows(LoginInvalidoException.class,
                () -> servicio.iniciarSesion("worf", "romulano"));
        LoginInvalidoException usuarioInexistente = assertThrows(LoginInvalidoException.class,
                () -> servicio.iniciarSesion("riker", "klingon"));

        assertEquals(contrasenaMala.getMessage(), usuarioInexistente.getMessage());
        assertFalse(servicio.haySesionIniciada());
    }

    @Test
    @DisplayName("Login: la contraseña distingue mayúsculas y minúsculas")
    public void loginDistingueMayusculas() throws Exception {
        servicio.registrar("troi", "Betazed");
        assertThrows(LoginInvalidoException.class, () -> servicio.iniciarSesion("troi", "betazed"));
    }

    @Test
    @DisplayName("Login: datos vacíos o nulos se rechazan con LoginInvalidoException")
    public void loginDatosVacios() {
        assertThrows(LoginInvalidoException.class, () -> servicio.iniciarSesion("", "clave"));
        assertThrows(LoginInvalidoException.class, () -> servicio.iniciarSesion("kirk", ""));
        assertThrows(LoginInvalidoException.class, () -> servicio.iniciarSesion(null, null));
    }

    @Test
    @DisplayName("Login: un intento fallido no cierra la sesión vigente; cerrarSesion sí")
    public void sesion() throws Exception {
        servicio.registrar("sisko", "ds9");
        Usuario sisko = servicio.iniciarSesion("sisko", "ds9");

        assertThrows(LoginInvalidoException.class, () -> servicio.iniciarSesion("sisko", "otra"));
        assertEquals(sisko, servicio.getUsuarioEnSesion());

        servicio.cerrarSesion();
        assertNull(servicio.getUsuarioEnSesion());
    }

    // --- Persistencia entre ejecuciones ---

    @Test
    @DisplayName("Persistencia: un servicio nuevo sobre la misma base reconoce a los usuarios ya registrados")
    public void credencialesPersisten() throws Exception {
        servicio.registrar("archer", "nx01");

        ServicioUsuarios otraEjecucion = new ServicioUsuarios(new UsuarioDAOJDBC(conexion));

        assertEquals("archer", otraEjecucion.iniciarSesion("archer", "nx01").getNombreUsuario());
    }

    @Test
    @DisplayName("Persistencia: sin tabla, el servicio informa PersistenciaException en lugar de fallar")
    public void baseNoDisponible() {
        ServicioUsuarios sinTabla = new ServicioUsuarios(
                new UsuarioDAOJDBC(new ConexionBD("jdbc:h2:mem:servicioSinTabla;DB_CLOSE_DELAY=-1")));
        assertThrows(PersistenciaException.class, () -> sinTabla.iniciarSesion("kirk", "clave"));
        assertThrows(PersistenciaException.class, () -> sinTabla.registrar("kirk", "clave"));
    }
}
