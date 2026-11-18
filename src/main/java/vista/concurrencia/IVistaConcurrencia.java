package vista.concurrencia;

import java.awt.event.ActionListener;
import java.util.function.Consumer;
import modelo.concurrencia.PlataformaDeCarga;
import modelo.universo.Observador;

/**
 * Interfaz de la vista del Visor del Universo Paralelo (Concurrencia).
 * Extiende {@link Observador} (Versión 2 de la cátedra) para recibir las notificaciones de la
 * PlataformaDeCarga (patrón Observer entre Modelo y Vista).
 *
 * La vista es la responsable de vincularse con el observado (apunte de Observer): el controlador sólo le
 * entrega la plataforma con {@link #observar(PlataformaDeCarga)} y es la vista la que invoca
 * {@code plataforma.agregarObservador(this)}.
 *
 * Estructurada en tres secciones paralelas:
 * 1. Generadoras de Energía (productores)
 * 2. Eventos (consola central ampliada en tiempo real de la plataforma)
 * 3. Naves Consumidoras (consumidores)
 */
public interface IVistaConcurrencia extends Observador {

    String COMANDO_CREAR_GENERADORA = "CREAR_GENERADORA";
    String COMANDO_CREAR_NAVE = "CREAR_NAVE";
    String COMANDO_LIMPIAR_CONSOLA = "LIMPIAR_CONSOLA";

    /**
     * La vista pasa a observar la plataforma recibida: deja de observar la anterior, si la había, y se suscribe
     * con {@code plataforma.agregarObservador(this)}.
     *
     * @pre plataforma != null.
     * @post La vista observa sólo a esa plataforma y, ante cada evento, consulta su stock (modelo pull).
     */
    void observar(PlataformaDeCarga plataforma);

    /**
     * La vista se quita de la lista de observadores de la plataforma que observa. Si no observa ninguna, no
     * hace nada.
     *
     * @post La vista no recibe más eventos.
     */
    void dejarDeObservar();

    void setControlador(ActionListener controlador);

    void agregarEvento(String evento);

    void limpiarConsola();

    void actualizarBaterias(int bateriasActuales, int capacidadMaxima);

    void actualizarEntidades(int generadorasActivas, int navesActivas);

    void agregarGeneradora(String idGeneradora);

    void agregarNaveConsumidora(String idNave);

    void removerGeneradora(String idGeneradora);

    void removerNaveConsumidora(String idNave);

    void limpiarEntidades();

    void setOyenteDetenerGeneradora(Consumer<String> oyente);

    void setOyenteDetenerNave(Consumer<String> oyente);

    void mostrarError(String mensaje);
}
