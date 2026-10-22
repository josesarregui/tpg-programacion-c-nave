
package controlador.principal;

import controlador.usuario.ControladorLogin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controlador.crearNave.ControladorCrearNave;
import controlador.tripulacion.ControladorTripulacion;
import modelo.nave.Nave;
import modelo.usuario.ListaDeUsuarios;
import modelo.usuario.Usuario;
import vista.principal.IVistaPrincipal;
import vista.principal.panelMenu.IVistaPanelMenu;
import vista.usuario.VistaLogin;

/**
 *
 */
public class ControladorPrincipal implements ActionListener{
    private IVistaPrincipal vista;
    
    private ListaDeUsuarios listUsuarios;
    private Usuario usuario;
    private Nave nave;
    
    /// Controladores de paneles
    private ControladorCrearNave controladorCrearNave;
    private ControladorTripulacion controladorTripulacion;

    
    public ControladorPrincipal(IVistaPrincipal vista,Usuario usuario,ListaDeUsuarios listUsuarios) {
        this.vista = vista;
        this.vista.setControlador(this);
        
        this.listUsuarios = listUsuarios;
        this.usuario = usuario;
        this.controladorCrearNave = new ControladorCrearNave(this.vista.getPanelCreacionNave(), this);
    }

    
    
    private void cerrarSesion() {
        this.vista.cerrar();
        ListaDeUsuarios usuarios = new ListaDeUsuarios();
        VistaLogin vista = new VistaLogin();

        ControladorLogin controlador = new ControladorLogin( usuarios, vista );
        vista.arrancar();
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