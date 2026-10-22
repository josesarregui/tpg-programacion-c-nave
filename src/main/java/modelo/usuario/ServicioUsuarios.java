package modelo.usuario;

import excepcion.ContrasenaInvalidaException;
import excepcion.LoginInvalidoException;
import excepcion.PersistenciaException;
import excepcion.UsuarioExistenteException;
import excepcion.UsuarioInvalidoException;
import persistencia.UsuarioDAO;
import persistencia.UsuarioDTO;

/**
 * Servicio de acceso al sistema: aplica las reglas del registro y del inicio de sesión, y recuerda qué operador
 * inició sesión (Enunciado E2, "Persistencia": identificar quién utiliza el sistema).
 *
 * Reparto de responsabilidades (Guía TP Nave, Entrega 2):
 *  - la Vista valida el formato (campos completos, contraseñas repetidas iguales);
 *  - el Controlador coordina: toma los datos de la Vista, invoca este servicio y muestra el resultado;
 *  - este servicio aplica las reglas de negocio y las informa con excepciones propias;
 *  - el DAO hace las consultas a la base de datos.
 *
 * El servicio no confía en que la Vista haya validado: los datos llegan desde fuera del modelo, por eso los
 * rechaza con excepciones (mismo criterio que la Entrega 1 con los datos externos).
 *
 * Depende de la interfaz {@link UsuarioDAO}, que recibe por constructor: no sabe que los datos están en H2.
 */
public class ServicioUsuarios {

    /** Longitud máxima del nombre y de la contraseña: la de las columnas VARCHAR(30) de la tabla usuario. */
    public static final int LONGITUD_MAXIMA = 30;

    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioEnSesion;

    /**
     * @pre usuarioDAO != null.
     * @post No hay una sesión iniciada.
     */
    public ServicioUsuarios(UsuarioDAO usuarioDAO) {
        assert usuarioDAO != null : "El servicio necesita un DAO de usuarios.";
        this.usuarioDAO = usuarioDAO;
        this.usuarioEnSesion = null;
    }

    /**
     * Da de alta un usuario nuevo y lo guarda para futuras ejecuciones.
     *
     * @post Las credenciales quedan guardadas; la sesión no cambia (el usuario debe iniciar sesión después).
     * @return el usuario registrado.
     * @throws UsuarioInvalidoException si el nombre es vacío, tiene espacios o supera {@link #LONGITUD_MAXIMA}.
     * @throws ContrasenaInvalidaException si la contraseña es vacía o supera {@link #LONGITUD_MAXIMA}.
     * @throws UsuarioExistenteException si ya hay un usuario con ese nombre; no se guarda nada.
     * @throws PersistenciaException si no se pudo acceder a la base de datos.
     */
    public Usuario registrar(String nombreUsuario, String contrasena)
            throws UsuarioInvalidoException, ContrasenaInvalidaException, UsuarioExistenteException,
                   PersistenciaException {
        validarNombre(nombreUsuario);
        validarContrasena(contrasena);
        if (usuarioDAO.buscarPorNombre(nombreUsuario) != null) {
            throw new UsuarioExistenteException(nombreUsuario);
        }
        usuarioDAO.insertar(new UsuarioDTO(nombreUsuario, contrasena));
        return new Usuario(nombreUsuario);
    }

    /**
     * Valida las credenciales y, si son correctas, deja al usuario como operador en sesión.
     *
     * @post getUsuarioEnSesion() es el usuario que inició sesión.
     * @return el usuario que inició sesión.
     * @throws LoginInvalidoException si falta algún dato, el usuario no existe o la contraseña no coincide;
     *                                la sesión anterior no cambia.
     * @throws PersistenciaException si no se pudo acceder a la base de datos.
     */
    public Usuario iniciarSesion(String nombreUsuario, String contrasena)
            throws LoginInvalidoException, PersistenciaException {
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new LoginInvalidoException("Debe ingresar el nombre de usuario y la contraseña.");
        }
        UsuarioDTO guardado = usuarioDAO.buscarPorNombre(nombreUsuario);
        if (guardado == null || !guardado.getContrasena().equals(contrasena)) {
            throw new LoginInvalidoException("Usuario o contraseña incorrectos.");
        }
        usuarioEnSesion = new Usuario(guardado.getNombreUsuario());
        assert usuarioEnSesion.getNombreUsuario().equals(nombreUsuario) : "Fallo postcondición al iniciar sesión.";
        return usuarioEnSesion;
    }

    /**
     * @post No hay una sesión iniciada.
     */
    public void cerrarSesion() {
        usuarioEnSesion = null;
    }

    /**
     * @return el operador que inició sesión, o null si no hay una sesión iniciada.
     */
    public Usuario getUsuarioEnSesion() {
        return usuarioEnSesion;
    }

    public boolean haySesionIniciada() {
        return usuarioEnSesion != null;
    }

    private void validarNombre(String nombreUsuario) throws UsuarioInvalidoException {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new UsuarioInvalidoException("El nombre de usuario no puede estar vacío.", nombreUsuario);
        }
        if (nombreUsuario.contains(" ")) {
            throw new UsuarioInvalidoException("El nombre de usuario no puede contener espacios.", nombreUsuario);
        }
        if (nombreUsuario.length() > LONGITUD_MAXIMA) {
            throw new UsuarioInvalidoException("El nombre de usuario no puede superar los " + LONGITUD_MAXIMA
                    + " caracteres.", nombreUsuario);
        }
    }

    private void validarContrasena(String contrasena) throws ContrasenaInvalidaException {
        if (contrasena == null || contrasena.isBlank()) {
            throw new ContrasenaInvalidaException("La contraseña no puede estar vacía.");
        }
        if (contrasena.length() > LONGITUD_MAXIMA) {
            throw new ContrasenaInvalidaException("La contraseña no puede superar los " + LONGITUD_MAXIMA
                    + " caracteres.");
        }
    }
}
