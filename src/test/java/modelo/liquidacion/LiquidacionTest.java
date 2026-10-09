package modelo.liquidacion;

import excepcion.LiquidacionInvalidaException;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de E1-08: cálculo verificable de haberes para los cuatro cargos y los tres orígenes,
 * composición de dos o más decoradores y rechazos por precondiciones incumplidas.
 */
public class LiquidacionTest {

    private static final double TOLERANCIA = 0.0001;
    private static final YearMonth OCTUBRE = YearMonth.of(2026, 10);
    private static final YearMonth NOVIEMBRE = YearMonth.of(2026, 11);

    private LiquidadorHaberes liquidador;

    @BeforeEach
    public void inicializar() {
        liquidador = new LiquidadorHaberes();
    }

    // Total esperado = remuneración del cargo + remuneración x % x años + subsidio por origen.
    @ParameterizedTest(name = "{0} {1} con {2} años cobra {3} PG")
    @CsvSource({
            "CAPITAN,   TERRICOLA, 3,  1620.0",  // 1000 + 600 + 20
            "CAPITAN,   VULCANO,   0,  1030.0",  // 1000 + 0   + 30
            "CAPITAN,   MARCIANO,  1,  1218.0",  // 1000 + 200 + 18
            "CONSEJERO, TERRICOLA, 2,  680.0",   // 600  + 60  + 20 (sin consejos)
            "CONSEJERO, VULCANO,   4,  750.0",   // 600  + 120 + 30
            "CONSEJERO, MARCIANO,  10, 918.0",   // 600  + 300 + 18
            "TENIENTE,  TERRICOLA, 1,  432.0",   // 400  + 12  + 20
            "TENIENTE,  VULCANO,   5,  490.0",   // 400  + 60  + 30
            "TENIENTE,  MARCIANO,  7,  502.0",   // 400  + 84  + 18
            "ALFEREZ,   TERRICOLA, 3,  223.0",   // 200  + 3   + 20
            "ALFEREZ,   VULCANO,   1,  231.0",   // 200  + 1   + 30
            "ALFEREZ,   MARCIANO,  9,  227.0"    // 200  + 9   + 18
    })
    @DisplayName("E1-08: haber de cada cargo y origen con antigüedad")
    public void testHaberPorCargoOrigenYAntiguedad(Cargo cargo, Origen origen, int antiguedad, double totalEsperado) throws Exception {
        Tripulante tripulante = crearTripulante(cargo, antiguedad, origen);

        ReciboHaberes recibo = liquidador.liquidar(tripulante, OCTUBRE);

        assertEquals(totalEsperado, recibo.getTotal(), TOLERANCIA);
    }

    @Test
    @DisplayName("El recibo mantiene identificable el aporte de cada concepto")
    public void testDetallePorConcepto() throws Exception {
        Tripulante capitan = new Capitan("Kirk", 3, Origen.TERRICOLA);

        ReciboHaberes recibo = liquidador.liquidar(capitan, OCTUBRE);
        List<ConceptoHaber> conceptos = recibo.getConceptos();

        assertEquals(3, conceptos.size());
        verificarConcepto(conceptos.get(0), TipoConcepto.CARGO, 1000.0);
        verificarConcepto(conceptos.get(1), TipoConcepto.ANTIGUEDAD, 600.0);
        verificarConcepto(conceptos.get(2), TipoConcepto.ORIGEN, 20.0);
        assertEquals(capitan, recibo.getTripulante());
        assertEquals(OCTUBRE, recibo.getPeriodo());
    }

    @Test
    @DisplayName("Composición de dos decoradores sobre el tripulante (componente concreto)")
    public void testComposicionDeDosDecoradores() throws Exception {
        Teniente teniente = new Teniente("Uhura", 5, Origen.VULCANO);

        Liquidacion conAntiguedad = new AntiguedadDecorator(teniente);
        Liquidacion completa = new OrigenDecorator(conAntiguedad);

        assertEquals(400.0, teniente.calcularHaberTotal(OCTUBRE), TOLERANCIA);
        assertEquals(460.0, conAntiguedad.calcularHaberTotal(OCTUBRE), TOLERANCIA);
        assertEquals(490.0, completa.calcularHaberTotal(OCTUBRE), TOLERANCIA);
    }

