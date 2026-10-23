package controlador.usuario;

import controlador.principal.ControladorPrincipal;
import excepcion.LoginInvalidoException;
import excepcion.PersistenciaException;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import vista.principal.VistaPrincipal;
import vista.usuario.IVistaLogin;
import vista.usuario.VistaRegistro;

/**
 * Controlador de la pantalla de login (patrón MVC, como el ControlConversor del apunte): recibe los comandos de
 * acción de la vista, invoca al servicio y coordina la respuesta. No contiene reglas de negocio ni SQL.
 *
 * Selección de vista: desde aquí se abre la pantalla de registro o la ventana principal tras iniciar sesión.
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
            abrirVentanaPrincipal(usuario);
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

    private void abrirVentanaPrincipal(Usuario usuario) {
        this.vista.cerrar();
        if (!GraphicsEnvironment.isHeadless()) {
            VistaPrincipal vistaPrincipal = new VistaPrincipal();
            ControladorPrincipal controlador = new ControladorPrincipal(vistaPrincipal, usuario, this.modelo);
            vistaPrincipal.arrancar();
        }
    }
}