package vista;

import vista.principal.IVistaPrincipal;
import vista.usuario.IVistaLogin;
import vista.usuario.IVistaRegistro;

/**
 * Crea las ventanas entre las que navegan los controladores (selección de vista del apunte "MVC - interfaz de
 * usuario"): el login, el registro y la ventana principal.
 *
 * Los controladores reciben esta fábrica y conocen a cada ventana sólo por su interfaz {@code IVista...}, de modo
 * que no dependen de las clases Swing concretas. La aplicación usa {@link FabricaDeVistasSwing}; las pruebas usan
 * una fábrica de vistas de prueba, sin ventanas.
 */
public interface FabricaDeVistas {

    /**
     * @post Devuelve una vista de login nueva, todavía sin mostrar.
     */
    IVistaLogin crearVistaLogin();

    /**
     * @post Devuelve una vista de registro nueva, todavía sin mostrar.
     */
    IVistaRegistro crearVistaRegistro();

    /**
     * @post Devuelve una ventana principal nueva, con sus secciones operativas, todavía sin mostrar.
     */
    IVistaPrincipal crearVistaPrincipal();
}
