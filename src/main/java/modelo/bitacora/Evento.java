package modelo.bitacora;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro inmutable de un suceso ocurrido a bordo de la nave.
 */

public class Evento {

    private final LocalDateTime fechaHora;  // Garantiza el orden cronológico que exige E1-05.
    private final TipoEvento tipo;
    private final String descripcion;

    public Evento(LocalDateTime fechaHora, TipoEvento tipo, String descripcion)
    {
        // TODO: Evaluar validación de nulos (Objects.requireNonNull) en fase de blindaje
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    /**
     * Constructor de conveniencia que asigna automáticamente el instante actual.
     */

    public Evento(TipoEvento tipo, String descripcion) {
        this(LocalDateTime.now(), tipo, descripcion);
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s", fechaHora, tipo, descripcion);
    }
}