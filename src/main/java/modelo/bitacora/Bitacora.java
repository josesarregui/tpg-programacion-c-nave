package modelo.bitacora;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Bitacora {

    // Usar la interfaz List como tipo del atributo y ArrayList para instanciarla, asegurando que se mantenga el orden de llegada de los eventos.
    private final List<Evento> eventos = new ArrayList<>();

    public void registrarEvento(Evento evento)
    {
        if (evento == null) {
            throw new IllegalArgumentException("No se puede registrar un evento nulo.");
        }
        eventos.add(evento);
    }


    /*Si alguien intenta hacer un .add() o .clear() sobre esa lista devuelta, Java lanza una excepción y no permite alterar la bitácora.*/

    public List<Evento> getEventos()
    {
        return Collections.unmodifiableList(eventos); // Para devolver una vista protegida de solo lectura
    }
}
