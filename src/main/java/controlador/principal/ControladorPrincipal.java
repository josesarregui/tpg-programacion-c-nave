package controlador.principal;

import controlador.crearNave.ControladorCrearNave;
import controlador.tripulacion.ControladorTripulacion;
import controlador.usuario.ControladorLogin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.nave.Nave;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import vista.principal.IVistaPrincipal;
import vista.principal.panelMenu.IVistaPanelMenu;
import vista.usuario.IVistaLogin;
import vista.usuario.VistaLogin;

/**
 * Controlador de la ventana principal: gestiona la navegación entre los distintos paneles del sistema
 * y la sesión del usuario.
 */
public class ControladorPrincipal implements ActionListener {
    private IVistaPrincipal vista;
    
    private ServicioUsuarios servicioUsuarios;
    private Usuario usuario;
    private Nave nave;
    
    /// Controladores de paneles
    private ControladorCrearNave controladorCrearNave;
    private ControladorTripulacion controladorTripulacion;

    public ControladorPrincipal(IVistaPrincipal vista, Usuario usuario, ServicioUsuarios servicioUsuarios) {
        this.vista = vista;
        this.vista.setControlador(this);
        
        this.servicioUsuarios = servicioUsuarios;
        this.usuario = usuario;
        this.controladorCrearNave = new ControladorCrearNave(this.vista.getPanelCreacionNave(), this);
    }

    private void cerrarSesion() {
        this.vista.cerrar();
        if (this.servicioUsuarios != null) {
            this.servicioUsuarios.cerrarSesion();
        }
        IVistaLogin vistaLogin = new VistaLogin();
        ControladorLogin controlador = new ControladorLogin(vistaLogin, this.servicioUsuarios);
        vistaLogin.setControlador(controlador);
        vistaLogin.arrancar();
    }
    
    public void naveCreada() {
        if (this.controladorCrearNave != null) {
            this.nave = this.controladorCrearNave.getNave();
            if (this.usuario != null) {
                this.usuario.setNave(this.nave);
            }
        }
        this.vista.habilitarTripulacion(true);
    }

    public Nave getNave() {
        return this.nave != null ? this.nave : (this.usuario != null ? this.usuario.getNave() : null);
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public ControladorCrearNave getControladorCrearNave() {
        return this.controladorCrearNave;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (IVistaPanelMenu.CREAR_NAVE.equals(comando)) {
            vista.mostrarPanel(IVistaPrincipal.PANEL_CREAR_NAVE);

        } else if (IVistaPanelMenu.TRIPULACION.equals(comando)) {
            vista.mostrarPanel(IVistaPrincipal.PANEL_TRIPULACION);

        } else if (IVistaPanelMenu.TRIPULANTES.equals(comando)) {
            vista.mostrarPanel(IVistaPrincipal.PANEL_TRIPULANTES);

        } else if (IVistaPanelMenu.CONTROL_NAVE.equals(comando)) {
            vista.mostrarPanel(IVistaPrincipal.PANEL_CONTROL_NAVE);

        } else if (IVistaPanelMenu.VISOR.equals(comando)) {
            vista.mostrarPanel(IVistaPrincipal.PANEL_VISOR);

        } else if (IVistaPanelMenu.CERRAR_SESION.equals(comando)) {
            this.cerrarSesion();
        }
    }
}