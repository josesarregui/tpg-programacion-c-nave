package vista.principal.panelcreartripulacion;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Clase oyente de los campos de alta de tripulante: valida el formato en la vista
 * (nombre no vacío y antigüedad compuesta por dígitos enteros) para habilitar el botón "Agregar".
 */
class OyenteCamposTripulacion extends KeyAdapter {

    private final VistaCrearTripulacion vista;

    OyenteCamposTripulacion(VistaCrearTripulacion vista) {
        this.vista = vista;
    }

    @Override
    public void keyReleased(KeyEvent evento) {
        verificarCampos();
    }

    void verificarCampos() {
        String nombre = vista.getCampoNombre().getText().trim();
        String antiguedad = vista.getCampoAntiguedad().getText().trim();
        boolean formatoValido = !nombre.isEmpty() && antiguedad.matches("\\d+");
        vista.getBotonAgregar().setEnabled(formatoValido);
    }
}

