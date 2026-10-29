package vista.principal;

import java.awt.event.ActionListener;
import vista.principal.panelnave.IVistaNave;
import vista.principal.paneltripulacion.IVistaTripulacion;

/**
 * Interfaz de la ventana principal del sistema.
 */
public interface IVistaPrincipal {
    String PANEL_NAVE = "PANEL_NAVE";
    String PANEL_TRIPULACION = "PANEL_TRIPULACION";
    String PANEL_CONTROL_NAVE = "PANEL_CONTROL_NAVE";

    IVistaNave getPanelNave();

    IVistaTripulacion getPanelTripulacion();

    void mostrarOperador(String nombreUsuario);

    void setControlador(ActionListener controlador);

    void mostrarPanel(String nombrePanel);

    void habilitarTripulacion(boolean habilitar);

    void habilitarControlNave(boolean habilitar);

    void mostrarError(String mensaje);

    void arrancar();

    void cerrar();
}