    @Test
    @DisplayName("Los decoradores pueden aplicarse en cualquier orden con el mismo resultado")
    public void testOrdenDeLosDecoradores() throws Exception {
        Alferez alferez = new Alferez("Chekov", 3, Origen.MARCIANO);

        Liquidacion antiguedadPrimero = new OrigenDecorator(new AntiguedadDecorator(alferez));
        Liquidacion origenPrimero = new AntiguedadDecorator(new OrigenDecorator(alferez));

        assertEquals(antiguedadPrimero.calcularHaberTotal(OCTUBRE), origenPrimero.calcularHaberTotal(OCTUBRE), TOLERANCIA);
    }

    @Test
    @DisplayName("Cada cargo calcula su propio adicional por antigüedad")
    public void testAdicionalAntiguedadPorCargo() throws Exception {
        assertEquals(600.0, new Capitan("Kirk", 3, Origen.TERRICOLA).calcularAdicionalAntiguedad(), TOLERANCIA);
        assertEquals(60.0, new Consejero("Troi", 2, Origen.VULCANO).calcularAdicionalAntiguedad(), TOLERANCIA);
        assertEquals(60.0, new Teniente("Uhura", 5, Origen.MARCIANO).calcularAdicionalAntiguedad(), TOLERANCIA);
        assertEquals(3.0, new Alferez("Chekov", 3, Origen.TERRICOLA).calcularAdicionalAntiguedad(), TOLERANCIA);
    }

    @Test
    @DisplayName("El consejero cobra 2 PG por cada consejo del período liquidado")
    public void testConsejeroCobraConsejosDelPeriodo() throws Exception {
        Consejero consejero = new Consejero("Troi", 2, Origen.VULCANO);
        consejero.registrarConsejo(OCTUBRE);
        consejero.registrarConsejo(OCTUBRE);
        consejero.registrarConsejo(OCTUBRE);
        consejero.registrarConsejo(NOVIEMBRE);

        ReciboHaberes octubre = liquidador.liquidar(consejero, OCTUBRE);
        ReciboHaberes noviembre = liquidador.liquidar(consejero, NOVIEMBRE);

        // Cargo + consejos (los aporta el consejero) + antigüedad + origen (los agregan los decoradores).
        assertEquals(4, octubre.getConceptos().size());
        verificarConcepto(octubre.getConceptos().get(1), TipoConcepto.CONSEJOS, 6.0);
        assertEquals(696.0, octubre.getTotal(), TOLERANCIA);     // 600 + 6 + 60 + 30
        assertEquals(692.0, noviembre.getTotal(), TOLERANCIA);   // 600 + 2 + 60 + 30
    }

    @Test
    @DisplayName("Sólo el consejero tiene el concepto por consejos")
    public void testOtrosCargosNoTienenConceptoConsejos() throws Exception {
        ReciboHaberes recibo = liquidador.liquidar(new Teniente("Uhura", 5, Origen.VULCANO), OCTUBRE);

        for (ConceptoHaber concepto : recibo.getConceptos()) {
            assertNotEquals(TipoConcepto.CONSEJOS, concepto.getTipo());
        }
    }

    @Test
    @DisplayName("Liquidación de toda la tripulación")
    public void testLiquidacionDeLaTripulacion() throws Exception {
        Consejero consejero = new Consejero("Troi", 2, Origen.VULCANO);
        consejero.registrarConsejo(OCTUBRE);
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Kirk", 3, Origen.TERRICOLA));  // 1620
        integrantes.add(consejero);  // 692
        integrantes.add(new Teniente("Uhura", 5, Origen.MARCIANO));  // 478
        integrantes.add(new Alferez("Chekov", 3, Origen.TERRICOLA));  // 223
        integrantes.add(new Alferez("Sulu", 0, Origen.MARCIANO));  // 218
        Tripulacion tripulacion = new Tripulacion(integrantes);

        LiquidacionTripulacion resultado = liquidador.liquidar(tripulacion, OCTUBRE);

        assertEquals(OCTUBRE, resultado.getPeriodo());
        assertEquals(5, resultado.getRecibos().size());
        assertEquals(3231.0, resultado.calcularTotal(), TOLERANCIA);
        assertEquals(tripulacion.getCapitan(), resultado.getRecibos().get(0).getTripulante());
    }

