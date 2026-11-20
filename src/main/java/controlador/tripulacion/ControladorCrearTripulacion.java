package controlador.tripulacion;

import excepcion.LiquidacionInvalidaException;
import excepcion.TripulacionInvalidaException;
import excepcion.TripulanteInvalidoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
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
import vista.principal.paneltripulacion.panelcreartripulacion.IVistaCrearTripulacion;

/**
 * Controlador para la creación y configuración de la tripulación.
 * Recibe los tipos enumerados directamente de la vista y delega las reglas de
 * negocio en el modelo.
 */
public class ControladorCrearTripulacion implements ActionListener {
    /**
     * Máximo de consejos iniciales que se registran al dar de alta un consejero. El alta corre en el hilo de eventos
     * y cada consejo verifica los contratos de {@link Consejero} recorriendo los ya registrados: sin un tope, una
     * cantidad con formato válido pero enorme congelaría la interfaz.
     */
    public static final int MAXIMO_CONSEJOS_INICIALES = 999;

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
            Alta alta = crearTripulanteDesdeVista();
            Tripulante tripulante = alta.tripulante();

            this.validarCapitanUnicoEnArmado(tripulante);

            this.tripulantesTemporales.add(tripulante);

            String descripcion = tripulante.getNombre()
                    + " — "
                    + tripulante.getCargo()
                    + " (" + tripulante.getOrigen() + ", "
                    + tripulante.getAntiguedad() + (tripulante.getAntiguedad() == 1 ? " año" : " años")
                    + alta.detalle() + ")";

            this.vistaCrearTripulacion.agregarTripulanteLista(descripcion);
            this.vistaCrearTripulacion.limpiarCampos();

        } catch (NumberFormatException e) {
            this.vistaCrearTripulacion.mostrarError("La antigüedad debe ser un número entero mayor o igual a 0.");
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

    /**
     * Tripulante creado desde la vista y el detalle propio de su cargo para la lista del armado (por ejemplo, los
     * consejos iniciales de un consejero). Se arma donde se decide qué clase crear, sin consultar el tipo después.
     */
    private record Alta(Tripulante tripulante, String detalle) {
    }

    /**
     * Crea el tripulante del cargo elegido en la vista (decisión de fábrica sobre el cargo, como {@code NaveFactory}
     * sobre el tipo de nave). Las reglas de nombre, antigüedad y origen las valida el modelo.
     */
    private Alta crearTripulanteDesdeVista() throws TripulanteInvalidoException {
        String nombre = this.vistaCrearTripulacion.getNombre();
        String textoAntiguedad = this.vistaCrearTripulacion.getTextoAntiguedad();

        int antiguedad = Integer.parseInt(textoAntiguedad != null ? textoAntiguedad.trim() : "");
        Cargo cargo = this.vistaCrearTripulacion.getCargo();
        Origen origen = this.vistaCrearTripulacion.getOrigen();

        if (cargo == null) {
            throw new TripulanteInvalidoException("Debe seleccionar un cargo.", nombre, antiguedad, origen);
        }

        // Las validaciones de nombre vacio, antigüedad negativa y origen nulo las
        // realiza Tripulante en el modelo.
        return switch (cargo) {
            case CAPITAN -> new Alta(new Capitan(nombre, antiguedad, origen), "");
            case CONSEJERO -> {
                Consejero consejero = new Consejero(nombre, antiguedad, origen);
                String textoConsejos = this.vistaCrearTripulacion.getTextoConsejos();
                int cantidadConsejos = 0;
                if (textoConsejos != null && !textoConsejos.isBlank()) {
                    try {
                        cantidadConsejos = Integer.parseInt(textoConsejos.trim());
                    } catch (NumberFormatException e) {
                        throw new TripulanteInvalidoException(
                                "La cantidad de consejos iniciales debe ser un número entero mayor o igual a 0.",
                                nombre, antiguedad, origen);
                    }
                    if (cantidadConsejos < 0) {
                        throw new TripulanteInvalidoException(
                                "La cantidad de consejos iniciales no puede ser negativa.",
                                nombre, antiguedad, origen);
                    }
                    if (cantidadConsejos > MAXIMO_CONSEJOS_INICIALES) {
                        throw new TripulanteInvalidoException(
                                "La cantidad de consejos iniciales no puede superar " + MAXIMO_CONSEJOS_INICIALES + ".",
                                nombre, antiguedad, origen);
                    }
                    YearMonth ahora = YearMonth.now();
                    for (int i = 0; i < cantidadConsejos; i++) {
                        try {
                            consejero.registrarConsejo(ahora);
                        } catch (LiquidacionInvalidaException e) {
                            throw new TripulanteInvalidoException(e.getMessage(), nombre, antiguedad, origen);
                        }
                    }
                }
                yield new Alta(consejero,
                        ", " + cantidadConsejos + (cantidadConsejos == 1 ? " consejo" : " consejos"));
            }
            case TENIENTE -> new Alta(new Teniente(nombre, antiguedad, origen), "");
            case ALFEREZ -> new Alta(new Alferez(nombre, antiguedad, origen), "");
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
            this.agregarTripulante();
        } else if (IVistaCrearTripulacion.ACEPTAR.equals(comando)) {
            this.aceptarTripulacion();
        }
    }
}
