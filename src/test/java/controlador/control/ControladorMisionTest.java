package controlador.control;

import controlador.VistasDePrueba.ControlDePrueba;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import modelo.asistente.AsistenteComando;
import modelo.mision.InformeMision;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.universo.DestinoSinMovimiento;
import modelo.universo.Posicion;
import modelo.universo.TableroDePrueba;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.principal.panelcontrol.IVistaControl;
import vista.principal.panelcontrol.panelcursomision.IVistaCursoMision;

import static controlador.VistasDePrueba.evento;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Hilo de la misión (Plan, Paso 6): iniciar una misión no bloquea a quien la inicia, la misión corre en su propio
 * hilo, actualiza por Observer los recursos, la Bitácora, el informe y la grilla, no se admiten dos misiones a la vez,
 * cancelarla la termina y la informa, y las excepciones del hilo llegan a la vista.
 *
 * El universo es determinista: un tablero vacío de 20 x 20 con el objetivo fijo y un Destino que no mueve elementos.
 * Las esperas usan colas, latches y join con tiempo límite.
 */
public class ControladorMisionTest {

    private static final long ESPERA_SEGUNDOS = 5;
    /** Pausa por turno tan larga que la misión sólo termina si se la cancela. */
    private static final int PAUSA_LARGA_MS = 60_000;
    private static final Posicion OBJETIVO = new Posicion(10, 13);

    private AsistenteComando asistente;
    private TableroDePrueba tablero;
    private ControlDePrueba vista;

    @BeforeEach
    public void setUp() throws Exception {
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
        asistente.asignarTripulacion(new Tripulacion(List.of(
                new Capitan("James Kirk", 15, Origen.TERRICOLA),
                new Consejero("Deanna Troi", 8, Origen.VULCANO),
                new Teniente("Spock", 12, Origen.VULCANO),
                new Alferez("Hikaru Sulu", 5, Origen.TERRICOLA),
                new Alferez("Pavel Chekov", 2, Origen.MARCIANO))));
        tablero = new TableroDePrueba(20, 20);
        tablero.fijarObjetivo(OBJETIVO);
        vista = new ControlDePrueba();
    }

    private ControladorControl crearControl(int pausaPorTurnoMs) {
        return new ControladorControl(vista, asistente, tablero, new DestinoSinMovimiento(tablero), pausaPorTurnoMs);
    }

    private Thread esperarHiloDeLaMision() throws InterruptedException {
        assertTrue(vista.curso.grilla.primeraNotificacion.await(ESPERA_SEGUNDOS, TimeUnit.SECONDS),
                "El tablero notificó a su vista.");
        assertEquals(1, vista.curso.grilla.hilosQueNotificaron.size(), "Sólo el hilo de la misión mueve el tablero.");
        return vista.curso.grilla.hilosQueNotificaron.iterator().next();
    }

    @Test
    @DisplayName("Iniciar no bloquea: la misión corre en su propio hilo y no se admite otra a la vez")
    public void iniciarNoBloqueaYNoAdmiteDosMisiones() throws Exception {
        ControladorControl control = crearControl(PAUSA_LARGA_MS);
        vista.select.seleccionar("M-02");

        // Con una pausa de 60 s por turno, si iniciar ejecutara la misión en este hilo tardaría más de 2 s.
        assertTimeout(Duration.ofSeconds(2), vista.select::iniciar);

        assertTrue(control.hayMisionEnCurso());
        assertEquals(IVistaControl.MISION_CURSO, vista.etapa);
        assertEquals(List.of("M-02"), vista.curso.iniciadas);
        Thread hilo = esperarHiloDeLaMision();
        assertNotSame(Thread.currentThread(), hilo);
        assertEquals("Misión M-02", hilo.getName());
        assertTrue(hilo.isAlive());

        // Una segunda misión se rechaza y no toca la que está en curso.
        vista.select.seleccionar("M-01");
        vista.select.iniciar();
        assertEquals("Ya hay una misión en curso: espere a que termine o cancélela.", vista.select.ultimoError());
        assertEquals("M-02", asistente.getMisionPendiente().getCodigo());
        assertEquals(List.of("M-02"), vista.curso.iniciadas);

        // Con la misión en curso no se vuelve a la selección.
        vista.curso.controlador.actionPerformed(evento(IVistaCursoMision.COMANDO_VOLVER));
        assertEquals("La misión sigue en curso: cancélela o espere a que termine.", vista.curso.errores.get(0));
        control.actualizarVista();
        assertEquals(IVistaControl.MISION_CURSO, vista.etapa);

        assertTrue(control.cancelarMision());
        assertFalse(hilo.isAlive());
    }

