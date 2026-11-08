package modelo.concurrencia;

/**
 * Tarea productora: representa una Generadora de Energía en el universo
 * paralelo. Implementa {@link Runnable} para ejecutarse en su propio hilo de
 * control, produciendo baterías y depositándolas en la PlataformaDeCarga
 * respetando la sincronización y exclusión mutua provistas por el monitor.
 */
public class GeneradoraDeEnergia implements Runnable {
    public static final int DELAY_POR_DEFECTO_MS = 3500;

    private final String idGeneradora;
    private final PlataformaDeCarga plataforma;
    private final int delayProduccionMs;
    private boolean activo;

    public GeneradoraDeEnergia(String idGeneradora, PlataformaDeCarga plataforma) {
        this(idGeneradora, plataforma, DELAY_POR_DEFECTO_MS);
    }

    public GeneradoraDeEnergia(String idGeneradora, PlataformaDeCarga plataforma, int delayProduccionMs) {
        if (idGeneradora == null || idGeneradora.isBlank()) {
            throw new IllegalArgumentException("El identificador de la generadora no puede ser vacío.");
        }
        if (plataforma == null) {
            throw new IllegalArgumentException("La plataforma de carga no puede ser nula.");
        }
        this.idGeneradora = idGeneradora.trim();
        this.plataforma = plataforma;
        this.delayProduccionMs = Math.max(100, delayProduccionMs);
        this.activo = true;
    }


    
    
    public void detener() {
        this.activo = false;
    }

    public String getIdGeneradora() {
        return this.idGeneradora;
    }

    public boolean isActivo() {
        return this.activo;
    }

    
    
    
    @Override
    public void run() {
        try {
            while(this.activo) {
                Thread.sleep(this.delayProduccionMs);

                this.plataforma.depositar(this.idGeneradora);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
