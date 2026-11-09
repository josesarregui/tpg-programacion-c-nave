package vista.principal.paneltripulacion.panelcreartripulacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Origen;

/**
 * Vista para la creación y gestión de la tripulación de la nave.
 * Posee un diseño espacial translúcido (efecto glassmorphism) coherente con el resto de la aplicación.
 * Todo el diseño y las tareas visuales se resuelven en la misma clase sin subclases internas.
 */
public class VistaCrearTripulacion extends JPanel implements IVistaCrearTripulacion {

    private final JTextField txtNombre;
    private final JTextField txtAntiguedad;
    private final JTextField txtConsejos;
    private final JPanel seccionConsejos;

    private final JComboBox<Cargo> cbCargo;
    private final JComboBox<Origen> cbOrigen;

    private final JButton bAgregar;
    private final JButton bAceptar;

    private final DefaultListModel<String> modeloLista;
    private final JList<String> listaTripulantes;
    private final JLabel lblContador;
    private final OyenteCamposTripulacion oyenteCampos;

    public VistaCrearTripulacion() {
        this.setLayout(new BorderLayout(0, 16));
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(16, 32, 16, 32));

        // =========================
        // TITULO Y SUBTITULO
        // =========================
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setOpaque(false);

        JLabel titulo = new JLabel("ARMADO DE TRIPULACIÓN", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitulo = new JLabel("Recluta y asigna a los miembros de la tripulación a bordo", SwingConstants.CENTER);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setForeground(new Color(186, 230, 253));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelEncabezado.add(titulo);
        panelEncabezado.add(Box.createVerticalStrut(4));
        panelEncabezado.add(subtitulo);

        this.add(panelEncabezado, BorderLayout.NORTH);

        // ===================================
        // TARJETA FORMULARIO (IZQUIERDA)
        // ===================================
        JPanel cardFormulario = crearPanelTranslucido(
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardFormulario.setLayout(new BorderLayout(0, 14));
        cardFormulario.setBorder(new EmptyBorder(18, 22, 18, 22));

        // Encabezado de la tarjeta formulario
        JPanel headerFormulario = new JPanel();
        headerFormulario.setLayout(new BoxLayout(headerFormulario, BoxLayout.Y_AXIS));
        headerFormulario.setOpaque(false);

        JLabel tituloForm = new JLabel("📋 Nuevo Tripulante");
        tituloForm.setForeground(Color.WHITE);
        tituloForm.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JLabel subForm = new JLabel("Completa los datos del nuevo oficial");
        subForm.setForeground(new Color(148, 163, 184));
        subForm.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        headerFormulario.add(tituloForm);
        headerFormulario.add(Box.createVerticalStrut(2));
        headerFormulario.add(subForm);

        cardFormulario.add(headerFormulario, BorderLayout.NORTH);

        // Campos de entrada
        this.txtNombre = crearCampoTexto(15);
        this.txtAntiguedad = crearCampoTexto(5);
        this.txtConsejos = crearCampoTexto(5);
        this.txtConsejos.setText("0");

        this.cbCargo = crearComboBox();
        this.cbCargo.addItem(Cargo.CAPITAN);
        this.cbCargo.addItem(Cargo.TENIENTE);
        this.cbCargo.addItem(Cargo.ALFEREZ);
        this.cbCargo.addItem(Cargo.CONSEJERO);

        this.cbOrigen = crearComboBox();
        for (Origen origen : Origen.values()) {
            this.cbOrigen.addItem(origen);
        }

        this.bAgregar = crearBoton(
                "➕  Agregar Tripulante",
                new Color(14, 116, 144, 210),
                new Color(6, 182, 212, 240),
                new Color(56, 189, 248, 210),
                8
        );
        this.bAgregar.setPreferredSize(new Dimension(100, 40));
        this.bAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        this.bAgregar.setActionCommand(IVistaCrearTripulacion.AGREGAR);
        this.bAgregar.setEnabled(false);

        this.oyenteCampos = new OyenteCamposTripulacion(this);
        this.txtNombre.addKeyListener(this.oyenteCampos);
        this.txtAntiguedad.addKeyListener(this.oyenteCampos);
        this.txtConsejos.addKeyListener(this.oyenteCampos);

        this.seccionConsejos = crearSeccionCampo("Consejos iniciales", this.txtConsejos);
        this.seccionConsejos.setBorder(new EmptyBorder(10, 0, 0, 0));
        this.seccionConsejos.setVisible(this.cbCargo.getSelectedItem() == Cargo.CONSEJERO);

        this.cbCargo.addActionListener(e -> {
            boolean esConsejero = this.cbCargo.getSelectedItem() == Cargo.CONSEJERO;
            this.seccionConsejos.setVisible(esConsejero);
            if (this.oyenteCampos != null) {
                this.oyenteCampos.verificarCampos();
            }
            revalidate();
            repaint();
        });

        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setOpaque(false);

        panelCampos.add(crearSeccionCampo("Nombre", this.txtNombre));
        panelCampos.add(Box.createVerticalStrut(10));
        panelCampos.add(crearSeccionCampo("Rango / Cargo", this.cbCargo));
        panelCampos.add(this.seccionConsejos);
        panelCampos.add(Box.createVerticalStrut(10));
        panelCampos.add(crearSeccionCampo("Planeta de Origen", this.cbOrigen));
        panelCampos.add(Box.createVerticalStrut(10));
        panelCampos.add(crearSeccionCampo("Antigüedad (años)", this.txtAntiguedad));
        panelCampos.add(Box.createVerticalStrut(16));
        panelCampos.add(this.bAgregar);

        cardFormulario.add(panelCampos, BorderLayout.CENTER);

        // ===================================
        // TARJETA LISTA (DERECHA)
        // ===================================
        JPanel cardLista = crearPanelTranslucido(
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardLista.setLayout(new BorderLayout(0, 12));
        cardLista.setBorder(new EmptyBorder(18, 22, 18, 22));

        // Encabezado de la lista
        JPanel headerLista = new JPanel(new BorderLayout(10, 0));
        headerLista.setOpaque(false);

        JLabel tituloLista = new JLabel("👥 Tripulación Actual");
        tituloLista.setForeground(Color.WHITE);
        tituloLista.setFont(new Font("Segoe UI", Font.BOLD, 17));

        this.lblContador = new JLabel("Sin tripulantes registrados");
        this.lblContador.setForeground(new Color(56, 189, 248));
        this.lblContador.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblContador.setHorizontalAlignment(SwingConstants.RIGHT);

        headerLista.add(tituloLista, BorderLayout.WEST);
        headerLista.add(this.lblContador, BorderLayout.EAST);

        cardLista.add(headerLista, BorderLayout.NORTH);

        // Componentes de la lista
        this.modeloLista = new DefaultListModel<>();
        this.listaTripulantes = new JList<>(this.modeloLista);
        this.listaTripulantes.setOpaque(false);
        this.listaTripulantes.setSelectionBackground(new Color(14, 116, 144, 200));
        this.listaTripulantes.setSelectionForeground(Color.WHITE);
        this.listaTripulantes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.listaTripulantes.setCellRenderer(crearRendererLista());

        JScrollPane scroll = new JScrollPane(this.listaTripulantes);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setOpaque(false);

        cardLista.add(scroll, BorderLayout.CENTER);

        // =========================
        // PANEL CENTRAL
        // =========================
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 24, 0));
        panelCentral.setOpaque(false);
        panelCentral.add(cardFormulario);
        panelCentral.add(cardLista);

        this.add(panelCentral, BorderLayout.CENTER);

        // =========================
        // BOTON ACEPTAR (INFERIOR)
        // =========================
        this.bAceptar = crearBoton(
                "✓  Aceptar Tripulación",
                new Color(16, 185, 129, 215),
                new Color(34, 197, 94, 245),
                new Color(74, 222, 128, 220),
                10
        );
        this.bAceptar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        this.bAceptar.setPreferredSize(new Dimension(320, 44));
        this.bAceptar.setActionCommand(IVistaCrearTripulacion.ACEPTAR);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        panelInferior.setOpaque(false);
        panelInferior.add(this.bAceptar);

        this.add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Crea un contenedor para un campo de entrada con su etiqueta estilizada encima.
     */
    private JPanel crearSeccionCampo(String textoEtiqueta, JComponent campo) {
        JPanel seccion = new JPanel(new BorderLayout(0, 4));
        seccion.setOpaque(false);
        seccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel etiqueta = new JLabel(textoEtiqueta);
        etiqueta.setForeground(new Color(224, 242, 254));
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 13));

