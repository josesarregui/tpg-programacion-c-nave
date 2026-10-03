package modelo.bitacora;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BitacoraTest {

    private Bitacora bitacora;

    @BeforeEach
    public void setUp() {
        bitacora = new Bitacora();
    }

    @Test
    public void testBitacoraNuevaIniciaVacia() {
        List<Evento> eventos = bitacora.getEventos();

        assertNotNull(eventos);
        assertTrue(eventos.isEmpty(), "La bitácora recién creada debe iniciar sin eventos.");
        assertEquals(0, eventos.size());
    }

    @Test
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
    public void testEventoIncompletoSeRechaza() {
        // Observaciones de la Guía: la Bitácora no aceptará eventos nulos o vacíos.
        assertThrows(IllegalArgumentException.class, () -> new Evento(TipoEvento.MOTOR, ""));
        assertThrows(IllegalArgumentException.class, () -> new Evento(TipoEvento.MOTOR, "   "));
        assertThrows(IllegalArgumentException.class, () -> new Evento(TipoEvento.MOTOR, null));
        assertThrows(IllegalArgumentException.class, () -> new Evento(null, "Sin tipo."));
        assertThrows(IllegalArgumentException.class, () -> new Evento(null, TipoEvento.SISTEMA, "Sin fecha."));
    }

    @Test
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