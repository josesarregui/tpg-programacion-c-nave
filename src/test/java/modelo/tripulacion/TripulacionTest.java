package modelo.tripulacion;

import excepcion.TripulacionInvalidaException;
import excepcion.TripulanteInexistenteException;
import excepcion.TripulanteInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de E1-04 y de la Ficha de Inicio (tripulación mínima):
 * datos de cada tripulante, invariante de la tripulación y rechazos sin cambios parciales.
 */
public class TripulacionTest {

    private Capitan capitan;
    private Consejero consejero;
    private Teniente teniente;
    private Alferez primerAlferez;
    private Alferez segundoAlferez;

    @BeforeEach
    public void inicializar() throws Exception {
        capitan = new Capitan("Kirk", 3, Origen.TERRICOLA);
        consejero = new Consejero("Troi", 2, Origen.VULCANO);
        teniente = new Teniente("Uhura", 5, Origen.MARCIANO);
        primerAlferez = new Alferez("Chekov", 1, Origen.TERRICOLA);
        segundoAlferez = new Alferez("Sulu", 0, Origen.MARCIANO);
    }

    private List<Tripulante> tripulacionMinima() {
        return new ArrayList<>(List.of(capitan, consejero, teniente, primerAlferez, segundoAlferez));
    }

    // --- Tripulante ---

    @Test
    @DisplayName("E1-04: cada tripulante tiene identidad, cargo, origen y antigüedad")
    public void testDatosDelTripulante() {
        assertTrue(capitan.getId() > 0);
        assertNotEquals(capitan.getId(), consejero.getId(), "Las identidades deben ser únicas.");
        assertEquals("Kirk", capitan.getNombre());
        assertEquals(Cargo.CAPITAN, capitan.getCargo());
        assertEquals(Cargo.CONSEJERO, consejero.getCargo());
        assertEquals(Cargo.TENIENTE, teniente.getCargo());
        assertEquals(Cargo.ALFEREZ, primerAlferez.getCargo());
        assertEquals(Origen.TERRICOLA, capitan.getOrigen());
        assertEquals(3, capitan.getAntiguedad());
    }

    @Test
    @DisplayName("Rechazo: la antigüedad no puede ser negativa (la excepción guarda el dato rechazado)")
    public void testAntiguedadNegativa() {
        TripulanteInvalidoException excepcion =
                assertThrows(TripulanteInvalidoException.class, () -> new Teniente("Data", -1, Origen.MARCIANO));

        assertEquals(-1, excepcion.getAntiguedadRecibida());
        assertEquals("Data", excepcion.getNombreRecibido());
    }

    @Test
    @DisplayName("Rechazo: nombre vacío u origen no informado")
    public void testDatosInvalidos() {
        assertThrows(TripulanteInvalidoException.class, () -> new Alferez("  ", 1, Origen.TERRICOLA));
        assertThrows(TripulanteInvalidoException.class, () -> new Alferez(null, 1, Origen.TERRICOLA));
        assertThrows(TripulanteInvalidoException.class, () -> new Alferez("Sulu", 1, null));
    }

    @Test
    @DisplayName("Incrementar la antigüedad suma un año")
    public void testIncrementarAntiguedad() {
        teniente.incrementarAntiguedad();

        assertEquals(6, teniente.getAntiguedad());
    }

    @Test
    @DisplayName("El consejero registra sus consejos por período")
    public void testConsejosPorPeriodo() {
        YearMonth octubre = YearMonth.of(2026, 10);
        YearMonth noviembre = YearMonth.of(2026, 11);

        consejero.registrarConsejo(octubre);
        consejero.registrarConsejo(octubre);
        consejero.registrarConsejo(noviembre);

        assertEquals(2, consejero.getCantidadConsejos(octubre));
        assertEquals(1, consejero.getCantidadConsejos(noviembre));
        assertEquals(0, consejero.getCantidadConsejos(YearMonth.of(2026, 12)));
        assertEquals(4.0, consejero.calcularAdicionalConsejos(octubre), 0.0001);
    }

    // Las aserciones se verifican porque Maven Surefire ejecuta las pruebas con -ea por defecto.
    @Test
    @DisplayName("Aserción: el período de un consejo no puede ser nulo")
    public void testConsejoSinPeriodo() {
        assertThrows(AssertionError.class, () -> consejero.registrarConsejo(null));
    }

    // --- Tripulación ---

    @Test
    @DisplayName("Ficha de Inicio: capitán/a + 4 tripulantes forman una tripulación válida")
    public void testTripulacionMinimaValida() throws Exception {
        Tripulacion tripulacion = new Tripulacion(tripulacionMinima());

        assertEquals(Tripulacion.TAMANIO_MINIMO, tripulacion.getCantidad());
        assertEquals(capitan, tripulacion.getCapitan());
        assertEquals(2, tripulacion.getTripulantes(Cargo.ALFEREZ).size());
        assertEquals(teniente, tripulacion.buscarPorId(teniente.getId()));
    }

