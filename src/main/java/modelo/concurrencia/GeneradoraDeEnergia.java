package modelo.concurrencia;

/**
 * Tarea productora: representa una Generadora de Energía del universo paralelo (Enunciado, Especificación Técnica de
 * Concurrencia, punto 1). Implementa {@link Runnable} y no extiende Thread (Clase 10 - Concurrencia): separa qué hace
 * la generadora de quién la ejecuta y no gasta la herencia simple. Quien la crea la lanza con
 * {@code new Thread(generadora).start()}, nunca invocando run().
 *
 * Recibe por constructor la referencia al único monitor compartido y no tiene sincronización propia: sólo le pide a la
 * plataforma que deposite (toda la lógica de sincronización está en {@link PlataformaDeCarga}). Tampoco conoce la
 * vista ni notifica por su cuenta: los eventos los publica la plataforma, que es el sujeto observado.
 *
 * Ciclo: produce una batería (la pausa, fuera del monitor) y pide depositarla; solicitar acceso, esperar si la
 * plataforma está llena, depositar y avisar con notifyAll() ocurren dentro de
 * {@link PlataformaDeCarga#depositarBateria(String)}.
 *
 * Finalización: el hilo se detiene interrumpiéndolo. La interrupción corta la pausa o la espera con una
 * InterruptedException, o la detecta la condición del ciclo; en todos los casos se restaura y run() termina.
 */
public class GeneradoraDeEnergia implements Runnable {
    public static final int DELAY_POR_DEFECTO_MS = 3500;

    private final String idGeneradora;
    private final PlataformaDeCarga plataforma;
    private final int delayProduccionMs;

    public GeneradoraDeEnergia(String idGeneradora, PlataformaDeCarga plataforma) {
        this(idGeneradora, plataforma, DELAY_POR_DEFECTO_MS);
    }

    /**
     * @pre idGeneradora no es nulo ni vacío, plataforma no es nula y delayProduccionMs > 0 (si no,
     *      IllegalArgumentException).
     */
    public GeneradoraDeEnergia(String idGeneradora, PlataformaDeCarga plataforma, int delayProduccionMs) {
        if (idGeneradora == null || idGeneradora.isBlank()) {
            throw new IllegalArgumentException("El identificador de la generadora no puede ser vacío.");
        }
        if (plataforma == null) {
            throw new IllegalArgumentException("La plataforma de carga no puede ser nula.");
        }
        if (delayProduccionMs <= 0) {
            throw new IllegalArgumentException("El tiempo de producción debe ser mayor a 0.");
        }
        this.idGeneradora = idGeneradora.trim();
        this.plataforma = plataforma;
        this.delayProduccionMs = delayProduccionMs;
    }

    public String getIdGeneradora() {
        return this.idGeneradora;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(this.delayProduccionMs); // 1. Produce una batería, sin tener el monitor.
                this.plataforma.depositarBateria(this.idGeneradora); // 2 a 5, dentro del monitor.
            }
        } catch (InterruptedException e) {
            // Pedido de detención: se restaura la interrupción y el hilo termina al salir de run().
            Thread.currentThread().interrupt();
        }
    }
}
