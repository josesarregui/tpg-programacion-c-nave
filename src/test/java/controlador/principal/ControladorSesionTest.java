package controlador.principal;

import controlador.VistasDePrueba.ControlDePrueba;
import controlador.VistasDePrueba.FabricaDePrueba;
import controlador.VistasDePrueba.LoginDePrueba;
import controlador.VistasDePrueba.PrincipalDePrueba;
import controlador.usuario.ControladorLogin;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import modelo.asistente.Asistente;
import modelo.bitacora.TipoEvento;
import modelo.concurrencia.PlataformaDeCarga;
import modelo.universo.CentroDeControl;
import modelo.universo.Posicion;
import modelo.universo.Tablero;
import modelo.usuario.ServicioUsuarios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.concurrencia.IVistaConcurrencia;
import vista.principal.panelmenu.IVistaPanelMenu;
import vista.principal.panelnave.panelcrearnave.IVistaCrearNave;
import vista.usuario.IVistaLogin;

import static controlador.VistasDePrueba.evento;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Flujo completo de una sesión con vistas de prueba (sin ventanas): login, ventana principal, creación de nave y
 * tripulación, una misión en curso en su hilo, entidades del Visor en sus hilos y cierre de sesión. Al cerrar sesión
 * no queda ningún hilo vivo ni ninguna vista suscripta, y se vuelve al login.
 */
public class ControladorSesionTest {

    private static final long ESPERA_SEGUNDOS = 5;

    @Test
    @DisplayName("Sesión: del login a la principal y, al cerrar sesión, sin hilos vivos ni observadores, de vuelta al login")
    public void sesionCompleta() throws Exception {
        ServicioUsuarios servicio = new ServicioUsuarios(new ControladorPrincipalTest.UsuarioDAOMemoria());
        servicio.registrar("kirk", "enterprise");
        FabricaDePrueba fabrica = new FabricaDePrueba();

        // 1. Login -> ventana principal
        LoginDePrueba login = new LoginDePrueba("kirk", "enterprise");
        new ControladorLogin(login, servicio, new CentroDeControl(), fabrica);
        login.controlador.actionPerformed(evento(IVistaLogin.INGRESAR));
        assertTrue(login.cerrada);
        PrincipalDePrueba principal = fabrica.ultimaPrincipal();
        assertTrue(principal.arrancada);
        ActionListener menu = principal.controlador;

        // 2. Creación de nave y tripulación
        principal.nave.crear.controlador.actionPerformed(evento(IVistaCrearNave.SELECCIONAR_EXPLORADORA));
        principal.nave.crear.controlador.actionPerformed(evento(IVistaCrearNave.ACEPTAR));
        principal.tripulacion.crear.armarTripulacionCompleta();
        assertTrue(principal.controlNaveHabilitado);

        // 3. Visor: una generadora y una nave consumidora en sus hilos
        Set<Thread> antes = Thread.getAllStackTraces().keySet();
        principal.visor.controlador.actionPerformed(evento(IVistaConcurrencia.COMANDO_CREAR_GENERADORA));
        principal.visor.controlador.actionPerformed(evento(IVistaConcurrencia.COMANDO_CREAR_NAVE));
        Set<Thread> hilosVisor = hilosNuevos(antes, List.of("[G-1]", "[Nave-1]"));
        assertEquals(2, hilosVisor.size(), "La generadora y la nave corren en sus propios hilos.");

        // 4. Misión en curso, en su propio hilo (universo de 20 x 20 con la pausa del Enunciado)
        menu.actionPerformed(evento(IVistaPanelMenu.CONTROL_NAVE));
        ControlDePrueba control = principal.control;
        control.select.seleccionar("M-02");
        control.select.iniciar();
        assertTrue(control.curso.grilla.primeraNotificacion.await(ESPERA_SEGUNDOS, TimeUnit.SECONDS),
                "El tablero notifica a su vista desde el hilo de la misión.");
        Thread hiloMision = control.curso.grilla.hilosQueNotificaron.iterator().next();
        assertNotSame(Thread.currentThread(), hiloMision);

        Asistente asistente = principal.nave.detalles.getObservado();
        Tablero tablero = control.curso.grilla.getObservado();
        PlataformaDeCarga plataforma = principal.visor.getObservada();
        assertNotNull(asistente);
        assertNotNull(tablero);
        assertNotNull(plataforma);

        // 5. Cerrar sesión
        menu.actionPerformed(evento(IVistaPanelMenu.CERRAR_SESION));

        assertFalse(hiloMision.isAlive(), "El hilo de la misión terminó.");
        for (Thread hilo : hilosVisor) {
            assertFalse(hilo.isAlive(), "El hilo " + hilo.getName() + " terminó.");
        }
        String fin = control.curso.finalizaciones.poll(ESPERA_SEGUNDOS, TimeUnit.SECONDS);
        assertNotNull(fin, "El fin de la misión se informó en la vista.");

        assertNull(principal.nave.detalles.getObservado());
        assertNull(control.recursos.getObservado());
        assertNull(control.select.getObservado());
        assertNull(control.curso.getObservado());
        assertNull(control.curso.grilla.getObservado());
        assertNull(control.resultados.getObservado());
        assertNull(principal.visor.getObservada());

        // Ninguna vista recibe notificaciones después del cierre.
        int notificacionesAsistente = control.recursos.notificaciones.get() + control.select.notificaciones.get()
                + control.curso.notificaciones.get() + control.resultados.notificaciones.get()
                + principal.nave.detalles.notificaciones.get();
        int notificacionesTablero = control.curso.grilla.notificaciones.get();
        int notificacionesVisor = principal.visor.notificaciones.get();
        asistente.registrarEvento(TipoEvento.SISTEMA, "Evento posterior al cierre de sesión.");
        tablero.moverNave(new Posicion(0, 0));
        plataforma.notificarIngreso("Generadora de Energía", "G-99");
        assertEquals(notificacionesAsistente, control.recursos.notificaciones.get() + control.select.notificaciones.get()
                + control.curso.notificaciones.get() + control.resultados.notificaciones.get()
                + principal.nave.detalles.notificaciones.get());
        assertEquals(notificacionesTablero, control.curso.grilla.notificaciones.get());
        assertEquals(notificacionesVisor, principal.visor.notificaciones.get());

        // Sesión cerrada, ventana cerrada y login visible con su controlador.
        assertFalse(servicio.haySesionIniciada());
        assertTrue(principal.cerrada);
        assertEquals(1, fabrica.logins.size());
        assertTrue(fabrica.ultimoLogin().arrancada);
        assertNotNull(fabrica.ultimoLogin().controlador);
    }

