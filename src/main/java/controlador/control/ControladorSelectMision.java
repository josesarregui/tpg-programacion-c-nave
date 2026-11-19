package controlador.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import modelo.asistente.Asistente;
import modelo.mision.Mision;
import modelo.mision.MisionIntercepcion;
import modelo.mision.MisionRecoleccion;
import modelo.mision.MisionRetorno;
import modelo.universo.Destino;
import modelo.universo.Tablero;
import vista.principal.panelcontrol.panelselectmision.IVistaSelectMision;

/**
 * Controlador de la pantalla de selección de misión y Bitácora (VistaSelectMision).
 *
 * Le entrega a la vista el asistente que debe observar (la Bitácora se actualiza sola, patrón Observer), muestra el
 * catálogo de misiones disponibles y, al iniciar, encomienda la misión elegida al asistente y le pide al
 * {@link ControladorControl} que la lance en su propio hilo. No permite iniciar una misión mientras otra está en
 * curso.
 *
 * Cada objeto misión se realiza una sola vez: el catálogo guarda, por código, cómo crear una misión nueva sobre el
 * tablero y el Destino recibidos, sin condicionales de tipo.
 */
public class ControladorSelectMision implements ActionListener {
    private final IVistaSelectMision vista;
    private final Asistente asistente;
    private final ControladorControl controladorControl;
    private final int pausaPorTurnoMs;
    private final Map<String, Supplier<Mision>> catalogo;

    private Mision misionSeleccionada;

    /**
     * @pre Ningún parámetro es nulo, destino fue creado sobre tablero y pausaPorTurnoMs >= 0.
     * @post La vista observa al asistente y muestra el catálogo, la Bitácora y la misión seleccionada.
     */
    public ControladorSelectMision(
            IVistaSelectMision vista,
            Asistente asistente,
            ControladorControl controladorControl,
            Tablero tablero,
            Destino destino,
            int pausaPorTurnoMs) {
        assert vista != null && asistente != null && controladorControl != null && tablero != null
                && destino != null : "La selección de misión necesita la vista, el asistente, el control y el universo.";
        assert pausaPorTurnoMs >= 0 : "La pausa por turno no puede ser negativa.";
        this.vista = vista;
        this.asistente = asistente;
        this.controladorControl = controladorControl;
        this.pausaPorTurnoMs = pausaPorTurnoMs;
        this.catalogo = crearCatalogo(tablero, destino);

        this.vista.observar(asistente);
        this.vista.mostrarMisionesDisponibles(misionesDisponibles());
        this.vista.setControlador(this);
        actualizarVista();
    }

    /**
     * Muestra la Bitácora completa y los detalles de la misión seleccionada. Lee el modelo desde el hilo de eventos,
     * por eso sólo se invoca sin misión en curso.
     *
     * @pre !controladorControl.hayMisionEnCurso().
     */
    public void actualizarVista() {
        vista.mostrarBitacora(asistente.getEventos());
        seleccionarMision();
    }

    /**
     * La vista deja de observar al asistente.
     */
    public void desuscribir() {
        vista.dejarDeObservar();
    }

    public Mision getMisionSeleccionada() {
        return misionSeleccionada;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if (IVistaSelectMision.COMANDO_SELECCIONAR_MISION.equals(comando)) {
            seleccionarMision();
        } else if (IVistaSelectMision.COMANDO_INICIAR_MISION.equals(comando)) {
            iniciarMision();
        }
    }

    private void seleccionarMision() {
        if (controladorControl.hayMisionEnCurso()) {
            return;
        }
        misionSeleccionada = crearMision(vista.getMisionSeleccionada());
        if (misionSeleccionada != null) {
            vista.mostrarDetallesMision(misionSeleccionada, asistente.naveListaParaOperar(),
                    asistente.getCombustible(), asistente.getEnergia());
        }
    }

    /**
     * Encomienda la misión seleccionada y la lanza en su propio hilo. Una misión rechazada antes queda pendiente
     * (Escenario B): se cancela para encomendar la nueva.
     */
    private void iniciarMision() {
        if (controladorControl.hayMisionEnCurso()) {
            vista.mostrarError("Ya hay una misión en curso: espere a que termine o cancélela.");
            return;
        }
        if (misionSeleccionada == null) {
            vista.mostrarError("Debe seleccionar una directiva de misión válida.");
            return;
        }
        if (!asistente.naveListaParaOperar()) {
            vista.mostrarError("La nave no se encuentra lista para operar.\n"
                    + "Verifique el estado del Motor Warp, el mantenimiento estructural y la asignación de tripulación.");
            return;
        }

        if (asistente.tieneMisionPendiente()) {
            asistente.cancelarMision();
        }
        asistente.encomendarMision(misionSeleccionada);
        // El objeto misión ya quedó encomendado: la próxima selección crea uno nuevo.
        misionSeleccionada = null;
        controladorControl.lanzarMision();
    }

    private Map<String, Supplier<Mision>> crearCatalogo(Tablero tablero, Destino destino) {
        List<Supplier<Mision>> fabricas = List.of(
                () -> new MisionIntercepcion(tablero, destino),
                () -> new MisionRecoleccion(tablero, destino),
                () -> new MisionRetorno(tablero, destino));
        Map<String, Supplier<Mision>> porCodigo = new LinkedHashMap<>();
        for (Supplier<Mision> fabrica : fabricas) {
            porCodigo.put(fabrica.get().getCodigo(), fabrica);
        }
        return porCodigo;
    }

    private List<Mision> misionesDisponibles() {
        List<Mision> misiones = new ArrayList<>();
        for (Supplier<Mision> fabrica : catalogo.values()) {
            misiones.add(fabrica.get());
        }
        return misiones;
    }

    /**
     * @return una misión nueva con ese código y la pausa por turno configurada, o null si el código no está en el
     *         catálogo.
     */
    private Mision crearMision(String codigo) {
        Supplier<Mision> fabrica = codigo != null ? catalogo.get(codigo.trim()) : null;
        Mision mision = fabrica != null ? fabrica.get() : null;
        if (mision != null) {
            mision.configurarPausaPorTurno(pausaPorTurnoMs);
        }
        return mision;
    }
}
