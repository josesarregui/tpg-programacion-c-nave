package modelo.bitacora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bitácora de la nave (E1-05): registra eventos relevantes, errores, cambios del Motor Warp,
 * ejecución de misiones y operaciones sobre recursos, y permite consultarlos en orden temporal.
 *
 * Invariante: no contiene eventos nulos y los conserva en el orden en que se registraron.
 */
public class Bitacora {

    // Usar la interfaz List como tipo del atributo y ArrayList para instanciarla, asegurando que se mantenga el orden de llegada de los eventos.
    private final List<Evento> eventos = new ArrayList<>();

    /**
     * Agrega un evento al final de la bitácora.
     *
     * @throws IllegalArgumentException si el evento es nulo (Observaciones: la Bitácora no acepta eventos nulos).
     * @post getEventos().size() == cantidad anterior + 1 y el último evento es el recibido.
     */
    public void registrarEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("No se puede registrar un evento nulo.");
        }
        int cantidadAnterior = eventos.size();
        eventos.add(evento);
        assert eventos.size() == cantidadAnterior + 1 : "Fallo postcondición: el evento no fue registrado.";
        assert invariante() : "Fallo invariante tras registrar un evento.";
    }

    /**
     * Si alguien intenta hacer un .add() o .clear() sobre la lista devuelta, Java lanza una excepción
     * y no permite alterar la bitácora.
     *
     * @return lista de solo lectura, en orden temporal de registro.
     */
    public List<Evento> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    private boolean invariante() {
        boolean sinNulos = true;
        // Invariante de ciclo: ninguno de los eventos ya recorridos es nulo.
        for (Evento evento : eventos) {
            sinNulos = sinNulos && evento != null;
        }
        return sinNulos;
    }
}
