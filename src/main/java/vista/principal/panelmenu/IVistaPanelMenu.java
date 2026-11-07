package vista.principal.panelmenu;

import java.awt.event.ActionListener;

/**
 * Interfaz del panel de menú lateral de la ventana principal.
 */
public interface IVistaPanelMenu {
    String NAVE = "NAVE";
    String TRIPULACION = "TRIPULACION";
    String CONTROL_NAVE = "CONTROL_NAVE";
    String VISOR = "VISOR";
    String CERRAR_SESION = "CERRAR_SESION";

    void setControlador(ActionListener controlador);

    void habilitarTripulacion(boolean habilitar);

    void habilitarControlNave(boolean habilitar);
}
