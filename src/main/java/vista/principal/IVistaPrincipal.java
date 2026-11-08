package vista.principal;

import java.awt.event.ActionListener;
import vista.concurrencia.IVistaConcurrencia;
import vista.principal.panelcontrol.IVistaControl;
import vista.principal.panelnave.IVistaNave;
import vista.principal.paneltripulacion.IVistaTripulacion;

/**
 * Interfaz de la ventana principal del sistema.
 */
public interface IVistaPrincipal {
    public String PANEL_NAVE = "PANEL_NAVE";
    public String PANEL_TRIPULACION = "PANEL_TRIPULACION";
    public String PANEL_CONTROL_NAVE = "PANEL_CONTROL_NAVE";
    public String PANEL_VISOR = "PANEL_VISOR";

    public IVistaNave getPanelNave();

    public IVistaTripulacion getPanelTripulacion();

    public IVistaControl getPanelControl();

    public IVistaConcurrencia getPanelVisor();

    public void mostrarOperador(String nombreUsuario);

    public void setControlador(ActionListener controlador);

    public void mostrarPanel(String nombrePanel);

    public void habilitarTripulacion(boolean habilitar);

    public void habilitarControlNave(boolean habilitar);

    public void mostrarError(String mensaje);

    public void arrancar();

    public void cerrar();
}
