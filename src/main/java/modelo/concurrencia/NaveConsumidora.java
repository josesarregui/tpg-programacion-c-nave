package modelo.concurrencia;

/**
 * Tarea consumidora: representa una Nave Desconocida del universo paralelo (Enunciado, Especificación Técnica de
 * Concurrencia, punto 2). Implementa {@link Runnable} y no extiende Thread (Clase 10 - Concurrencia): separa qué hace
 * la nave de quién la ejecuta y no gasta la herencia simple. Quien la crea la lanza con
 * {@code new Thread(nave).start()}, nunca invocando run().
 *
 * Recibe por constructor la referencia al único monitor compartido y no tiene sincronización propia: sólo le pide a la
 * plataforma que le entregue una batería (toda la lógica de sincronización está en {@link PlataformaDeCarga}).
 * Tampoco conoce la vista ni notifica por su cuenta: los eventos los publica la plataforma.
 *
 * Ciclo: solicitar la batería, esperar si no hay, retirarla y avisar con notifyAll() ocurren dentro de
 * {@link PlataformaDeCarga#retirarBateria(String)}; después la nave consume la batería (la pausa, fuera del monitor).
 *
 * Finalización: el hilo se detiene interrumpiéndolo. La interrupción corta la espera o la pausa con una
 * InterruptedException, o la detecta la condición del ciclo; en todos los casos se restaura y run() termina.
 */
public class NaveConsumidora implements Runnable {
    public static final int DELAY_POR_DEFECTO_MS = 4000;

    private final String idNave;
    private final PlataformaDeCarga plataforma;
    private final int delayConsumoMs;

    public NaveConsumidora(String idNave, PlataformaDeCarga plataforma) {
        this(idNave, plataforma, DELAY_POR_DEFECTO_MS);
    }

    /**
     * @pre idNave no es nulo ni vacío, plataforma no es nula y delayConsumoMs > 0 (si no, IllegalArgumentException).
     */
    public NaveConsumidora(String idNave, PlataformaDeCarga plataforma, int delayConsumoMs) {
        if (idNave == null || idNave.isBlank()) {
            throw new IllegalArgumentException("El identificador de la nave no puede ser vacío.");
        }
        if (plataforma == null) {
            throw new IllegalArgumentException("La plataforma de carga no puede ser nula.");
        }
        if (delayConsumoMs <= 0) {
            throw new IllegalArgumentException("El tiempo de consumo debe ser mayor a 0.");
        }
        this.idNave = idNave.trim();
        this.plataforma = plataforma;
        this.delayConsumoMs = delayConsumoMs;
    }

    public String getIdNave() {
        return this.idNave;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                this.plataforma.retirarBateria(this.idNave); // 1 a 5, dentro del monitor.
                Thread.sleep(this.delayConsumoMs); // Consume la batería, sin tener el monitor.
            }
        } catch (InterruptedException e) {
            // Pedido de detención: se restaura la interrupción y el hilo termina al salir de run().
            Thread.currentThread().interrupt();
        }
    }
}
