package vista;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ayuda para verificar, sin pausas fijas, que una vista Swing modifica sus componentes en el hilo de eventos (EDT).
 *
 * Bloquea el EDT, ejecuta la notificación en otro hilo (como el de la misión) y comprueba que ese hilo no espera al
 * EDT y que los componentes todavía no cambiaron. Al liberar el EDT, espera a que procese lo pendiente: recién
 * entonces la vista muestra el cambio.
 */
public final class EdtDePrueba {

    private static final long ESPERA_SEGUNDOS = 5;

    private EdtDePrueba() {
    }

    /**
     * @param notificacion acción que se ejecuta en un hilo de fondo mientras el EDT está bloqueado.
     * @param verificarSinCambios verificaciones sobre la vista mientras el EDT sigue bloqueado.
     */
    public static void conEdtBloqueado(Runnable notificacion, Runnable verificarSinCambios) throws Exception {
        CountDownLatch bloqueado = new CountDownLatch(1);
        CountDownLatch liberar = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            bloqueado.countDown();
            try {
                liberar.await(ESPERA_SEGUNDOS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        assertTrue(bloqueado.await(ESPERA_SEGUNDOS, TimeUnit.SECONDS), "El EDT quedó bloqueado.");
        try {
            Thread hilo = new Thread(notificacion, "Notificador de prueba");
            hilo.start();
            hilo.join(TimeUnit.SECONDS.toMillis(ESPERA_SEGUNDOS));
            assertFalse(hilo.isAlive(), "La notificación no espera al hilo de eventos de Swing.");
            verificarSinCambios.run();
        } finally {
            liberar.countDown();
        }
        esperarEdt();
    }

    /**
     * Espera a que el EDT procese todo lo que tenía pendiente.
     */
    public static void esperarEdt() throws Exception {
        SwingUtilities.invokeAndWait(() -> { });
    }
}
