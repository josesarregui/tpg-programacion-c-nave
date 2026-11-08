package vista.principal.panelselectmision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.bitacora.Evento;
import modelo.mision.Mision;

/**
 * Panel de Seleccion de Mision y visualizacion de Bitacora.
 * Permite examinar la bitacora cronologica y seleccionar una mision para ejecutar.
 */
public class VistaSelectMision extends JPanel implements IVistaSelectMision {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Componentes de la Bitacora
    private final DefaultTableModel modeloTablaBitacora;
    private final JTable tablaBitacora;
    private final JLabel lblTotalEventos;
    private final JButton btnActualizarBitacora;

    // Componentes de la Seleccion de Mision
    private final JComboBox<String> comboMisiones;
    private final DefaultComboBoxModel<String> modeloComboMisiones;
    private final JLabel lblCodigoMision;
    private final JLabel lblNombreMision;
    private final JTextArea txtDescripcionMision;
    private final JLabel lblCostoCombustible;
    private final JLabel lblCostoEnergia;
    private final JLabel lblCostoDesgaste;
    private final JLabel lblCondicionExito;
    private final JLabel lblEstadoNave;
    private final JButton btnIniciarMision;

    public VistaSelectMision() {
        this.setLayout(new BorderLayout(0, 14));
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(16, 24, 16, 24));

        // =====================================================================
        // ENCABEZADO SUPERIOR
        // =====================================================================
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setOpaque(false);

