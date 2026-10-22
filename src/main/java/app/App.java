package app;

import controlador.usuario.ControladorLogin;
import excepcion.PersistenciaException;
import modelo.usuario.ServicioUsuarios;
import persistencia.ConexionBD;
import persistencia.UsuarioDAOJDBC;
import vista.usuario.IVistaLogin;
import vista.usuario.VistaLogin;

/**
 * Programa principal de la Entrega 2: arma las piezas del acceso al sistema y muestra la pantalla de login,
 * como el ProgramaDeConversion del apunte "MVC - interfaz de usuario" (el modelo, la vista, el control,
 * se configura la vista y arranca).
 *
 * Es el único lugar que conoce las clases concretas de cada capa: el servicio recibe el DAO por su interfaz y
 * el controlador recibe la vista por su interfaz.
 *
 * La demostración por consola de la Entrega 1 está en {@link DemostracionEntrega1}.
 */
public class App {

    public static void main(String[] args) {
        // la persistencia y el modelo:
        UsuarioDAOJDBC usuarioDAO = new UsuarioDAOJDBC(new ConexionBD());
        ServicioUsuarios modelo = new ServicioUsuarios(usuarioDAO);
        // la vista:
        IVistaLogin vista = new VistaLogin();
        try {
            usuarioDAO.crearTablaSiNoExiste();
            // el control, y se configura la vista:
            ControladorLogin controlador = new ControladorLogin(vista, modelo);
            vista.setControlador(controlador);
            // arranca la interfaz:
            vista.arrancar();
        } catch (PersistenciaException e) {
            vista.mostrarError(e.getMessage() + " El sistema no puede iniciarse.");
            vista.cerrar();
        }
    }
}
