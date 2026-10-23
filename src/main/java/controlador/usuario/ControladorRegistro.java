package controlador.usuario;

import excepcion.ContrasenaInvalidaException;
import excepcion.PersistenciaException;
import excepcion.UsuarioExistenteException;
import excepcion.UsuarioInvalidoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import vista.usuario.IVistaRegistro;
import vista.usuario.VistaLogin;

/**
 * Controlador de la pantalla de registro (patrón MVC): recibe los comandos de acción de la vista, invoca al
 * servicio y coordina la respuesta. No contiene reglas de negocio ni SQL.
 *
 * Selección de vista: después de un registro exitoso, o con "Volver", regresa a la pantalla de login.
 */
public class ControladorRegistro implements ActionListener {

    private final IVistaRegistro vista;
    private final ServicioUsuarios modelo;

    public ControladorRegistro(IVistaRegistro vista, ServicioUsuarios modelo) {
        this.vista = vista;
        this.modelo = modelo;
    }

    /**
     * El comando de acción identifica el botón presionado.
     */
    @Override
    public void actionPerformed(ActionEvent evento) {
        if (IVistaRegistro.REGISTRAR.equals(evento.getActionCommand())) {
            registrar();
        } else if (IVistaRegistro.VOLVER.equals(evento.getActionCommand())) {
            volverAlLogin();
        }
    }

    private void registrar() {
        try {
            Usuario usuario = modelo.registrar(vista.getNombreUsuario(), vista.getContrasena());
            vista.mostrarMensaje("Usuario " + usuario.getNombreUsuario() + " registrado correctamente. "
                    + "Ya puede iniciar sesión.");
            volverAlLogin();
        } catch (UsuarioInvalidoException | ContrasenaInvalidaException | UsuarioExistenteException
                 | PersistenciaException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void volverAlLogin() {
        VistaLogin vistaLogin = new VistaLogin();
        vistaLogin.setControlador(new ControladorLogin(vistaLogin, modelo));
        vista.cerrar();
        vistaLogin.arrancar();
    }
}
