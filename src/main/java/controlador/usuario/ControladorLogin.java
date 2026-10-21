
package controlador;

import excepcion.LoginInvalidoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.usuario.ListaDeUsuarios;
import vista.IVistaLogin;
import vista.VistaRegistro;

/**
 *
 */
public class ControladorLogin implements ActionListener {
    private ListaDeUsuarios modelo;
    private IVistaLogin vista;

    
    public ControladorLogin(ListaDeUsuarios modelo,IVistaLogin vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vista.setControlador(this);
    }

  

    private void iniciarSesion() {
        String nombre = vista.getNombreUsuario();
        String contrasena = vista.getContrasena();

        if (nombre.isBlank() || contrasena.isBlank()) {
            this.vista.mostrarError("Debe completar usuario y contraseña.");
        }else{
            try {
                this.modelo.validarLogin(nombre, contrasena);
                this.vista.mostrarMensaje("Inicio de sesión correcto.");

                // abrirVentanaPrincipal();

            } catch (LoginInvalidoException ex) {
                this.vista.mostrarError(ex.getMessage());
            }
        }
    }
    
     private void abrirRegistro() {
        VistaRegistro vistaRegistro = new VistaRegistro();
        ControladorRegistro controladorRegistro = new ControladorRegistro(this.modelo,vistaRegistro);
        
        this.vista.cerrar();
        vistaRegistro.arrancar();
    }
    
     
    private void abrirVentanaPrincipal(){
        
    }
     
     
     
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (IVistaLogin.LOGIN.equals(e.getActionCommand())) {
            this.iniciarSesion();
        }else 
            if(IVistaLogin.IR_REGISTRO.equals(e.getActionCommand())) {
                this.abrirRegistro();
            }
    }
}