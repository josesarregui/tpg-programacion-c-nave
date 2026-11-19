package vista.principal.panelcontrol.panelrecursos;

import java.awt.GraphicsEnvironment;
import modelo.asistente.AsistenteComando;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static vista.EdtDePrueba.conEdtBloqueado;

/**
 * Franja de recursos: consulta al asistente (pull) en el hilo que notifica y modifica las barras en el EDT.
 */
public class VistaRecursosTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
    }

    @Test
    @DisplayName("Observer: un impacto desde otro hilo actualiza la energía y el estado en el EDT")
    public void recursosEnElEdt() throws Exception {
        AsistenteComando asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
        VistaRecursos vista = new VistaRecursos();
        vista.observar(asistente);
        vista.mostrarRecursos(asistente.getCombustible(), asistente.getEnergia(), asistente.getDesgaste(),
                asistente.getGemas(), asistente.getEstadoMotor(), asistente.naveListaParaOperar(),
                asistente.naveDestruida());
        String energiaInicial = vista.getTextoEnergia();

        conEdtBloqueado(
                () -> asistente.recibirImpacto(10),
                () -> assertEquals(energiaInicial, vista.getTextoEnergia(), "Con el EDT bloqueado no cambió."));

        assertEquals(String.valueOf(asistente.getEnergia()), vista.getTextoEnergia());
        assertEquals(String.valueOf(asistente.getCombustible()), vista.getTextoCombustible());

        conEdtBloqueado(() -> asistente.recibirImpacto(asistente.getEnergia()), () -> { });
        assertEquals("Destruida", vista.getTextoEstado());
    }
}
