
package vista.principal;

import controlador.principal.ControladorPrincipal;
import vista.principal.PanelCrearNave.VistaCrearNave;
import vista.principal.panelMenu.VistaPanelMenu;

/**
 *
 */
public interface IVistaPrincipal {
    public String PANEL_INICIO = "INICIO";
    public String PANEL_CREAR_NAVE = "CREAR_NAVE";
    public String PANEL_TRIPULACION = "TRIPULACION";
    public String PANEL_TRIPULANTES = "TRIPULANTES";
    public String PANEL_CONTROL_NAVE = "CONTROL_NAVE";
    public String PANEL_VISOR = "VISOR";

    
    public VistaCrearNave getPanelCreacionNave();
    
    public VistaPanelMenu getPanelMenu();
    
    public void habilitarTripulacion(boolean habilitar);
    
    public void setControlador(ControladorPrincipal controlador);

    public void mostrarPanel(String nombrePanel);

    public void arrancar();

    public void cerrar();
}