package controlador.tripulacion;

import excepcion.TripulacionInvalidaException;
import excepcion.TripulanteInvalidoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import modelo.asistente.Asistente;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import vista.principal.panelcreartripulacion.IVistaCrearTripulacion;

/**
 * Controlador para la creación y configuración de la tripulación.
 * Recibe los tipos enumerados directamente de la vista y delega las reglas de negocio en el modelo.
 */
public class ControladorCrearTripulacion implements ActionListener {
    private final IVistaCrearTripulacion vistaCrearTripulacion;
    private final Asistente asistente;
    private final ControladorTripulacion controladorTripulacion;
    private final List<Tripulante> tripulantesTemporales;

    public ControladorCrearTripulacion(
            IVistaCrearTripulacion vistaCrearTripulacion,
            Asistente asistente,
            ControladorTripulacion controladorTripulacion) {
        this.vistaCrearTripulacion = vistaCrearTripulacion;
        this.asistente = asistente;
        this.controladorTripulacion = controladorTripulacion;

        this.tripulantesTemporales = new ArrayList<>();
        this.vistaCrearTripulacion.setControlador(this);
    }

    private void agregarTripulante() {
        try {
            Tripulante tripulante = crearTripulanteDesdeVista();

            validarCapitanUnicoEnArmado(tripulante);

            this.tripulantesTemporales.add(tripulante);

            this.vistaCrearTripulacion.agregarTripulanteLista(
                    tripulante.getNombre()
                    + " — "
                    + tripulante.getCargo()
                    + " (" + tripulante.getOrigen() + ", "
                    + tripulante.getAntiguedad() + (tripulante.getAntiguedad() == 1 ? " año)" : " años)")
            );

            this.vistaCrearTripulacion.limpiarCampos();

        } catch (NumberFormatException e) {
            this.vistaCrearTripulacion.mostrarError(
                    "La antigüedad debe ser un número entero mayor o igual a 0."
            );
        } catch (TripulanteInvalidoException | TripulacionInvalidaException e) {
            this.vistaCrearTripulacion.mostrarError(e.getMessage());
        }
    }

    private void aceptarTripulacion() {
        try {
            Tripulacion tripulacion = new Tripulacion(this.tripulantesTemporales);

            this.asistente.asignarTripulacion(tripulacion);

            if (this.controladorTripulacion != null) {
                this.controladorTripulacion.tripulacionAsignada();
            }

        } catch (TripulacionInvalidaException e) {
            this.vistaCrearTripulacion.mostrarError(e.getMessage());
        }
    }

    private Tripulante crearTripulanteDesdeVista() throws TripulanteInvalidoException {
        String nombre = this.vistaCrearTripulacion.getNombre();
        String textoAntiguedad = this.vistaCrearTripulacion.getTextoAntiguedad();

        int antiguedad = Integer.parseInt(textoAntiguedad != null ? textoAntiguedad.trim() : "");
        Cargo cargo = this.vistaCrearTripulacion.getCargo();
        Origen origen = this.vistaCrearTripulacion.getOrigen();

        if (cargo == null) {
            throw new TripulanteInvalidoException("Debe seleccionar un cargo.", nombre, antiguedad, origen);
        }

        // Las validaciones de nombre vacío, antigüedad negativa y origen nulo las realiza Tripulante en el modelo.
        return switch (cargo) {
            case CAPITAN -> new Capitan(nombre, antiguedad, origen);
            case CONSEJERO -> new Consejero(nombre, antiguedad, origen);
            case TENIENTE -> new Teniente(nombre, antiguedad, origen);
            case ALFEREZ -> new Alferez(nombre, antiguedad, origen);
        };
    }

    private void validarCapitanUnicoEnArmado(Tripulante nuevo) throws TripulacionInvalidaException {
        if (nuevo.getCargo() == Cargo.CAPITAN) {
            for (Tripulante tripulante : this.tripulantesTemporales) {
                if (tripulante.getCargo() == Cargo.CAPITAN) {
                    throw new TripulacionInvalidaException("La tripulación ya tiene un/a capitán/a.", nuevo);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (IVistaCrearTripulacion.AGREGAR.equals(comando)) {
            agregarTripulante();
        } else if (IVistaCrearTripulacion.ACEPTAR.equals(comando)) {
            aceptarTripulacion();
        }
    }
}