    // --- Rechazos por precondiciones o invariantes ---

    @Test
    @DisplayName("Rechazo: el cálculo de haberes no acepta conceptos nulos")
    public void testDecoradorSobreLiquidacionNula() {
        LiquidacionInvalidaException excepcion =
                assertThrows(LiquidacionInvalidaException.class, () -> new AntiguedadDecorator(null));
        assertEquals(TipoConcepto.ANTIGUEDAD, excepcion.getConceptoRechazado());

        assertThrows(LiquidacionInvalidaException.class, () -> new OrigenDecorator(null));
    }

    @Test
    @DisplayName("Rechazo: un mismo concepto no puede aplicarse dos veces")
    public void testConceptoDuplicadoSeRechaza() throws Exception {
        Capitan capitan = new Capitan("Kirk", 3, Origen.TERRICOLA);
        Liquidacion conAntiguedad = new AntiguedadDecorator(capitan);
        Liquidacion conOrigen = new OrigenDecorator(conAntiguedad);

        assertThrows(LiquidacionInvalidaException.class, () -> new AntiguedadDecorator(conAntiguedad));
        assertThrows(LiquidacionInvalidaException.class, () -> new OrigenDecorator(conOrigen));
        LiquidacionInvalidaException excepcion =
                assertThrows(LiquidacionInvalidaException.class, () -> new AntiguedadDecorator(conOrigen));
        assertEquals(TipoConcepto.ANTIGUEDAD, excepcion.getConceptoRechazado());
    }

    @Test
    @DisplayName("Rechazo: no se puede liquidar un tripulante, tripulación o período nulo")
    public void testLiquidarConDatosNulos() throws Exception {
        Alferez alferez = new Alferez("Chekov", 1, Origen.TERRICOLA);

        assertThrows(LiquidacionInvalidaException.class, () -> liquidador.liquidar((Tripulante) null, OCTUBRE));
        assertThrows(LiquidacionInvalidaException.class, () -> liquidador.liquidar(alferez, null));
        assertThrows(LiquidacionInvalidaException.class, () -> liquidador.liquidar((Tripulacion) null, OCTUBRE));
    }

    // Las aserciones se verifican porque Maven Surefire ejecuta las pruebas con -ea por defecto.
    @Test
    @DisplayName("Aserción: un concepto no admite importes negativos")
    public void testConceptoConImporteNegativo() {
        assertThrows(AssertionError.class, () -> new ConceptoHaber(TipoConcepto.CARGO, "Cargo", -1.0));
        assertThrows(AssertionError.class, () -> new ConceptoHaber(TipoConcepto.CARGO, " ", 10.0));
        assertThrows(AssertionError.class, () -> new ConceptoHaber(null, "Cargo", 10.0));
    }

    @Test
    @DisplayName("El detalle del recibo es de solo lectura")
    public void testDetalleInmodificable() throws Exception {
        ReciboHaberes recibo = liquidador.liquidar(new Alferez("Chekov", 1, Origen.TERRICOLA), OCTUBRE);

        assertThrows(UnsupportedOperationException.class, () -> recibo.getConceptos().clear());
    }

    private static Tripulante crearTripulante(Cargo cargo, int antiguedad, Origen origen) throws Exception {
        Tripulante tripulante = null;
        switch (cargo) {
            case CAPITAN:
                tripulante = new Capitan("Capitán de prueba", antiguedad, origen);
                break;
            case CONSEJERO:
                tripulante = new Consejero("Consejero de prueba", antiguedad, origen);
                break;
            case TENIENTE:
                tripulante = new Teniente("Teniente de prueba", antiguedad, origen);
                break;
            case ALFEREZ:
                tripulante = new Alferez("Alférez de prueba", antiguedad, origen);
                break;
        }
        return tripulante;
    }

    private static void verificarConcepto(ConceptoHaber concepto, TipoConcepto tipoEsperado, double importeEsperado) {
        assertEquals(tipoEsperado, concepto.getTipo());
        assertEquals(importeEsperado, concepto.getImporte(), TOLERANCIA);
    }
}
