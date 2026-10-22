
package vista.usuario;

import controlador.usuario.ControladorLogin;
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
public class VistaLogin extends JFrame implements IVistaLogin{
    private JLabel txtUsuario;
    private JLabel txtContrasena;

    private JTextField usuario;
    private JPasswordField contrasena;

    private JButton bLogin;
    private JButton bRegistro;

    public VistaLogin() {
        super("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // =========================
        // GIF DE FONDO
        // =========================

        URL rutaGif = getClass().getResource("/videos/FondoInicioEspacio.gif");

        ImageIcon gif = new ImageIcon(rutaGif);
        JLabel fondo = new JLabel(gif);

        // GridBagLayout nos sirve para centrar
        // todo el contenido sobre el GIF
        fondo.setLayout(new GridBagLayout());


        // =========================
        // TITULO
        // =========================

        URL rutaTitulo = getClass().getResource("/imagenes/Titulo.png");
        
        ImageIcon tituloOriginal = new ImageIcon(rutaTitulo);
        Image tituloEscalado = tituloOriginal.getImage().getScaledInstance(450,-1,Image.SCALE_SMOOTH);

        JLabel titulo = new JLabel(new ImageIcon(tituloEscalado));
        titulo.setHorizontalAlignment(JLabel.CENTER);


        // =========================
        // COMPONENTES LOGIN
        // =========================

        this.txtUsuario = new JLabel("Usuario:");
        this.txtContrasena = new JLabel("Contraseña:");

        this.usuario = new JTextField();
        this.contrasena = new JPasswordField();

        this.bLogin = new JButton("Ingresar");
        this.bLogin.setActionCommand(IVistaLogin.LOGIN);

        this.bRegistro = new JButton("Registro");
        this.bRegistro.setActionCommand(IVistaLogin.IR_REGISTRO);


        // =========================
        // ESTILO LABELS
        // =========================

        this.txtUsuario.setForeground(Color.WHITE);
        this.txtContrasena.setForeground(Color.WHITE);

        Font fuenteLabel = new Font("Arial",Font.BOLD,14);

        this.txtUsuario.setFont(fuenteLabel);
        this.txtContrasena.setFont(fuenteLabel);


        // =========================
        // TAMAÑO CAMPOS
        // =========================

        Dimension tamCampo =new Dimension(250,30);
        
        this.usuario.setPreferredSize(tamCampo);
        this.contrasena.setPreferredSize(tamCampo);
        

        // =========================
        // PANEL USUARIO
        // =========================

        JPanel panelUsuario = new JPanel( new BorderLayout(0,5));
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
        // PANEL CAMPOS
        // =========================

        JPanel panelCampos =new JPanel(new GridLayout(2,1,0,15));
        panelCampos.setOpaque(false);

        panelCampos.add(panelUsuario);
        panelCampos.add(panelContrasena);


        // =========================
        // PANEL BOTONES
        // =========================

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);

        panelBotones.add(bLogin);
        panelBotones.add(bRegistro);


        // =========================
        // PANEL LOGIN
        // =========================

        JPanel panelLogin = new JPanel( new BorderLayout(0,15));
        panelLogin.setOpaque(false);

        panelLogin.add(panelCampos,BorderLayout.CENTER);
        panelLogin.add(panelBotones,BorderLayout.SOUTH);


        // =========================
        // TITULO + LOGIN
        // =========================

        JPanel panelCentral = new JPanel( new BorderLayout(0,25));
        panelCentral.setOpaque(false);

        panelCentral.add(titulo,BorderLayout.NORTH);
        panelCentral.add(panelLogin,BorderLayout.CENTER);


        // =========================
        // AGREGAR SOBRE EL GIF
        // =========================

        fondo.add(panelCentral);
        this.setContentPane(fondo);
    }

    @Override
    public void setControlador(ControladorLogin controlador){
        this.bLogin.addActionListener(controlador);
        this.bRegistro.addActionListener(controlador);
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
        JOptionPane.showMessageDialog(this,mensaje,"Error",JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this,mensaje,"Información",JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void limpiarCampos() {
        this.usuario.setText("");
        this.contrasena.setText("");
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