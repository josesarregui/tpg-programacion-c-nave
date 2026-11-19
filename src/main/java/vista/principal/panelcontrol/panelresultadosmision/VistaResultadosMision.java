package vista.principal.panelcontrol.panelresultadosmision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import modelo.asistente.Asistente;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import modelo.universo.Posicion;
import vista.principal.panelcontrol.EstiloControl;

/**
 * Historial de informes finales de misiones (Enunciado E2, Vistas, punto 4): la lista de misiones realizadas y el
 * informe completo de la que se seleccione (resultado, acciones, objetivo, recorrido, balance de recursos, estado
 * final y observaciones).
 *
 * Observa al asistente (patrón Observer, Versión 2 de la cátedra): en {@link #actualizar(String)} copia los informes
 * de las misiones realizadas (modelo pull) en el hilo que notifica y los muestra en el hilo de eventos de Swing con
 * invokeLater. El informe nuevo aparece en cuanto el asistente lo registra en la Bitácora.
 */
public class VistaResultadosMision extends JPanel implements IVistaResultadosMision {

    private final DefaultListModel<String> modeloLista;
    private final JList<String> listaInformes;
    private final JTextArea areaDetalle;
    private final JLabel lblCantidad;
    private final JButton btnVolver;

    /** Informes mostrados, en el orden de la lista. Confinado al hilo de eventos de Swing. */
    private List<InformeMision> informes;

    /**
     * Asistente observado. Se asigna antes de suscribirse y se anula después de quitarse; el hilo que notifica lo ve
     * actualizado porque el asistente sincroniza la suscripción y la notificación.
     */
    private Asistente asistente;

