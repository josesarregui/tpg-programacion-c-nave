package modelo.bitacora;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de E1-05 y de la Aclaración R5: registro en orden temporal, eventos inmutables y
 * rechazo de eventos nulos, vacíos o fuera de orden (Observaciones).
 */
public class BitacoraTest {

    private Bitacora bitacora;

    @BeforeEach
    public void setUp() {
        bitacora = new Bitacora();
    }

    @Test
    @DisplayName("La Bitácora recién creada no tiene eventos")
    public void testBitacoraNuevaIniciaVacia() {
        List<Evento> eventos = bitacora.getEventos();

        assertNotNull(eventos);
        assertTrue(eventos.isEmpty(), "La bitácora recién creada debe iniciar sin eventos.");
        assertEquals(0, eventos.size());
    }

    @Test
    @DisplayName("E1-05: un evento válido queda registrado con su tipo y descripción")
    public void testRegistrarEventoValidoAgregaCorrectamente() {
        Evento evento = new Evento(TipoEvento.MOTOR, "Salto Warp completado a factor 4.");

        bitacora.registrarEvento(evento);
        List<Evento> eventos = bitacora.getEventos();

        assertEquals(1, eventos.size());
        assertEquals(evento, eventos.get(0));
        assertEquals(TipoEvento.MOTOR, eventos.get(0).getTipo());
        assertEquals("Salto Warp completado a factor 4.", eventos.get(0).getDescripcion());
    }

    @Test
    @DisplayName("E1-05: los eventos se consultan en el orden en que ocurrieron")
    public void testRegistrarMultiplesEventosMantieneOrdenCronologico() {
        Evento e1 = new Evento(TipoEvento.SISTEMA, "Arranque de sistemas auxiliares.");
        Evento e2 = new Evento(TipoEvento.RECURSO, "Carga de deuterio al 100%.");
        Evento e3 = new Evento(TipoEvento.MISION, "Inicio de maniobra de exploración.");

        bitacora.registrarEvento(e1);
        bitacora.registrarEvento(e2);
        bitacora.registrarEvento(e3);

        List<Evento> eventos = bitacora.getEventos();

        assertEquals(3, eventos.size());
        assertEquals(e1, eventos.get(0));
        assertEquals(e2, eventos.get(1));
        assertEquals(e3, eventos.get(2));
    }

    @Test
    @DisplayName("Observaciones: la Bitácora no acepta eventos nulos y no cambia")
    public void testRegistrarEventoNuloLanzaExcepcion() {
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> bitacora.registrarEvento(null),
                "Registrar un evento nulo debe lanzar IllegalArgumentException."
        );

        assertNotNull(excepcion.getMessage());
        assertTrue(bitacora.getEventos().isEmpty(), "La bitácora no debe haber registrado ningún elemento.");
    }

    @Test
    @DisplayName("Observaciones: un evento sin fecha, sin tipo o con descripción vacía se rechaza")
    public void testEventoIncompletoSeRechaza() {
        // Observaciones de la Guía: la Bitácora no aceptará eventos nulos o vacíos.
        assertThrows(IllegalArgumentException.class, () -> new Evento(TipoEvento.MOTOR, ""));
        assertThrows(IllegalArgumentException.class, () -> new Evento(TipoEvento.MOTOR, "   "));
        assertThrows(IllegalArgumentException.class, () -> new Evento(TipoEvento.MOTOR, null));
        assertThrows(IllegalArgumentException.class, () -> new Evento(null, "Sin tipo."));
        assertThrows(IllegalArgumentException.class, () -> new Evento(null, TipoEvento.SISTEMA, "Sin fecha."));
    }

    @Test
    @DisplayName("E1-05: un evento anterior al último se rechaza para conservar el orden temporal")
    public void testEventoFueraDeOrdenSeRechaza() {
        // E1-05: los eventos deben poder consultarse en orden temporal.
        LocalDateTime ahora = LocalDateTime.of(2026, 10, 9, 12, 0);
        Evento primero = new Evento(ahora, TipoEvento.SISTEMA, "Arranque.");
        bitacora.registrarEvento(primero);
        Evento anterior = new Evento(ahora.minusHours(1), TipoEvento.SISTEMA, "Evento con fecha anterior.");

        assertThrows(IllegalArgumentException.class, () -> bitacora.registrarEvento(anterior));
        assertEquals(1, bitacora.getEventos().size(), "La bitácora no cambia al rechazar el evento.");

        Evento simultaneo = new Evento(ahora, TipoEvento.MOTOR, "Evento en el mismo instante.");
        bitacora.registrarEvento(simultaneo);
        assertEquals(2, bitacora.getEventos().size(), "Un evento del mismo instante conserva el orden temporal.");
    }

    @Test
    @DisplayName("Aclaración R5: la lista de eventos es de sólo lectura")
    public void testGetEventosRetornaColeccionInmutable() {
        Evento evento = new Evento(TipoEvento.MOTOR, "Prueba de aislamiento.");
        bitacora.registrarEvento(evento);

        List<Evento> eventos = bitacora.getEventos();
        Evento intentoExterno = new Evento(TipoEvento.ERROR, "Intrusión externa.");

        assertThrows(
                UnsupportedOperationException.class,
                () -> eventos.add(intentoExterno),
                "No debe permitirse agregar eventos modificando la lista retornada."
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> eventos.clear(),
                "No debe permitirse vaciar la bitácora desde la lista retornada."
        );

        assertEquals(1, bitacora.getEventos().size(), "El estado interno debe permanecer inalterado.");
    }
}