    @Test
    @DisplayName("La misión completa actualiza por Observer recursos, Bitácora, consola, informe y grilla")
    public void misionCompletaActualizaLasVistasPorObserver() throws Exception {
        ControladorControl control = crearControl(1);
        int eventosAntes = asistente.getEventos().size();
        int combustibleAntes = asistente.getCombustible();
        vista.select.seleccionar("M-02");

        vista.select.iniciar();

        String fin = vista.curso.finalizaciones.poll(ESPERA_SEGUNDOS, TimeUnit.SECONDS);
        assertNotNull(fin, "El fin de la misión se informó en la vista.");
        Thread hilo = esperarHiloDeLaMision();
        hilo.join(TimeUnit.SECONDS.toMillis(ESPERA_SEGUNDOS));
        assertFalse(hilo.isAlive());
        assertFalse(control.hayMisionEnCurso());
        assertTrue(fin.contains("M-02") && fin.contains("EXITOSA") && !fin.contains("NO EXITOSA"), fin);
        assertFalse(fin.contains("cancelada"));
        assertTrue(vista.curso.hilosDeAviso.contains(hilo), "El fin lo avisa el hilo de la misión.");

        // Grilla: consultada en el hilo de la misión.
        assertEquals(20, vista.curso.grilla.filasVistas);
        assertEquals(20, vista.curso.grilla.columnasVistas);
        assertEquals(OBJETIVO, vista.curso.grilla.naveVista, "La última notificación muestra la nave en el objetivo.");
        assertEquals(OBJETIVO, vista.curso.grilla.objetivoVisto);
        assertTrue(vista.curso.grilla.notificaciones.get() >= 3, "Un movimiento por casilla recorrida.");

        // Recursos, Bitácora y consola: notificados por el asistente desde el hilo de la misión.
        assertTrue(vista.recursos.hilosQueNotificaron.contains(hilo));
        assertEquals(asistente.getCombustible(), vista.recursos.combustibleVisto);
        assertTrue(vista.recursos.combustibleVisto < combustibleAntes);
        assertTrue(vista.select.hilosQueNotificaron.contains(hilo));
        assertEquals(asistente.getEventos().size(), vista.select.eventosVistos);
        assertTrue(vista.select.eventosVistos > eventosAntes);
        assertTrue(vista.curso.mensajes.stream().anyMatch(m -> m.startsWith("Informe de ")));

        // Informe final en el historial.
        assertTrue(vista.resultados.hilosQueNotificaron.contains(hilo));
        List<InformeMision> informes = vista.resultados.informesVistos;
        assertEquals(1, informes.size());
        assertTrue(informes.get(0).isExitosa());
        assertTrue(asistente.getGemas() > 0, "M-02 acredita las gemas al cerrar.");

        // Terminada, se puede volver a la selección e iniciar otra.
        vista.curso.controlador.actionPerformed(evento(IVistaCursoMision.COMANDO_VOLVER));
        assertEquals(IVistaControl.SELECT_MISION, vista.etapa);
        vista.curso.controlador.actionPerformed(evento(IVistaCursoMision.COMANDO_VER_RESULTADOS));
        assertEquals(IVistaControl.RESULTADOS_MISION, vista.etapa);
    }

    @Test
    @DisplayName("Cancelar interrumpe el hilo, espera su fin con join y la misión termina fallida e informada")
    public void cancelarTerminaLaMisionYLaInforma() throws Exception {
        ControladorControl control = crearControl(PAUSA_LARGA_MS);
        vista.select.seleccionar("M-02");
        vista.select.iniciar();
        Thread hilo = esperarHiloDeLaMision();

        vista.curso.cancelar();

        assertFalse(hilo.isAlive(), "Cancelar espera a que el hilo termine.");
        assertFalse(control.hayMisionEnCurso());
        String fin = vista.curso.finalizaciones.poll(ESPERA_SEGUNDOS, TimeUnit.SECONDS);
        assertNotNull(fin);
        assertTrue(fin.contains("cancelada") && fin.contains("NO EXITOSA"), fin);
        assertEquals(1, asistente.getMisionesRealizadas().size());
        assertFalse(asistente.tieneMisionPendiente());
        assertFalse(asistente.getMisionesRealizadas().get(0).getInforme().isExitosa());
        assertEquals(1, vista.resultados.informesVistos.size(), "El informe de la misión cancelada llega al historial.");
        assertTrue(vista.curso.errores.isEmpty());
    }

    @Test
    @DisplayName("Las excepciones del hilo de la misión se informan en la vista y se puede reintentar")
    public void rechazoEnElHiloSeInforma() throws Exception {
        ControladorControl control = crearControl(0);
        asistente.consumirRecursos(0, asistente.getEnergia() - 1, 0);
        assertTrue(asistente.naveListaParaOperar());
        vista.select.seleccionar("M-02");

        vista.select.iniciar();

        String fin = vista.curso.finalizaciones.poll(ESPERA_SEGUNDOS, TimeUnit.SECONDS);
        assertNotNull(fin);
        assertTrue(fin.startsWith("Misión rechazada: "), fin);
        assertEquals(List.of(fin), vista.curso.errores, "El rechazo se muestra como error.");
        assertFalse(vista.curso.hilosDeAviso.contains(Thread.currentThread()), "Lo avisa el hilo de la misión.");
        assertFalse(control.hayMisionEnCurso());
        assertTrue(asistente.tieneMisionPendiente(), "La misión rechazada queda pendiente (Escenario B).");

        // Al reintentar, la misión rechazada se cancela y se encomienda una nueva.
        vista.curso.controlador.actionPerformed(evento(IVistaCursoMision.COMANDO_VOLVER));
        vista.select.iniciar();
        assertNotNull(vista.curso.finalizaciones.poll(ESPERA_SEGUNDOS, TimeUnit.SECONDS));
        assertEquals(List.of("M-02", "M-02"), vista.curso.iniciadas);
    }
}