    public VistaResultadosMision() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout(0, 12));
        this.setBorder(new EmptyBorder(12, 24, 16, 24));
        this.informes = new ArrayList<>();

        this.lblCantidad = EstiloControl.crearEtiqueta("Sin misiones realizadas", Font.PLAIN, 13,
                new Color(186, 230, 253));
        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 2));
        encabezado.setOpaque(false);
        encabezado.add(EstiloControl.crearEtiqueta("Historial de informes finales", Font.BOLD, 20, Color.WHITE));
        encabezado.add(this.lblCantidad);
        this.add(encabezado, BorderLayout.NORTH);

        // Lista de informes
        JPanel tarjetaLista = EstiloControl.crearPanelTranslucido(EstiloControl.FONDO_TARJETA,
                EstiloControl.BORDE_TARJETA);
        tarjetaLista.setLayout(new BorderLayout(0, 8));
        tarjetaLista.add(EstiloControl.crearEncabezadoTarjeta("📋", "Misiones realizadas",
                "Seleccione una para ver su informe"), BorderLayout.NORTH);
        this.modeloLista = new DefaultListModel<>();
        this.listaInformes = new JList<>(this.modeloLista);
        this.listaInformes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listaInformes.setBackground(new Color(8, 15, 30));
        this.listaInformes.setForeground(Color.WHITE);
        this.listaInformes.setFont(new Font("Segoe UI", Font.BOLD, 13));
        this.listaInformes.addListSelectionListener(e -> mostrarSeleccionado());
        JScrollPane scrollLista = new JScrollPane(this.listaInformes);
        scrollLista.setBorder(BorderFactory.createLineBorder(new Color(56, 189, 248, 60), 1, true));
        tarjetaLista.add(scrollLista, BorderLayout.CENTER);

        // Detalle del informe
        JPanel tarjetaDetalle = EstiloControl.crearPanelTranslucido(EstiloControl.FONDO_TARJETA,
                EstiloControl.BORDE_TARJETA);
        tarjetaDetalle.setLayout(new BorderLayout(0, 8));
        tarjetaDetalle.add(EstiloControl.crearEncabezadoTarjeta("🧾", "Informe final de misión",
                "Resultado y balance de recursos"), BorderLayout.NORTH);
        this.areaDetalle = new JTextArea();
        this.areaDetalle.setEditable(false);
        this.areaDetalle.setLineWrap(true);
        this.areaDetalle.setWrapStyleWord(true);
        this.areaDetalle.setBackground(new Color(8, 15, 30));
        this.areaDetalle.setForeground(new Color(226, 232, 240));
        this.areaDetalle.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollDetalle = new JScrollPane(this.areaDetalle);
        scrollDetalle.setBorder(BorderFactory.createLineBorder(new Color(56, 189, 248, 60), 1, true));
        tarjetaDetalle.add(scrollDetalle, BorderLayout.CENTER);

        JPanel centro = new JPanel(new GridLayout(1, 2, 18, 0));
        centro.setOpaque(false);
        centro.add(tarjetaLista);
        centro.add(tarjetaDetalle);
        this.add(centro, BorderLayout.CENTER);

        this.btnVolver = EstiloControl.crearBotonEstilizado("↩ Volver", new Color(30, 58, 138, 200),
                new Color(37, 99, 235, 230), EstiloControl.CELESTE);
        this.btnVolver.setActionCommand(COMANDO_VOLVER);
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controles.setOpaque(false);
        controles.add(this.btnVolver);
        this.add(controles, BorderLayout.SOUTH);
    }

    @Override
    public void setControlador(ActionListener controlador) {
        this.btnVolver.addActionListener(controlador);
    }

    @Override
    public void observar(Asistente asistente) {
        assert asistente != null : "La vista debe observar un asistente.";
        dejarDeObservar();
        this.asistente = asistente;
        this.asistente.agregarObservador(this);
    }

    @Override
    public void dejarDeObservar() {
        Asistente observado = this.asistente;
        if (observado != null) {
            observado.quitarObservador(this);
            this.asistente = null;
        }
    }

    /**
     * Copia los informes de las misiones realizadas (pull) en el hilo que notifica y los muestra en el hilo de
     * eventos de Swing.
     *
     * @pre La vista observa un asistente (sólo ese asistente la notifica).
     */
    @Override
    public void actualizar(String mensaje) {
        Asistente observado = this.asistente;
        assert observado != null : "La vista recibió una notificación sin observar un asistente.";
        List<InformeMision> copia = new ArrayList<>();
        for (Mision mision : observado.getMisionesRealizadas()) {
            copia.add(mision.getInforme());
        }
        SwingUtilities.invokeLater(() -> mostrarInformes(copia));
    }

    @Override
    public void mostrarInformes(List<InformeMision> informes) {
        if (informes.size() == this.informes.size()) {
            return; // ningún informe nuevo: se conserva la selección actual
        }
        this.informes = new ArrayList<>(informes);
        this.modeloLista.clear();
        for (int i = 0; i < this.informes.size(); i++) {
            InformeMision informe = this.informes.get(i);
            this.modeloLista.addElement((i + 1) + ". " + informe.getMision() + " — "
                    + (informe.isExitosa() ? "EXITOSA" : "NO EXITOSA"));
        }
        int cantidad = this.informes.size();
        this.lblCantidad.setText(cantidad == 1 ? "1 misión realizada" : cantidad + " misiones realizadas");
        if (cantidad > 0) {
            this.listaInformes.setSelectedIndex(cantidad - 1);
        }
    }

    private void mostrarSeleccionado() {
        int indice = this.listaInformes.getSelectedIndex();
        this.areaDetalle.setText(indice >= 0 ? describir(this.informes.get(indice)) : "");
        this.areaDetalle.setCaretPosition(0);
    }

    private String describir(InformeMision informe) {
        StringBuilder texto = new StringBuilder();
        texto.append("Misión: ").append(informe.getMision()).append('\n');
        texto.append("Resultado: ").append(informe.isExitosa() ? "EXITOSA" : "NO EXITOSA").append('\n');
        texto.append("Objetivo: ").append(texto(informe.getObjetivo())).append('\n');
        texto.append("Casillas recorridas: ").append(Math.max(0, informe.getRuta().size() - 1)).append('\n');
        texto.append("Balance: ").append(informe.getBalance()).append('\n');
        texto.append("Consumo: combustible ").append(informe.getCombustibleConsumido())
                .append(", energía ").append(informe.getEnergiaConsumida())
                .append(", desgaste ").append(informe.getDesgasteProducido()).append('\n');
        texto.append("Estado al evaluar: combustible ").append(informe.getCombustibleFinal())
                .append(", energía ").append(informe.getEnergiaFinal())
                .append(", desgaste ").append(informe.getDesgasteFinal())
                .append(", motor ").append(informe.getEstadoMotorFinal())
                .append(", operativa: ").append(informe.isNaveOperativa() ? "sí" : "no").append('\n');
        texto.append("\nAcciones:\n");
        for (String accion : informe.getAcciones()) {
            texto.append(" • ").append(accion).append('\n');
        }
        texto.append("\nObservaciones: ").append(informe.getObservaciones()).append('\n');
        return texto.toString();
    }

    private static String texto(Posicion posicion) {
        return posicion != null ? "(" + posicion.getX() + ", " + posicion.getY() + ")" : "-";
    }

    // Getters para las pruebas del paquete (se invocan en el hilo de eventos)

    int getCantidadInformes() {
        return this.modeloLista.getSize();
    }

    String getTextoDetalle() {
        return this.areaDetalle.getText();
    }
}
