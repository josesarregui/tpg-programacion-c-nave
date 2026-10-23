
package vista.principal.PanelCrearNave;

import controlador.crearNave.ControladorCrearNave;
import modelo.nave.TipoNave;

/**
 *
 */
public interface IVistaCrearNave {
    public String SELECCIONAR_EXPLORADORA = "SELECCIONAR_EXPLORADORA";
    public String SELECCIONAR_CARGUERA = "SELECCIONAR_CARGUERA";
    public String SELECCIONAR_COMBATE = "SELECCIONAR_COMBATE";
    public String ACEPTAR = "ACEPTAR";

    
    
    
    public TipoNave getTipoSeleccionado();

    public void setTipoSeleccionado(TipoNave tipo);

    public void setControlador(ControladorCrearNave controlador);

    public void mostrarError(String mensaje);

    public void mostrarMensaje(String mensaje);

    public void bloquearSeleccion();
}
