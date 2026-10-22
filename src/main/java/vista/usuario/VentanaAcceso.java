package vista.usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Ventana base de las pantallas de acceso: el fondo animado, el título, los campos de usuario y contraseña y los
 * mensajes son iguales en el login y en el registro, por eso se escriben una sola vez.
 *
 * {@link #armarVentana()} es un método plantilla (patrón Template Method, apunte 04-02): arma siempre la misma
 * estructura y delega en cada subclase sus campos ({@link #crearPanelCampos()}) y sus botones
 * ({@link #crearPanelBotones()}). Cada subclase lo invoca al final de su constructor, cuando sus componentes
 * ya fueron creados.
 *
 * Los campos tienen getters de paquete para que las clases oyentes ({@code OyenteCamposLogin},
 * {@code OyenteCamposRegistro}) los consulten a través de la referencia a la ventana (apunte "Interfaces
 * gráficas", ejemplo de la ventana de login con un KeyAdapter).
 */
public abstract class VentanaAcceso extends JFrame implements IVistaUsuario {

    private static final String RUTA_FONDO = "/imagenes/FondoEspacio.gif";
    private static final String RUTA_TITULO = "/imagenes/Titulo.png";
    private static final int ANCHO_TITULO = 400;
    private static final Dimension TAMANIO_FONDO_ALTERNATIVO = new Dimension(930, 560);
    protected static final Dimension TAMANIO_CAMPO = new Dimension(250, 30);
    private static final Font FUENTE_ETIQUETA = new Font("Arial", Font.BOLD, 14);

    private final JTextField campoUsuario;
    private final JPasswordField campoContrasena;

    protected VentanaAcceso(String titulo) {
        super(titulo);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        campoUsuario = new JTextField();
        campoUsuario.setPreferredSize(TAMANIO_CAMPO);
        campoContrasena = new JPasswordField();
        campoContrasena.setPreferredSize(TAMANIO_CAMPO);
    }

    /**
     * Método plantilla: título arriba, campos al centro y botones abajo, todo centrado sobre el fondo.
     */
    protected final void armarVentana() {
        JPanel panelFormulario = new JPanel(new BorderLayout(0, 15));
        panelFormulario.setOpaque(false);
        panelFormulario.add(crearPanelCampos(), BorderLayout.CENTER);
        panelFormulario.add(crearPanelBotones(), BorderLayout.SOUTH);

        JPanel panelCentral = new JPanel(new BorderLayout(0, 20));
        panelCentral.setOpaque(false);
        panelCentral.add(crearTitulo(), BorderLayout.NORTH);
        panelCentral.add(panelFormulario, BorderLayout.CENTER);

        JLabel fondo = crearFondo();
        fondo.setLayout(new GridBagLayout()); // GridBagLayout centra el panel sobre la imagen de fondo
        fondo.add(panelCentral);
        setContentPane(fondo);
    }

    /**
     * Operación primitiva: el panel con los campos propios de la pantalla.
     */
    protected abstract JPanel crearPanelCampos();

    /**
     * Operación primitiva: el panel con los botones propios de la pantalla.
     */
    protected abstract JPanel crearPanelBotones();

    /**
     * Arma un campo con su etiqueta arriba, con el estilo común de las pantallas de acceso.
     */
    protected JPanel crearCampo(String textoEtiqueta, JComponent campo) {
        JLabel etiqueta = new JLabel(textoEtiqueta);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(FUENTE_ETIQUETA);

        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    JTextField getCampoUsuario() {
        return campoUsuario;
    }

    JPasswordField getCampoContrasena() {
        return campoContrasena;
    }

    @Override
    public String getNombreUsuario() {
        return campoUsuario.getText().trim();
    }

    @Override
    public String getContrasena() {
        return new String(campoContrasena.getPassword());
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void limpiarCampos() {
        campoUsuario.setText("");
        campoContrasena.setText("");
    }

    @Override
    public void arrancar() {
        pack();                       // coloca los componentes
        setLocationRelativeTo(null);  // centra la ventana en la pantalla
        setVisible(true);             // visualiza la ventana
    }

    @Override
    public void cerrar() {
        dispose();
    }

    /**
     * Si la imagen no está en el classpath, usa un fondo negro del mismo tamaño en lugar de fallar.
     */
    private JLabel crearFondo() {
        URL rutaFondo = getClass().getResource(RUTA_FONDO);
        JLabel fondo;
        if (rutaFondo != null) {
            fondo = new JLabel(new ImageIcon(rutaFondo));
        } else {
            fondo = new JLabel();
            fondo.setOpaque(true);
            fondo.setBackground(Color.BLACK);
            fondo.setPreferredSize(TAMANIO_FONDO_ALTERNATIVO);
        }
        return fondo;
    }

    /**
     * Si la imagen del título no está en el classpath, muestra el título como texto.
     */
    private JLabel crearTitulo() {
        URL rutaTitulo = getClass().getResource(RUTA_TITULO);
        JLabel titulo;
        if (rutaTitulo != null) {
            Image imagen = new ImageIcon(rutaTitulo).getImage().getScaledInstance(ANCHO_TITULO, -1, Image.SCALE_SMOOTH);
            titulo = new JLabel(new ImageIcon(imagen));
        } else {
            titulo = new JLabel("NAVE INTERESTELAR");
            titulo.setForeground(Color.WHITE);
            titulo.setFont(new Font("Arial", Font.BOLD, 32));
        }
        titulo.setHorizontalAlignment(JLabel.CENTER);
        return titulo;
    }
}
