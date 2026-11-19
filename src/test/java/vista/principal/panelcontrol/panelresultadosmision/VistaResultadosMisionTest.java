package vista.principal.panelcontrol.panelresultadosmision;

import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import modelo.asistente.AsistenteComando;
import modelo.mision.MisionRetorno;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.universo.DestinoSinMovimiento;
import modelo.universo.TableroDePrueba;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static vista.EdtDePrueba.conEdtBloqueado;

/**
 * Historial de informes: el informe de una misión ejecutada en otro hilo aparece solo, en el EDT.
 */
public class VistaResultadosMisionTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
    }

    @Test
    @DisplayName("Observer: el informe final llega desde el hilo de la misión y se muestra en el EDT")
    public void informeNuevoEnElEdt() throws Exception {
        AsistenteComando asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.CARGUERO));
        asistente.asignarTripulacion(new Tripulacion(List.of(
                new Capitan("Janeway", 10, Origen.TERRICOLA),
                new Consejero("Tuvok", 9, Origen.VULCANO),
                new Teniente("Paris", 4, Origen.TERRICOLA),
                new Alferez("Kim", 1, Origen.TERRICOLA),
                new Alferez("Torres", 3, Origen.MARCIANO))));
        TableroDePrueba tablero = new TableroDePrueba(20, 20);
        MisionRetorno mision = new MisionRetorno(tablero, new DestinoSinMovimiento(tablero));
        mision.configurarPausaPorTurno(0);
        asistente.encomendarMision(mision);

        VistaResultadosMision vista = new VistaResultadosMision();
        vista.observar(asistente);
        vista.mostrarInformes(List.of());

        AtomicReference<Exception> rechazo = new AtomicReference<>();
        conEdtBloqueado(
                () -> {
                    try {
                        asistente.ejecutarMision();
                    } catch (Exception e) {
                        rechazo.set(e);
                    }
                },
                () -> assertEquals(0, vista.getCantidadInformes(), "Con el EDT bloqueado la lista no cambió."));

        assertNull(rechazo.get(), "La misión de retorno no debía rechazarse.");

        assertEquals(1, vista.getCantidadInformes());
        assertTrue(vista.getTextoDetalle().contains("Resultado: EXITOSA"), vista.getTextoDetalle());
        assertTrue(vista.getTextoDetalle().contains(mision.getInforme().getMision()));
    }
}