        seccion.add(etiqueta, BorderLayout.NORTH);
        seccion.add(campo, BorderLayout.CENTER);
        return seccion;
    }

    private JPanel crearPanelTranslucido(Color colorFondo, Color colorBorde) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));
        return panel;
    }

    private JTextField crearCampoTexto(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setOpaque(true);
        campo.setBackground(new Color(15, 23, 42, 180));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(new Color(56, 189, 248));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 116, 139, 130), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 38));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBackground(new Color(30, 41, 59, 215));
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(56, 189, 248, 230), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBackground(new Color(15, 23, 42, 180));
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 116, 139, 130), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        return campo;
    }

    private <E> JComboBox<E> crearComboBox() {
        JComboBox<E> cb = new JComboBox<>();
        cb.setOpaque(true);
        cb.setBackground(new Color(15, 23, 42, 220));
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 38));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 116, 139, 130), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 8)
        ));
        return cb;
    }

    private JButton crearBoton(String texto, Color normal, Color hover, Color borde, int paddingV) {
        JButton btn = new JButton(texto);
        btn.setOpaque(true);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                BorderFactory.createEmptyBorder(paddingV, 20, paddingV, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseListener() {
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

            @Override public void mouseClicked(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
        });
        return btn;
    }

    /**
     * Renderer estilizado para cada elemento de la lista de tripulantes sin subclases internas.
     */
    private ListCellRenderer<String> crearRendererLista() {
        DefaultListCellRenderer rendererBase = new DefaultListCellRenderer();
        return (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) rendererBase.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            String valStr = value != null ? value.toString() : "";
            String icono = "👤  ";
            if (valStr.contains("Capitán")) {
                icono = "⭐  ";
            } else if (valStr.contains("Teniente")) {
                icono = "🛡️  ";
            } else if (valStr.contains("Alférez")) {
                icono = "🚀  ";
            } else if (valStr.contains("Consejero")) {
                icono = "🔮  ";
            }
            label.setText(icono + valStr);

            if (isSelected) {
                label.setBackground(new Color(14, 116, 144, 220));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(index % 2 == 0 ? new Color(15, 23, 42, 180) : new Color(15, 23, 42, 120));
                label.setForeground(new Color(224, 242, 254));
            }
            return label;
        };
    }

    /**
     * Actualiza el texto del contador de tripulantes registrados.
     */
    private void actualizarContador() {
        int cantidad = this.modeloLista.getSize();
        if (cantidad == 0) {
            this.lblContador.setText("Sin tripulantes registrados");
        } else if (cantidad == 1) {
            this.lblContador.setText("1 tripulante registrado");
        } else {
            this.lblContador.setText(cantidad + " tripulantes registrados");
        }
    }

    @Override
    public String getNombre() {
        return this.txtNombre.getText().trim();
    }

    @Override
    public Cargo getCargo() {
        return (Cargo) this.cbCargo.getSelectedItem();
    }

    @Override
    public Origen getOrigen() {
        return (Origen) this.cbOrigen.getSelectedItem();
    }

    @Override
    public String getTextoAntiguedad() {
        return this.txtAntiguedad.getText().trim();
    }

    @Override
    public String getTextoConsejos() {
        return this.txtConsejos.getText().trim();
    }

    @Override
    public void agregarTripulanteLista(String descripcion) {
        this.modeloLista.addElement(descripcion);
        actualizarContador();
    }

    @Override
    public void limpiarCampos() {
        this.txtNombre.setText("");
        this.txtAntiguedad.setText("");
        this.txtConsejos.setText("0");
        this.cbCargo.setSelectedIndex(0);
        this.cbOrigen.setSelectedIndex(0);
        this.seccionConsejos.setVisible(this.cbCargo.getSelectedItem() == Cargo.CONSEJERO);
        this.oyenteCampos.verificarCampos();
    }

    JTextField getCampoNombre() {
        return this.txtNombre;
    }

    JTextField getCampoAntiguedad() {
        return this.txtAntiguedad;
    }

    JTextField getCampoConsejos() {
        return this.txtConsejos;
    }

    JPanel getSeccionConsejos() {
        return this.seccionConsejos;
    }

    JComboBox<Cargo> getComboCargo() {
        return this.cbCargo;
    }

    JButton getBotonAgregar() {
        return this.bAgregar;
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje
        );
    }

    @Override
    public void setControlador(ActionListener controlador) {
        this.bAgregar.addActionListener(controlador);
        this.bAceptar.addActionListener(controlador);
    }
}
