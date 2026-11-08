package controlador.tripulacion;

import excepcion.LiquidacionInvalidaException;
import modelo.asistente.Asistente;
import modelo.liquidacion.LiquidadorHaberes;
import modelo.liquidacion.ReciboHaberes;
import modelo.tripulacion.Tripulante;
import vista.principal.paneltripulacion.paneldetallestripulacion.IVistaDetallesTripulacion;

import java.time.YearMonth;

/**
 * Controlador del panel de detalles de la tripulación.
 * Trabaja contra la interfaz {@link IVistaDetallesTripulacion} y {@link Asistente}.
 */
public class ControladorDetallesTripulacion {
    private final IVistaDetallesTripulacion vista;
    private final Asistente asistente;
    private final LiquidadorHaberes liquidador;

    public ControladorDetallesTripulacion(IVistaDetallesTripulacion vistaDetalles, Asistente asistente) {
        this(vistaDetalles, asistente, new LiquidadorHaberes());
    }

    public ControladorDetallesTripulacion(IVistaDetallesTripulacion vistaDetalles, Asistente asistente, LiquidadorHaberes liquidador) {
        this.vista = vistaDetalles;
        this.asistente = asistente;
        this.liquidador = liquidador != null ? liquidador : new LiquidadorHaberes();
    }

    public void actualizarVista() {
        if (!this.asistente.tieneTripulacion()) {
            this.vista.mostrarCantidad(0);
            this.vista.mostrarCapitan(null);
            this.vista.mostrarCostoNomina(0.0);
            this.vista.limpiarTripulantes();
            return;
        }

        this.vista.mostrarCantidad(this.asistente.getCantidadTripulantes());
        Tripulante capitan = this.asistente.getCapitan();
        this.vista.mostrarCapitan(capitan != null ? capitan.getNombre() : null);
        this.vista.limpiarTripulantes();

        YearMonth periodoActual = YearMonth.now();
        double totalNomina = 0.0;

        for (Tripulante tripulante : this.asistente.getTripulantes()) {
            try {
                ReciboHaberes recibo = this.liquidador.liquidar(tripulante, periodoActual);
                totalNomina += recibo.getTotal();
                this.vista.agregarTripulante(
                        tripulante.getId(),
                        tripulante.getNombre(),
                        tripulante.getCargo().toString(),
                        recibo.getTotal(),
                        recibo
                );
            } catch (LiquidacionInvalidaException e) {
                this.vista.agregarTripulante(
                        tripulante.getId(),
                        tripulante.getNombre(),
                        tripulante.getCargo().toString()
                );
            }
        }

        this.vista.mostrarCostoNomina(totalNomina);
    }
}

