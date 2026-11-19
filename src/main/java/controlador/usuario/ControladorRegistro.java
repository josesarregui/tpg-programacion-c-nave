package controlador.usuario;

import excepcion.ContrasenaInvalidaException;
import excepcion.PersistenciaException;
import excepcion.UsuarioExistenteException;
import excepcion.UsuarioInvalidoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import vista.FabricaDeVistas;
import vista.usuario.IVistaLogin;
import vista.usuario.IVistaRegistro;

/**
 * Controlador de la pantalla de registro (patrón MVC): recibe los comandos de acción de la vista, invoca al
 * servicio y coordina la respuesta. No contiene reglas de negocio ni SQL.
 *
 * Selección de vista: después de un registro exitoso, o con "Volver", regresa a la pantalla de login. La pide a la
 * {@link FabricaDeVistas}, así que sólo conoce las vistas por sus interfaces.
 */
public class ControladorRegistro implements ActionListener {

    private final IVistaRegistro vista;
    private final ServicioUsuarios modelo;
    private final CentroDeControl centroDeControl;
    private final FabricaDeVistas fabricaDeVistas;

    /**
     * @pre Ningún parámetro es nulo.
     * @post La vista tiene a este controlador como oyente de sus botones.
     */
    public ControladorRegistro(IVistaRegistro vista, ServicioUsuarios modelo, CentroDeControl centroDeControl,
            FabricaDeVistas fabricaDeVistas) {
        assert vista != null && modelo != null && centroDeControl != null && fabricaDeVistas != null
                : "El controlador de registro necesita la vista, el servicio, el centro de control y la fábrica de vistas.";
        this.vista = vista;
        this.modelo = modelo;
        this.centroDeControl = centroDeControl;
        this.fabricaDeVistas = fabricaDeVistas;
        this.vista.setControlador(this);
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
        IVistaLogin vistaLogin = fabricaDeVistas.crearVistaLogin();
        new ControladorLogin(vistaLogin, modelo, centroDeControl, fabricaDeVistas);
        vista.cerrar();
        vistaLogin.arrancar();
    }
}
