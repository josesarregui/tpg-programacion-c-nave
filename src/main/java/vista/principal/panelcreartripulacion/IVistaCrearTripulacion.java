package vista.principal.panelcreartripulacion;

import java.awt.event.ActionListener;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Origen;

/**
 * Interfaz de la vista para el armado y asignación de la tripulación.
 */
public interface IVistaCrearTripulacion {
    String AGREGAR = "AGREGAR";
    String ACEPTAR = "ACEPTAR";

    String getNombre();

    Cargo getCargo();

    Origen getOrigen();

    String getTextoAntiguedad();

    void agregarTripulanteLista(String descripcion);

    void limpiarCampos();

    void mostrarError(String mensaje);

    void mostrarMensaje(String mensaje);

    void setControlador(ActionListener controlador);
}
