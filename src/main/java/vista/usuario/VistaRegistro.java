
package vista.usuario;

import controlador.usuario.ControladorRegistro;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 */
public class VistaRegistro extends JFrame implements IVistaRegistro {

    private JLabel txtUsuario;
    private JLabel txtContrasena;
    private JLabel txtRepetirContrasena;

    private JTextField usuario;
    private JPasswordField contrasena;
    private JPasswordField repetirContrasena;

    private JButton bRegistrar;
    private JButton bVolver;

    public VistaRegistro() {
        super("Registro de Usuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // =========================
        // GIF DE FONDO
        // =========================
        URL rutaGif = getClass().getResource("/videos/FondoInicioEspacio.gif");

        ImageIcon gif = new ImageIcon(rutaGif);
        JLabel fondo = new JLabel(gif);
        fondo.setLayout(new GridBagLayout());

        // =========================
        // TITULO
        // =========================
        URL rutaTitulo = getClass().getResource("/imagenes/Titulo.png");

        ImageIcon tituloOriginal = new ImageIcon(rutaTitulo);
        Image tituloEscalado = tituloOriginal.getImage().getScaledInstance(450, -1, Image.SCALE_SMOOTH);

        JLabel titulo = new JLabel(new ImageIcon(tituloEscalado));
        titulo.setHorizontalAlignment(JLabel.CENTER);

        // =========================
        // COMPONENTES
        // =========================
        this.txtUsuario = new JLabel("Usuario:");
        this.txtContrasena = new JLabel("Contraseña:");
        this.txtRepetirContrasena = new JLabel("Repetir contraseña:");
        this.usuario = new JTextField();
        this.contrasena = new JPasswordField();
        this.repetirContrasena = new JPasswordField();
        this.bRegistrar = new JButton("Registrar");
        this.bRegistrar.setActionCommand(IVistaRegistro.REGISTRAR);
        this.bVolver = new JButton("Volver");
        this.bVolver.setActionCommand(IVistaRegistro.VOLVER);
        
        

        // =========================
        // ESTILO LABELS
        // =========================
        
        Font fuenteLabel = new Font("Arial",Font.BOLD,14);

        this.txtUsuario.setForeground(Color.WHITE);
        this.txtContrasena.setForeground(Color.WHITE);
        this.txtRepetirContrasena.setForeground(Color.WHITE);
        this.txtUsuario.setFont(fuenteLabel);
        this.txtContrasena.setFont(fuenteLabel);
        this.txtRepetirContrasena.setFont(fuenteLabel);

        
        // =========================
        // TAMAÑO CAMPOS
        // =========================
        
        Dimension tamCampo = new Dimension(250,30);

        this.usuario.setPreferredSize(tamCampo);

        this.contrasena.setPreferredSize(tamCampo);

        this.repetirContrasena.setPreferredSize(tamCampo);

        
        // =========================
        // PANEL USUARIO
        // =========================
        
        JPanel panelUsuario = new JPanel(new BorderLayout(0,5));
        panelUsuario.setOpaque(false);

        panelUsuario.add(txtUsuario,BorderLayout.NORTH);
        panelUsuario.add(usuario,BorderLayout.CENTER);

        
        
        // =========================
        // PANEL CONTRASEÑA
        // =========================
        
        JPanel panelContrasena = new JPanel(new BorderLayout(0,5));
        panelContrasena.setOpaque(false);

        panelContrasena.add(txtContrasena,BorderLayout.NORTH);
        panelContrasena.add(contrasena,BorderLayout.CENTER);

        
        // =========================
        // PANEL REPETIR CONTRASEÑA
        // =========================
        JPanel panelRepetir = new JPanel(new BorderLayout(0,5));
        panelRepetir.setOpaque(false);

        panelRepetir.add(txtRepetirContrasena,BorderLayout.NORTH);
        panelRepetir.add(repetirContrasena,BorderLayout.CENTER);

        
        // =========================
        // PANEL CAMPOS
        // =========================
        
        JPanel panelCampos = new JPanel(new GridLayout(3,1,0,15));
        panelCampos.setOpaque(false);

        panelCampos.add(panelUsuario);
        panelCampos.add(panelContrasena);
        panelCampos.add(panelRepetir);

        
        // =========================
        // PANEL BOTONES
        // =========================
        
        JPanel panelBotones = new JPanel();

        panelBotones.setOpaque(false);
        panelBotones.add(bRegistrar);
        panelBotones.add(bVolver);

        
        
        // =========================
        // PANEL REGISTRO
        // =========================
        
        JPanel panelRegistro = new JPanel(new BorderLayout(0,15));
        panelRegistro.setOpaque(false);

        panelRegistro.add(panelCampos,BorderLayout.CENTER);
        panelRegistro.add(panelBotones,BorderLayout.SOUTH);
        
        
        // =========================
        // TITULO + REGISTRO
        // =========================
        
        JPanel panelCentral = new JPanel(new BorderLayout(0,25));
        panelCentral.setOpaque(false);

        panelCentral.add(titulo,BorderLayout.NORTH);
        panelCentral.add(panelRegistro,BorderLayout.CENTER);

       
        
        // =========================
        // AGREGAR SOBRE EL GIF
        // =========================
        
        fondo.add(panelCentral);
        this.setContentPane(fondo);
    }

    @Override
    public String getRepetirContrasena() {
        return new String(repetirContrasena.getPassword());
    }

    @Override
    public void setControlador(ControladorRegistro controlador) {
        this.bRegistrar.addActionListener(controlador);
        this.bVolver.addActionListener(controlador);
    }

    @Override
    public String getNombreUsuario() {
        return usuario.getText();
    }

    @Override
    public String getContrasena() {
        return new String(contrasena.getPassword());
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
        this.usuario.setText("");
        this.contrasena.setText("");
        this.repetirContrasena.setText("");
    }

    @Override
    public void arrancar() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void cerrar() {
        dispose();
    }
}
