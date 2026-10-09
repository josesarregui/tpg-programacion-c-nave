package modelo.universo;

import excepcion.NaveInexistenteException;
import excepcion.NaveNoDisponibleException;
import excepcion.NaveYaRegistradaException;
import excepcion.RecursoInsuficienteException;
import modelo.asistente.Asistente;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.InformeMision;
import modelo.mision.MisionIntercepcion;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de la Aclaración "Naves, asistentes y misiones": R1 (el centro de control registra naves listas
 * para operar y devuelve cualquiera de ellas), R2 (una única nave en uso a la vez) y los Pedidos del diseño
 * (el centro no crea los objetos que guarda y admite una nueva variante de asistente sin modificarse).
 */
public class CentroDeControlTest {

    private CentroDeControl centro;
    private NaveFactory fabrica;
    private Asistente exploradora;
    private Asistente carguero;
    private Asistente combate;

    @BeforeEach
    public void inicializar() throws Exception {
        centro = new CentroDeControl();
        fabrica = new NaveFactory();
        exploradora = new AsistenteComando(fabrica.crearNave(TipoNave.EXPLORADORA));
        carguero = new AsistenteComando(fabrica.crearNave(TipoNave.CARGUERO));
        combate = new AsistenteComando(fabrica.crearNave(TipoNave.COMBATE));
        centro.registrar(exploradora);
        centro.registrar(carguero);
        centro.registrar(combate);
    }

    // --- R1: registrar y devolver naves ---

    @Test
    @DisplayName("R1: el centro registra las naves creadas por la fábrica, en orden, aunque todavía no tengan tripulación")
    public void testRegistro() {
        assertEquals(3, centro.getCantidadNaves());
        List<Asistente> registrados = centro.getAsistentes();
        assertSame(exploradora, registrados.get(0));
        assertSame(carguero, registrados.get(1));
        assertSame(combate, registrados.get(2));
        assertFalse(exploradora.tieneTripulacion(), "Escenario A: la tripulación se asigna después de seleccionar la nave");
        assertUltimoEvento(exploradora, TipoEvento.SISTEMA, "registrada");
    }

    @Test
    @DisplayName("R1: el centro devuelve cualquiera de las naves registradas cuando se la pide")
    public void testBuscar() throws Exception {
        assertSame(exploradora, centro.buscar(exploradora.getIdNave()));
        assertSame(carguero, centro.buscar(carguero.getIdNave()));
        assertSame(combate, centro.buscar(combate.getIdNave()));
    }

    @Test
    @DisplayName("Rechazo: pedir una nave que no está registrada lanza NaveInexistenteException con el id buscado")
    public void testBuscarInexistente() {
        NaveInexistenteException e = assertThrows(NaveInexistenteException.class, () -> centro.buscar(-1));

        assertEquals(-1, e.getIdNaveBuscada());
    }

    @Test
    @DisplayName("Rechazo: una nave no puede registrarse dos veces y el centro no cambia")
    public void testRegistroRepetido() {
        NaveYaRegistradaException e = assertThrows(NaveYaRegistradaException.class, () -> centro.registrar(carguero));

        assertEquals(carguero.getIdNave(), e.getIdNave());
        assertEquals(3, centro.getCantidadNaves());
    }

    @Test
    @DisplayName("Rechazo por contrato: no se registra un asistente nulo")
    public void testRegistroNulo() {
        assertThrows(AssertionError.class, () -> centro.registrar(null));
        assertEquals(3, centro.getCantidadNaves());
    }

    @Test
    @DisplayName("La lista de naves registradas es de sólo lectura")
    public void testListaDeSoloLectura() {
        assertThrows(UnsupportedOperationException.class, () -> centro.getAsistentes().clear());
    }

    // --- R2: una única nave a la vez ---

    @Test
    @DisplayName("R2: al comienzo no hay nave en uso")
    public void testSinNaveEnUso() {
        assertFalse(centro.hayNaveEnUso());
        assertNull(centro.getAsistenteEnUso());
    }

    @Test
    @DisplayName("R2: se usa una única nave a la vez; seleccionar otra reemplaza a la anterior")
    public void testSeleccion() throws Exception {
        assertSame(exploradora, centro.seleccionar(exploradora.getIdNave()));
        assertTrue(centro.hayNaveEnUso());
        assertSame(exploradora, centro.getAsistenteEnUso());
        assertUltimoEvento(exploradora, TipoEvento.SISTEMA, "nave en uso");

        centro.seleccionar(combate.getIdNave());

        assertSame(combate, centro.getAsistenteEnUso());
    }

    @Test
    @DisplayName("Rechazo: seleccionar una nave inexistente no cambia la nave en uso")
    public void testSeleccionInexistente() throws Exception {
        centro.seleccionar(carguero.getIdNave());

        assertThrows(NaveInexistenteException.class, () -> centro.seleccionar(-1));
        assertSame(carguero, centro.getAsistenteEnUso());
    }

    // --- Pedidos del diseño ---

    @Test
    @DisplayName("Pedido del diseño: otra variante de asistente se registra y realiza misiones sin modificar el centro")
    public void testOtraVarianteDeAsistente() throws Exception {
        Asistente variante = new AsistenteConAutorizacion(fabrica.crearNave(TipoNave.COMBATE));
        centro.registrar(variante);

        Asistente enUso = centro.seleccionar(variante.getIdNave());
        enUso.asignarTripulacion(crearTripulacion());
        enUso.encomendarMision(new MisionIntercepcion());
        InformeMision informe = enUso.ejecutarMision();

        assertSame(variante, enUso);
        assertEquals(4, centro.getCantidadNaves());
        assertTrue(informe.isExitosa());
        boolean autorizacionRegistrada = false;
        for (Evento evento : enUso.getEventos()) {
            autorizacionRegistrada = autorizacionRegistrada || evento.getTipo() == TipoEvento.RELEVANTE;
        }
        assertTrue(autorizacionRegistrada, "La variante agregó su propio comportamiento");
    }

    // --- Auxiliares ---

    /**
     * Variante de asistente que sólo existe en esta prueba: antes de ejecutar una misión deja constancia
     * de la autorización del/de la capitán/a. El centro de control y las misiones la aceptan sin cambios
     * porque dependen de la interfaz Asistente (principio de sustitución de Liskov).
     */
    private static class AsistenteConAutorizacion extends AsistenteComando {

        AsistenteConAutorizacion(Nave nave) {
            super(nave);
        }

        @Override
        public InformeMision ejecutarMision() throws NaveNoDisponibleException, RecursoInsuficienteException {
            registrarEvento(TipoEvento.RELEVANTE, "El/la capitán/a autorizó la misión " + getMisionPendiente().getCodigo() + ".");
            return super.ejecutarMision();
        }
    }

    private Tripulacion crearTripulacion() throws Exception {
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Sisko", 5, Origen.TERRICOLA));
        integrantes.add(new Consejero("Dax", 8, Origen.MARCIANO));
        integrantes.add(new Teniente("Kira", 3, Origen.MARCIANO));
        integrantes.add(new Alferez("Nog", 0, Origen.TERRICOLA));
        integrantes.add(new Alferez("Ezri", 1, Origen.TERRICOLA));
        return new Tripulacion(integrantes);
    }

    private void assertUltimoEvento(Asistente asistente, TipoEvento tipo, String texto) {
        List<Evento> eventos = asistente.getEventos();
        Evento ultimo = eventos.get(eventos.size() - 1);
        assertEquals(tipo, ultimo.getTipo());
        assertTrue(ultimo.getDescripcion().contains(texto), "El último evento no contiene: " + texto);
    }
}