    @Test
    @DisplayName("Rechazo: buscar un id inexistente (la excepción guarda el id buscado)")
    public void testBuscarTripulanteInexistente() throws Exception {
        Tripulacion tripulacion = new Tripulacion(tripulacionMinima());

        TripulanteInexistenteException excepcion =
                assertThrows(TripulanteInexistenteException.class, () -> tripulacion.buscarPorId(-5));

        assertEquals(-5, excepcion.getIdBuscado());
    }

    @Test
    @DisplayName("Rechazo: tripulación sin capitán/a")
    public void testTripulacionSinCapitan() throws Exception {
        List<Tripulante> sinCapitan = List.of(consejero, teniente, primerAlferez, segundoAlferez,
                new Teniente("Scott", 4, Origen.TERRICOLA));

        assertThrows(TripulacionInvalidaException.class, () -> new Tripulacion(sinCapitan));
    }

    @Test
    @DisplayName("Rechazo: menos de 4 tripulantes adicionales")
    public void testTripulacionIncompleta() {
        List<Tripulante> incompleta = List.of(capitan, consejero, teniente, primerAlferez);

        assertThrows(TripulacionInvalidaException.class, () -> new Tripulacion(incompleta));
    }

    @Test
    @DisplayName("Rechazo: dos capitanes, tripulantes repetidos o lista nula")
    public void testCapitanDuplicadoOTripulanteRepetido() throws Exception {
        Capitan segundoCapitan = new Capitan("Picard", 10, Origen.TERRICOLA);
        List<Tripulante> dosCapitanes = tripulacionMinima();
        dosCapitanes.add(segundoCapitan);
        List<Tripulante> repetido = tripulacionMinima();
        repetido.add(teniente);

        TripulacionInvalidaException excepcion =
                assertThrows(TripulacionInvalidaException.class, () -> new Tripulacion(dosCapitanes));
        assertEquals(segundoCapitan, excepcion.getTripulanteRechazado());
        assertThrows(TripulacionInvalidaException.class, () -> new Tripulacion(repetido));
        assertThrows(TripulacionInvalidaException.class, () -> new Tripulacion(null));
    }

    @Test
    @DisplayName("Incorporar y desembarcar respetan el invariante")
    public void testIncorporarYDesembarcar() throws Exception {
        Tripulacion tripulacion = new Tripulacion(tripulacionMinima());
        Teniente nuevo = new Teniente("Scott", 4, Origen.TERRICOLA);

        tripulacion.incorporar(nuevo);
        assertEquals(6, tripulacion.getCantidad());

        tripulacion.desembarcar(nuevo);
        assertEquals(5, tripulacion.getCantidad());
    }

    @Test
    @DisplayName("Rechazo sin cambios parciales: desembarcar al capitán o quedar bajo el mínimo")
    public void testDesembarcoInvalidoNoModificaLaTripulacion() throws Exception {
        Tripulacion tripulacion = new Tripulacion(tripulacionMinima());
        Alferez ajeno = new Alferez("Ajeno", 1, Origen.VULCANO);

        assertThrows(TripulacionInvalidaException.class, () -> tripulacion.desembarcar(capitan));
        assertThrows(TripulacionInvalidaException.class, () -> tripulacion.desembarcar(primerAlferez));
        assertThrows(TripulacionInvalidaException.class, () -> tripulacion.desembarcar(ajeno));

        assertEquals(Tripulacion.TAMANIO_MINIMO, tripulacion.getCantidad(), "El estado anterior debe conservarse.");
        assertTrue(tripulacion.getTripulantes().contains(primerAlferez));
    }

    @Test
    @DisplayName("Rechazo: incorporar un segundo capitán o un tripulante ya embarcado")
    public void testIncorporacionInvalida() throws Exception {
        Tripulacion tripulacion = new Tripulacion(tripulacionMinima());
        Capitan segundoCapitan = new Capitan("Picard", 10, Origen.TERRICOLA);

        assertThrows(TripulacionInvalidaException.class, () -> tripulacion.incorporar(segundoCapitan));
        assertThrows(TripulacionInvalidaException.class, () -> tripulacion.incorporar(teniente));
        assertThrows(TripulacionInvalidaException.class, () -> tripulacion.incorporar(null));
        assertEquals(Tripulacion.TAMANIO_MINIMO, tripulacion.getCantidad());
    }

    @Test
    @DisplayName("La tripulación no expone su lista interna")
    public void testEncapsulamiento() throws Exception {
        List<Tripulante> lista = tripulacionMinima();
        Tripulacion tripulacion = new Tripulacion(lista);

        lista.clear(); // Modificar la lista original no debe afectar a la tripulación.

        assertEquals(Tripulacion.TAMANIO_MINIMO, tripulacion.getCantidad());
        assertThrows(UnsupportedOperationException.class, () -> tripulacion.getTripulantes().clear());
    }
}
