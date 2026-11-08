package modelo.concurrencia;

/**
 * Tarea consumidora: representa una Nave Desconocida en el universo paralelo.
 * Implementa {@link Runnable} para ejecutarse en su propio hilo de control,
 * solicitando y retirando baterías de la PlataformaDeCarga para su
 * abastecimiento y aguardando en wait() si el depósito se encuentra
 * momentáneamente vacío.
 */
public class NaveConsumidora implements Runnable {
    public static final int DELAY_POR_DEFECTO_MS = 4000;

    private final String idNave;
    private final PlataformaDeCarga plataforma;
    private final int delayConsumoMs;
    private boolean activo;

    public NaveConsumidora(String idNave, PlataformaDeCarga plataforma) {
        this(idNave, plataforma, DELAY_POR_DEFECTO_MS);
    }

    public NaveConsumidora(String idNave, PlataformaDeCarga plataforma, int delayConsumoMs) {
        if (idNave == null || idNave.isBlank()) {
            throw new IllegalArgumentException("El identificador de la nave no puede ser vacío.");
        }
        if (plataforma == null) {
            throw new IllegalArgumentException("La plataforma de carga no puede ser nula.");
        }
        this.idNave = idNave.trim();
        this.plataforma = plataforma;
        this.delayConsumoMs = Math.max(100, delayConsumoMs);
        this.activo = true;
    }

    
    
    
    public void detener() {
        this.activo = false;
    }

    public String getIdNave() {
        return this.idNave;
    }

    public boolean isActivo() {
        return this.activo;
    }
    
    
    
    @Override
    public void run() {
        try {
            while(this.activo) {
                Thread.sleep(this.delayConsumoMs);

                this.plataforma.retirar(this.idNave);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

}
