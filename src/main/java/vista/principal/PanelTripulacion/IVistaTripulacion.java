
package vista.principal.PanelTripulacion;

import controlador.tripulacion.ControladorTripulacion;


/**
 *
 */
public interface IVistaTripulacion {
    
    
    
    
    
    public void setControlador(ControladorTripulacion controlador);

    public void mostrarError(String mensaje);

    public void mostrarMensaje(String mensaje);

    public void bloquearSeleccion();
}
