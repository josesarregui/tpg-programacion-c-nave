package vista.principal.panelnave;

import vista.principal.panelnave.panelcrearnave.IVistaCrearNave;
import vista.principal.panelnave.paneldetallesnave.IVistaDetallesNave;

/**
 * Interfaz contenedora de la sección de Nave (creación y detalles).
 */
public interface IVistaNave {
    String CREAR_NAVE = "CREAR_NAVE";
    String DETALLE_NAVE = "DETALLE_NAVE";

    void mostrarCrearNave();

    void mostrarDetalleNave();

    IVistaCrearNave getVistaCrearNave();

    IVistaDetallesNave getVistaDetallesNave();
}
