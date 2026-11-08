package controlador.tripulacion;

import modelo.asistente.AsistenteComando;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.principal.paneltripulacion.paneldetallestripulacion.IVistaDetallesTripulacion;
import modelo.liquidacion.ReciboHaberes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControladorDetallesTripulacionTest {

    private static class VistaDetallesMock implements IVistaDetallesTripulacion {
        int cantidad = -1;
        String capitan;
        double costoNomina = -1.0;
        final List<String> filas = new ArrayList<>();
        final List<ReciboHaberes> recibos = new ArrayList<>();

        @Override
        public void mostrarCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        @Override
        public void mostrarCapitan(String nombre) {
            this.capitan = nombre;
        }

        @Override
        public void mostrarCostoNomina(double totalNomina) {
            this.costoNomina = totalNomina;
        }

        @Override
        public void limpiarTripulantes() {
            filas.clear();
            recibos.clear();
        }

        @Override
        public void agregarTripulante(int id, String nombre, String cargo) {
            filas.add(id + ":" + nombre + ":" + cargo);
        }

        @Override
        public void agregarTripulante(int id, String nombre, String cargo, double sueldoTotal, ReciboHaberes recibo) {
            filas.add(id + ":" + nombre + ":" + cargo + ":" + sueldoTotal);
            if (recibo != null) {
                recibos.add(recibo);
            }
        }
    }

    @Test
    @DisplayName("actualizarVista sin tripulación reinicia indicadores y nómina")
    public void testActualizarVistaSinTripulacion() {
        Nave nave = new NaveFactory().crearNave(TipoNave.EXPLORADORA);
        AsistenteComando asistente = new AsistenteComando(nave);
        VistaDetallesMock vista = new VistaDetallesMock();

        ControladorDetallesTripulacion ctrl = new ControladorDetallesTripulacion(vista, asistente);
        ctrl.actualizarVista();

        assertEquals(0, vista.cantidad);
        assertNull(vista.capitan);
        assertEquals(0.0, vista.costoNomina);
        assertTrue(vista.filas.isEmpty());
    }

    @Test
    @DisplayName("actualizarVista con tripulación liquida haberes con Decorator y actualiza vista")
    public void testActualizarVistaConTripulacion() throws Exception {
        Nave nave = new NaveFactory().crearNave(TipoNave.EXPLORADORA);
        List<Tripulante> integrantes = List.of(
                new Capitan("James Kirk", 4, Origen.TERRICOLA),
                new Consejero("Spock", 6, Origen.VULCANO),
                new Teniente("Uhura", 3, Origen.TERRICOLA),
                new Alferez("Sulu", 1, Origen.MARCIANO),
                new Alferez("Chekov", 0, Origen.TERRICOLA)
        );
        nave.asignarTripulacion(new Tripulacion(integrantes));

        AsistenteComando asistente = new AsistenteComando(nave);
        VistaDetallesMock vista = new VistaDetallesMock();

        ControladorDetallesTripulacion ctrl = new ControladorDetallesTripulacion(vista, asistente);
        ctrl.actualizarVista();

        assertEquals(5, vista.cantidad);
        assertEquals("James Kirk", vista.capitan);
        assertEquals(5, vista.filas.size());
        assertEquals(5, vista.recibos.size());
        assertTrue(vista.costoNomina > 0.0);

        // Verificamos que los recibos contienen los conceptos aportados por los Decorators
        ReciboHaberes reciboKirk = vista.recibos.get(0);
        assertEquals("James Kirk", reciboKirk.getTripulante().getNombre());
        assertTrue(reciboKirk.getConceptos().size() >= 3, "Debe tener Cargo, Antiguedad y Origen");
        assertTrue(reciboKirk.getTotal() > 0);
    }
}
