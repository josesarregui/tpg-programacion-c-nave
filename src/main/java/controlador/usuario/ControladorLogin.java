package controlador.usuario;

import controlador.principal.ControladorPrincipal;
import excepcion.LoginInvalidoException;
import excepcion.PersistenciaException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import vista.FabricaDeVistas;
import vista.principal.IVistaPrincipal;
import vista.usuario.IVistaLogin;
import vista.usuario.IVistaRegistro;

/**
 * Controlador de la pantalla de login (patrón MVC, como el ControlConversor del apunte): recibe los comandos de
 * acción de la vista, invoca al servicio y coordina la respuesta. No contiene reglas de negocio ni SQL.
 *
 * Selección de vista: desde aquí se abre la pantalla de registro o, al autenticar con éxito en
 * {@link ServicioUsuarios}, se cierra el login y se abre la ventana principal con las secciones operativas. Las
 * ventanas las crea la {@link FabricaDeVistas}: el controlador sólo conoce sus interfaces.
 */
public class ControladorLogin implements ActionListener {
    private final IVistaLogin vista;
    private final ServicioUsuarios modelo;
    private final CentroDeControl centroDeControl;
    private final FabricaDeVistas fabricaDeVistas;

    /**
     * @pre Ningún parámetro es nulo.
     * @post La vista tiene a este controlador como oyente de sus botones.
     */
    public ControladorLogin(IVistaLogin vista, ServicioUsuarios modelo, CentroDeControl centroDeControl,
            FabricaDeVistas fabricaDeVistas) {
        assert vista != null && modelo != null && centroDeControl != null && fabricaDeVistas != null
                : "El controlador de login necesita la vista, el servicio, el centro de control y la fábrica de vistas.";
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
        IVistaRegistro vistaRegistro = fabricaDeVistas.crearVistaRegistro();
        new ControladorRegistro(vistaRegistro, modelo, centroDeControl, fabricaDeVistas);
        vista.cerrar();
        vistaRegistro.arrancar();
    }

    /**
     * @pre usuario es el usuario en sesión.
     * @post El login quedó cerrado y la ventana principal, con su controlador, quedó visible.
     */
    private void abrirVentanaPrincipal(Usuario usuario) {
        vista.cerrar();
        IVistaPrincipal vistaPrincipal = fabricaDeVistas.crearVistaPrincipal();
        new ControladorPrincipal(vistaPrincipal, usuario, modelo, centroDeControl, fabricaDeVistas);
        vistaPrincipal.arrancar();
    }
}
