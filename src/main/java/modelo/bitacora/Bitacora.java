package modelo.bitacora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bitácora de la nave (E1-05): registra eventos relevantes, errores, cambios del Motor Warp,
 * ejecución de misiones y operaciones sobre recursos, y permite consultarlos en orden temporal.
 *
 * Invariante: no contiene eventos nulos y están en orden temporal (ningún evento es anterior al que lo precede).
 */
public class Bitacora {

    // Usar la interfaz List como tipo del atributo y ArrayList para instanciarla, asegurando que se mantenga el orden de llegada de los eventos.
    private final List<Evento> eventos = new ArrayList<>();

    /**
     * Agrega un evento al final de la bitácora.
     * Los eventos los crea el propio sistema, por eso un evento nulo o fuera de orden es un error de programación
     * y se rechaza con IllegalArgumentException, sin modificar la bitácora.
     *
     * @throws IllegalArgumentException si el evento es nulo (Observaciones: la Bitácora no acepta eventos nulos)
     *                                  o si es anterior al último registrado (E1-05: los eventos se consultan en orden temporal).
     * @post getEventos().size() == cantidad anterior + 1 y el último evento es el recibido.
     */
    public void registrarEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("No se puede registrar un evento nulo.");
        }
        if (!eventos.isEmpty() && evento.getFechaHora().isBefore(eventos.get(eventos.size() - 1).getFechaHora())) {
            throw new IllegalArgumentException("No se puede registrar un evento anterior al último registrado: "
                    + "la Bitácora conserva los eventos en orden temporal.");
        }
        int cantidadAnterior = eventos.size();
        eventos.add(evento);
        assert eventos.size() == cantidadAnterior + 1 && eventos.get(cantidadAnterior) == evento
                : "Fallo postcondición: el evento no fue registrado.";
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
        boolean valido = true;
        // Invariante de ciclo: los eventos de las posiciones 0..i no son nulos y están en orden temporal.
        for (int i = 0; valido && i < eventos.size(); i++) {
            valido = eventos.get(i) != null
                    && (i == 0 || !eventos.get(i).getFechaHora().isBefore(eventos.get(i - 1).getFechaHora()));
        }
        return valido;
    }
}
