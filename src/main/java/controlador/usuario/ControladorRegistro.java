
package controlador;

import excepcion.ContrasenaInvalidaException;
import excepcion.UsuarioExistenteException;
import excepcion.UsuarioInvalidoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.usuario.ListaDeUsuarios;
import modelo.usuario.Usuario;
import vista.IVistaRegistro;
import vista.VistaLogin;

/**
 *
 */
public class ControladorRegistro implements ActionListener {
    private ListaDeUsuarios modelo;
    private IVistaRegistro vista;

    
    
    public ControladorRegistro(ListaDeUsuarios modelo,IVistaRegistro vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vista.setControlador(this);
    }

    
    
    private void registrarUsuario() {
        String nombre = vista.getNombreUsuario();
        String contrasena = vista.getContrasena();
        String repetir = vista.getRepetirContrasena();

        if (nombre == null || nombre.isBlank()) {
            this.vista.mostrarError("Debe ingresar un nombre de usuario.");
        }else{
            if (contrasena == null || contrasena.isBlank()) {
                this.vista.mostrarError("Debe ingresar una contraseña.");
            }else{
                if (repetir == null || repetir.isBlank()) {
                    this.vista.mostrarError("Debe repetir la contraseña.");
                }else{
                    if (!contrasena.equals(repetir)) {
                        this.vista.mostrarError("Las contraseñas no coinciden.");
                    }else{
                        try {
                            this.modelo.registrarUsuario(nombre, contrasena);
                            this.vista.mostrarMensaje("Usuario registrado correctamente.");
                            this.volverLogin();

                        } catch (UsuarioExistenteException | UsuarioInvalidoException | ContrasenaInvalidaException ex) {
                            this.vista.mostrarError(ex.getMessage());
                            
                        }
                    }
                }
            }
        }
    }

    
    
    private void volverLogin() {
        VistaLogin vistaLogin = new VistaLogin();
        ControladorLogin controladorLogin = new ControladorLogin(modelo,vistaLogin);

        this.vista.cerrar();
        vistaLogin.arrancar();
    }
    
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (IVistaRegistro.REGISTRAR.equals(e.getActionCommand())) {
            this.registrarUsuario();
        }else 
            if(IVistaRegistro.VOLVER.equals(e.getActionCommand())) {
                this.volverLogin();
            }
    }
}