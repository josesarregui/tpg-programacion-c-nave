
package modelo.usuario;

import excepcion.ContrasenaInvalidaException;
import excepcion.LoginInvalidoException;
import excepcion.UsuarioExistenteException;
import excepcion.UsuarioInvalidoException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ListaDeUsuarios {
    private List<Usuario> usuarios;

    
    public ListaDeUsuarios() {
        usuarios = new ArrayList<>();
    }

    
    private boolean existeUsuario(String nombreUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equals(nombreUsuario)) {
                return true;
            }
        }
        return false;
    }

    public void registrarUsuario(String nombre, String contrasena) throws UsuarioExistenteException, UsuarioInvalidoException, ContrasenaInvalidaException {
        if (nombre == null || nombre.isBlank()) {
            throw new UsuarioInvalidoException("El nombre de usuario no puede estar vacio.");
        }

        if (contrasena == null || contrasena.isBlank()) {
            throw new ContrasenaInvalidaException("La contraseña no puede estar vacia.");
        }

        if (this.existeUsuario(nombre)) {
            throw new UsuarioExistenteException("El usuario ya existe.");
        }

        usuarios.add(new Usuario(nombre, contrasena));
    }

    private Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equals(nombreUsuario)) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario validarLogin(String nombre, String contrasena) throws LoginInvalidoException {
        Usuario usuario = this.buscarUsuario(nombre);

        if (usuario == null) {
            throw new LoginInvalidoException("Usuario o contraseña incorrectos.");
        }
        if (!usuario.getContrasena().equals(contrasena)) {
            throw new LoginInvalidoException("Usuario o contraseña incorrectos.");
        }
        
        return usuario;
    }
}