package vista.principal.panelcrearnave;

import java.awt.event.ActionListener;
import modelo.nave.TipoNave;

/**
 * Interfaz de la vista de selección y creación de nave.
 */
public interface IVistaCrearNave {
    String SELECCIONAR_EXPLORADORA = "SELECCIONAR_EXPLORADORA";
    String SELECCIONAR_CARGUERA = "SELECCIONAR_CARGUERA";
    String SELECCIONAR_COMBATE = "SELECCIONAR_COMBATE";
    String ACEPTAR = "ACEPTAR";

    TipoNave getTipoSeleccionado();

    void setTipoSeleccionado(TipoNave tipo);

    void setControlador(ActionListener controlador);

    void mostrarError(String mensaje);
}
