package controlador.usuario;

import excepcion.LoginInvalidoException;
import excepcion.PersistenciaException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import vista.usuario.IVistaLogin;
import vista.usuario.VistaRegistro;

/**
 * Controlador de la pantalla de login (patrón MVC, como el ControlConversor del apunte): recibe los comandos de
 * acción de la vista, invoca al servicio y coordina la respuesta. No contiene reglas de negocio ni SQL.
 *
 * Selección de vista: desde aquí se abre la pantalla de registro.
 */
public class ControladorLogin implements ActionListener {

    private final IVistaLogin vista;
    private final ServicioUsuarios modelo;

    public ControladorLogin(IVistaLogin vista, ServicioUsuarios modelo) {
        this.vista = vista;
        this.modelo = modelo;
    }

    /**
     * El comando de acción identifica el botón presionado.
     */
    @Override
    public void actionPerformed(ActionEvent evento) {
        if (IVistaLogin.INGRESAR.equals(evento.getActionCommand())) {
            iniciarSesion();
        } else if (IVistaLogin.IR_A_REGISTRO.equals(evento.getActionCommand())) {
            abrirRegistro();
        }
    }

    private void iniciarSesion() {
        try {
            Usuario usuario = modelo.iniciarSesion(vista.getNombreUsuario(), vista.getContrasena());
            vista.mostrarMensaje("Acceso concedido. Bienvenido/a, " + usuario.getNombreUsuario() + ".");
            // Una vez validada la identidad, aquí se abrirá el panel central cuando su vista esté integrada.
        } catch (LoginInvalidoException e) {
            vista.mostrarError(e.getMessage());
            vista.limpiarContrasena();
        } catch (PersistenciaException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void abrirRegistro() {
        VistaRegistro vistaRegistro = new VistaRegistro();
        vistaRegistro.setControlador(new ControladorRegistro(vistaRegistro, modelo));
        vista.cerrar();
        vistaRegistro.arrancar();
    }
}