    @Test
    @DisplayName("Sesión: cerrar sesión sin nave ni misión también vuelve al login")
    public void cerrarSesionSinOperar() throws Exception {
        ServicioUsuarios servicio = new ServicioUsuarios(new ControladorPrincipalTest.UsuarioDAOMemoria());
        servicio.registrar("uhura", "enterprise");
        FabricaDePrueba fabrica = new FabricaDePrueba();
        PrincipalDePrueba principal = new PrincipalDePrueba();
        new ControladorPrincipal(principal, servicio.iniciarSesion("uhura", "enterprise"), servicio,
                new CentroDeControl(), fabrica);

        principal.controlador.actionPerformed(evento(IVistaPanelMenu.CERRAR_SESION));

        assertNull(principal.visor.getObservada());
        assertFalse(servicio.haySesionIniciada());
        assertTrue(principal.cerrada);
        assertTrue(fabrica.ultimoLogin().arrancada);
    }

    /**
     * @return los hilos vivos que no estaban en {@code antes} y cuyo nombre termina con alguno de los sufijos.
     */
    private static Set<Thread> hilosNuevos(Set<Thread> antes, List<String> sufijos) {
        Set<Thread> nuevos = new HashSet<>();
        for (Thread hilo : Thread.getAllStackTraces().keySet()) {
            for (String sufijo : sufijos) {
                if (!antes.contains(hilo) && hilo.getName().endsWith(sufijo)) {
                    nuevos.add(hilo);
                }
            }
        }
        return nuevos;
    }
}
