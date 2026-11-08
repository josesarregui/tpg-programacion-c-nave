package vista.principal.panelselectmision;

import java.awt.event.ActionListener;
import java.util.List;
import modelo.bitacora.Evento;
import modelo.mision.Mision;

/**
 * Interfaz de la vista del panel de Selección de Misión y consulta de Bitácora.
 */
public interface IVistaSelectMision {
    public String COMANDO_INICIAR_MISION = "INICIAR_MISION";
    public String COMANDO_SELECCIONAR_MISION = "SELECCIONAR_MISION";
    public String COMANDO_ACTUALIZAR_BITACORA = "ACTUALIZAR_BITACORA";

    public void setControlador(ActionListener controlador);

    public void mostrarBitacora(List<Evento> eventos);

    public void mostrarMisionesDisponibles(List<Mision> misiones);

    public String getMisionSeleccionada();

    public void mostrarDetallesMision(Mision mision, boolean naveListaParaOperar, int combustibleActual,
            int energiaActual);

    public void habilitarIniciarMision(boolean habilitar);

    public void mostrarError(String mensaje);

    public void mostrarMensaje(String mensaje);
}