        JLabel titulo = new JLabel("CENTRO DE CONTROL Y MISIONES", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subtitulo = new JLabel(
                "Bitácora cronológica de vuelo y selección de directivas operativas",
                SwingConstants.CENTER);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setForeground(new Color(186, 230, 253));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelEncabezado.add(titulo);
        panelEncabezado.add(Box.createVerticalStrut(4));
        panelEncabezado.add(subtitulo);

        this.add(panelEncabezado, BorderLayout.NORTH);

        // =====================================================================
        // COLUMNA IZQUIERDA: BITÁCORA DE VUELO
        // =====================================================================
        JPanel cardBitacora = crearPanelTranslucido(
                new Color(15, 23, 42, 185),
                new Color(56, 189, 248, 120));
        cardBitacora.setLayout(new BorderLayout(0, 10));

        JPanel barraCabeceraBitacora = new JPanel(new BorderLayout(8, 0));
        barraCabeceraBitacora.setOpaque(false);

        barraCabeceraBitacora.add(crearEncabezadoTarjeta(
                "📜",
                "Bitácora de la Nave",
                "Registro cronológico inmutable de eventos del sistema"), BorderLayout.CENTER);

        JPanel panelAccionesBitacora = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelAccionesBitacora.setOpaque(false);

        this.lblTotalEventos = new JLabel("0 eventos");
        this.lblTotalEventos.setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.lblTotalEventos.setForeground(new Color(148, 163, 184));

        this.btnActualizarBitacora = crearBotonEstilizado(
                "🔄 Refrescar",
                new Color(30, 58, 138, 180),
                new Color(37, 99, 235, 220),
                new Color(56, 189, 248));
        this.btnActualizarBitacora.setActionCommand(COMANDO_ACTUALIZAR_BITACORA);

        panelAccionesBitacora.add(this.lblTotalEventos);
        panelAccionesBitacora.add(this.btnActualizarBitacora);

        barraCabeceraBitacora.add(panelAccionesBitacora, BorderLayout.EAST);
        cardBitacora.add(barraCabeceraBitacora, BorderLayout.NORTH);

        // Tabla de Bitácora
        String[] columnas = { "Hora", "Tipo", "Acontecimiento" };
        this.modeloTablaBitacora = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tablaBitacora = new JTable(this.modeloTablaBitacora);
        this.tablaBitacora.setOpaque(false);
        this.tablaBitacora.setBackground(new Color(15, 23, 42, 190));
        this.tablaBitacora.setForeground(Color.WHITE);
        this.tablaBitacora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.tablaBitacora.setRowHeight(26);
        this.tablaBitacora.setShowGrid(false);
        this.tablaBitacora.setSelectionBackground(new Color(14, 116, 144, 180));
        this.tablaBitacora.setSelectionForeground(Color.WHITE);
        this.tablaBitacora.getTableHeader().setReorderingAllowed(false);
        this.tablaBitacora.getTableHeader().setBackground(new Color(15, 23, 42));
        this.tablaBitacora.getTableHeader().setForeground(new Color(56, 189, 248));
        this.tablaBitacora.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Anchos de columnas
        this.tablaBitacora.getColumnModel().getColumn(0).setPreferredWidth(70);
        this.tablaBitacora.getColumnModel().getColumn(1).setPreferredWidth(85);
        this.tablaBitacora.getColumnModel().getColumn(2).setPreferredWidth(320);

        // Renderizado personalizado de celdas sin subclases
        DefaultTableCellRenderer rendererBase = new DefaultTableCellRenderer();
        this.tablaBitacora.setDefaultRenderer(Object.class, (table, value, isSelected, hasFocus, row, column) -> {
            Component c = rendererBase.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            rendererBase.setOpaque(true);
            if (isSelected) {
                rendererBase.setBackground(new Color(14, 116, 144, 200));
                rendererBase.setForeground(Color.WHITE);
            } else {
                rendererBase.setBackground(row % 2 == 0 ? new Color(15, 23, 42, 140) : new Color(30, 41, 59, 140));
                if (column == 1) {
                    rendererBase.setForeground(new Color(251, 191, 36));
                } else if (column == 0) {
                    rendererBase.setForeground(new Color(148, 163, 184));
                } else {
                    rendererBase.setForeground(new Color(241, 245, 249));
                }
            }
            rendererBase.setBorder(new EmptyBorder(2, 6, 2, 6));
            return c;
        });

        JScrollPane scrollBitacora = new JScrollPane(this.tablaBitacora);
        scrollBitacora.setOpaque(false);
        scrollBitacora.getViewport().setOpaque(false);
        scrollBitacora.setBorder(BorderFactory.createLineBorder(new Color(56, 189, 248, 60), 1, true));

        cardBitacora.add(scrollBitacora, BorderLayout.CENTER);

        // =====================================================================
        // COLUMNA DERECHA: SELECCIÓN DE MISIÓN
        // =====================================================================
        JPanel cardMisiones = crearPanelTranslucido(
                new Color(15, 23, 42, 185),
                new Color(56, 189, 248, 120));
        cardMisiones.setLayout(new BorderLayout(0, 12));

        cardMisiones.add(crearEncabezadoTarjeta(
                "🎯",
                "Selección de Misión",
                "Elija la directiva operativa para la ejecución táctica"), BorderLayout.NORTH);

        JPanel panelCentroMision = new JPanel();
        panelCentroMision.setLayout(new BoxLayout(panelCentroMision, BoxLayout.Y_AXIS));
        panelCentroMision.setOpaque(false);

        // Selector desplegable de misiones
        JLabel lblSelector = new JLabel("Directiva a encomendar:");
        lblSelector.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSelector.setForeground(new Color(186, 230, 253));
        lblSelector.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.modeloComboMisiones = new DefaultComboBoxModel<>();
        this.comboMisiones = new JComboBox<>(this.modeloComboMisiones);
        this.comboMisiones.setBackground(new Color(15, 23, 42));
        this.comboMisiones.setForeground(Color.WHITE);
        this.comboMisiones.setFont(new Font("Segoe UI", Font.BOLD, 13));
        this.comboMisiones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        this.comboMisiones.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.comboMisiones.setActionCommand(COMANDO_SELECCIONAR_MISION);

        DefaultListCellRenderer rendererLista = new DefaultListCellRenderer();
        this.comboMisiones.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            Component c = rendererLista.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            rendererLista.setText(value != null ? value : "");
            rendererLista.setBackground(isSelected ? new Color(14, 116, 144) : new Color(15, 23, 42));
            rendererLista.setForeground(Color.WHITE);
            rendererLista.setBorder(new EmptyBorder(4, 8, 4, 8));
            return c;
        });

        panelCentroMision.add(lblSelector);
        panelCentroMision.add(Box.createVerticalStrut(6));
        panelCentroMision.add(this.comboMisiones);
        panelCentroMision.add(Box.createVerticalStrut(12));

        // Tarjeta de detalles de la misión seleccionada
        JPanel panelDetalleMision = new JPanel();
        panelDetalleMision.setLayout(new BoxLayout(panelDetalleMision, BoxLayout.Y_AXIS));
        panelDetalleMision.setOpaque(true);
        panelDetalleMision.setBackground(new Color(8, 15, 30, 160));
        panelDetalleMision.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 189, 248, 90), 1, true),
                new EmptyBorder(12, 14, 12, 14)));
        panelDetalleMision.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filaTituloMision = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaTituloMision.setOpaque(false);
        filaTituloMision.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.lblCodigoMision = new JLabel("M-01");
        this.lblCodigoMision.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblCodigoMision.setForeground(new Color(56, 189, 248));

        this.lblNombreMision = new JLabel("Intercepción y asistencia");
        this.lblNombreMision.setFont(new Font("Segoe UI", Font.BOLD, 16));
        this.lblNombreMision.setForeground(Color.WHITE);

        filaTituloMision.add(this.lblCodigoMision);
        filaTituloMision.add(this.lblNombreMision);
        panelDetalleMision.add(filaTituloMision);
        panelDetalleMision.add(Box.createVerticalStrut(8));

        this.txtDescripcionMision = new JTextArea(3, 20);
        this.txtDescripcionMision.setWrapStyleWord(true);
        this.txtDescripcionMision.setLineWrap(true);
        this.txtDescripcionMision.setEditable(false);
        this.txtDescripcionMision.setOpaque(false);
        this.txtDescripcionMision.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.txtDescripcionMision.setForeground(new Color(203, 213, 225));
        this.txtDescripcionMision.setText("Llegar al objetivo simulado y realizar la asistencia requerida.");
        this.txtDescripcionMision.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalleMision.add(this.txtDescripcionMision);
        panelDetalleMision.add(Box.createVerticalStrut(10));

        // Requerimientos de recursos
        JPanel panelCostos = new JPanel(new GridLayout(3, 1, 0, 4));
        panelCostos.setOpaque(false);
        panelCostos.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.lblCostoCombustible = new JLabel("⛽ Combustible requerido: 4 unidades");
        this.lblCostoCombustible.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblCostoCombustible.setForeground(new Color(241, 245, 249));

        this.lblCostoEnergia = new JLabel("⚡ Energía requerida: 5 unidades");
        this.lblCostoEnergia.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblCostoEnergia.setForeground(new Color(52, 211, 153));

        this.lblCostoDesgaste = new JLabel("🔧 Desgaste estimado: +4 puntos estructurales");
        this.lblCostoDesgaste.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        this.lblCostoDesgaste.setForeground(new Color(251, 191, 36));

        panelCostos.add(this.lblCostoCombustible);
        panelCostos.add(this.lblCostoEnergia);
        panelCostos.add(this.lblCostoDesgaste);

        panelDetalleMision.add(panelCostos);
        panelDetalleMision.add(Box.createVerticalStrut(10));

        this.lblCondicionExito = new JLabel("Condición de éxito: asistencia completada con recursos suficientes.");
        this.lblCondicionExito.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        this.lblCondicionExito.setForeground(new Color(148, 163, 184));
        this.lblCondicionExito.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalleMision.add(this.lblCondicionExito);

        panelCentroMision.add(panelDetalleMision);
        panelCentroMision.add(Box.createVerticalStrut(12));

        // Indicador de disponibilidad operativa
        this.lblEstadoNave = new JLabel("🛡️ Estado de la nave: Verificando...");
        this.lblEstadoNave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblEstadoNave.setForeground(new Color(74, 222, 128));
        this.lblEstadoNave.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentroMision.add(this.lblEstadoNave);

        cardMisiones.add(panelCentroMision, BorderLayout.CENTER);

        // Botón inferior para seleccionar / iniciar la misión
        this.btnIniciarMision = crearBotonEstilizado(
                "Seleccionar Misión ➔",
                new Color(14, 116, 144, 220),
                new Color(6, 182, 212, 240),
                new Color(56, 189, 248));
        this.btnIniciarMision.setFont(new Font("Segoe UI", Font.BOLD, 15));
        this.btnIniciarMision.setPreferredSize(new Dimension(0, 44));
        this.btnIniciarMision.setActionCommand(COMANDO_INICIAR_MISION);

        cardMisiones.add(this.btnIniciarMision, BorderLayout.SOUTH);

        // =====================================================================
        // CONTENEDOR CENTRAL (DOS COLUMNAS)
        // =====================================================================
        JPanel panelContenido = new JPanel(new GridLayout(1, 2, 18, 0));
        panelContenido.setOpaque(false);
        panelContenido.add(cardBitacora);
        panelContenido.add(cardMisiones);

        this.add(panelContenido, BorderLayout.CENTER);
    }

    // =========================================================================
    // MÉTODOS DE LA INTERFAZ IVISTASELECTMISION
    // =========================================================================

    @Override
    public void setControlador(ActionListener controlador) {
        this.btnActualizarBitacora.addActionListener(controlador);
        this.comboMisiones.addActionListener(controlador);
        this.btnIniciarMision.addActionListener(controlador);
    }

    @Override
    public void mostrarBitacora(List<Evento> eventos) {
        this.modeloTablaBitacora.setRowCount(0);
        if (eventos == null || eventos.isEmpty()) {
            this.lblTotalEventos.setText("0 eventos");
            return;
        }

        this.lblTotalEventos.setText(eventos.size() + " eventos");
        for (Evento e : eventos) {
            String hora = e.getFechaHora() != null ? e.getFechaHora().format(FORMATO_HORA) : "--:--:--";
            String tipo = e.getTipo() != null ? e.getTipo().name() : "GENERAL";
            String desc = e.getDescripcion() != null ? e.getDescripcion() : "";
            this.modeloTablaBitacora.addRow(new Object[] { hora, tipo, desc });
        }

        // Auto-scroll al último evento
        if (this.tablaBitacora.getRowCount() > 0) {
            this.tablaBitacora.scrollRectToVisible(
                    this.tablaBitacora.getCellRect(this.tablaBitacora.getRowCount() - 1, 0, true));
        }
    }

    @Override
    public void mostrarMisionesDisponibles(List<Mision> misiones) {
        this.modeloComboMisiones.removeAllElements();
        if (misiones != null) {
            for (Mision m : misiones) {
                if (m != null) {
                    this.modeloComboMisiones.addElement(m.getCodigo());
                }
            }
        }
    }

    @Override
    public String getMisionSeleccionada() {
        return (String) this.comboMisiones.getSelectedItem();
    }

    @Override
    public void mostrarDetallesMision(Mision mision, boolean naveListaParaOperar, int combustibleActual,
            int energiaActual) {
        if (mision == null) {
            return;
        }

        this.lblCodigoMision.setText(mision.getCodigo());
        this.lblNombreMision.setText(mision.getNombre());

        int energiaReq = 0;
        String desc = "";
        String condicion = "";

        String codigo = mision.getCodigo() != null ? mision.getCodigo().trim().toUpperCase() : "";
        switch (codigo) {
            case "M-01":
                desc = "Llegar al objetivo simulado y realizar la asistencia técnica o rescate.";
                energiaReq = 5;
                condicion = "Condición de éxito: asistencia completada con recursos suficientes.";
                break;
            case "M-02":
                desc = "Llegar al punto de exploración simulado y obtener una muestra o paquete de datos estelar.";
                energiaReq = 5;
                condicion = "Condición de éxito: elemento de muestra registrado como obtenido.";
                break;
            case "M-03":
                desc = "Completar la maniobra de regreso simulado de la nave a la base central.";
                energiaReq = 0;
                condicion = "Condición de éxito: la nave finaliza en estado operativo válido (desgaste < 80).";
                break;
            default:
                desc = mision.getNombre();
                condicion = "Condición de éxito: objetivos cumplidos.";
                break;
        }

        this.txtDescripcionMision.setText(desc);
        this.lblCondicionExito.setText(condicion);

        boolean alcanzaCombustible = combustibleActual >= Mision.COMBUSTIBLE_CONSUMIDO;
        boolean alcanzaEnergia = energiaActual >= energiaReq;

        this.lblCostoCombustible.setText("⛽ Combustible requerido: " + Mision.COMBUSTIBLE_CONSUMIDO
                + " (Disponible: " + combustibleActual + ")");
        this.lblCostoCombustible
                .setForeground(alcanzaCombustible ? new Color(241, 245, 249) : new Color(248, 113, 113));

        this.lblCostoEnergia.setText("⚡ Energía requerida: " + energiaReq
                + " (Disponible: " + energiaActual + ")");
        this.lblCostoEnergia.setForeground(alcanzaEnergia ? new Color(52, 211, 153) : new Color(248, 113, 113));

        this.lblCostoDesgaste.setText("🔧 Desgaste producido: +" + Mision.DESGASTE_PRODUCIDO + " puntos estructurales");

        if (naveListaParaOperar && alcanzaCombustible && alcanzaEnergia) {
            this.lblEstadoNave.setText("🛡️ Estado: Nave lista y con recursos suficientes");
            this.lblEstadoNave.setForeground(new Color(74, 222, 128));
            habilitarIniciarMision(true);
        } else if (!naveListaParaOperar) {
            this.lblEstadoNave.setText("⚠️ Estado: La nave no está lista para operar");
            this.lblEstadoNave.setForeground(new Color(251, 191, 36));
            habilitarIniciarMision(false);
        } else {
            this.lblEstadoNave.setText("⚠️ Estado: Recursos insuficientes para esta misión");
            this.lblEstadoNave.setForeground(new Color(248, 113, 113));
            habilitarIniciarMision(false);
        }
    }

    @Override
    public void habilitarIniciarMision(boolean habilitar) {
        this.btnIniciarMision.setEnabled(habilitar);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error en Misión", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Control de Misión", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE CONSTRUCCIÓN VISUAL
    // =========================================================================

    private JPanel crearPanelTranslucido(Color colorFondo, Color colorBorde) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 1, true),
                new EmptyBorder(14, 16, 14, 16)));
        return panel;
    }

    private JPanel crearEncabezadoTarjeta(String icono, String titulo, String subtitulo) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIcono = new JLabel(icono + " ");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblIcono.setForeground(new Color(56, 189, 248));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));

        filaTitulo.add(lblIcono);
        filaTitulo.add(lblTitulo);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSub.setForeground(new Color(148, 163, 184));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(filaTitulo);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSub);
        return header;
    }

    private JButton crearBotonEstilizado(String texto, Color normal, Color hover, Color borde) {
        JButton btn = new JButton(texto);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(normal);
                }
            }
        });
        return btn;
    }

    // Getters para pruebas unitarias
    public DefaultTableModel getModeloTablaBitacora() {
        return this.modeloTablaBitacora;
    }

    public JTable getTablaBitacora() {
        return this.tablaBitacora;
    }

    public JComboBox<String> getComboMisiones() {
        return this.comboMisiones;
    }

    public JButton getBtnIniciarMision() {
        return this.btnIniciarMision;
    }

    public JButton getBtnActualizarBitacora() {
        return this.btnActualizarBitacora;
    }
}
