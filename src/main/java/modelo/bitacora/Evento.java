package modelo.bitacora;

import java.time.LocalDateTime;

/**
 * Registro inmutable de un suceso ocurrido a bordo de la nave (E1-05): cuándo sucedió,
 * de qué tipo es y qué pasó. Todos sus atributos son final y no tiene métodos modificadores,
 * de modo que un evento registrado no puede modificarse (Aclaración, R5).
 *
 * Invariante: fechaHora != null, tipo != null y descripción no nula ni vacía
 * (Observaciones: "La Bitácora no aceptará eventos nulos o vacíos").
 */
public class Evento {

    private final LocalDateTime fechaHora;  // Garantiza el orden cronológico que exige E1-05.
    private final TipoEvento tipo;
    private final String descripcion;

    /**
     * Los eventos los crea el propio sistema (el Asistente), por eso un dato faltante es un error
     * de programación: se rechaza con IllegalArgumentException, igual que Bitacora rechaza un evento nulo.
     *
     * @throws IllegalArgumentException si la fecha o el tipo son nulos, o la descripción es nula o vacía.
     * @post El evento queda con los datos recibidos y no puede modificarse.
     */
    public Evento(LocalDateTime fechaHora, TipoEvento tipo, String descripcion) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("El evento debe indicar cuándo sucedió.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El evento debe indicar su tipo.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("El evento debe indicar qué pasó: la descripción no puede estar vacía.");
        }
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.descripcion = descripcion;
        assert invariante() : "Fallo invariante: el evento quedó incompleto.";
    }

    /**
     * Constructor de conveniencia que asigna automáticamente el instante actual.
     *
     * @throws IllegalArgumentException si el tipo es nulo o la descripción es nula o vacía.
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

    private boolean invariante() {
        return fechaHora != null && tipo != null && descripcion != null && !descripcion.isBlank();
    }

    @Override
    public String toString() {
        return "[" + fechaHora + "] [" + tipo + "] " + descripcion;
    }
}
