package vista.concurrencia;

import java.awt.event.ActionListener;
import java.util.function.Consumer;
import modelo.concurrencia.ObservadorConcurrencia;

/**
 * Interfaz de la vista del Visor del Universo Paralelo (Concurrencia).
 * Extiende {@link ObservadorConcurrencia} para recibir directamente las
 * notificaciones de la PlataformaDeCarga (patrón Observer entre Modelo y Vista).
 *
 * Estructurada en tres secciones paralelas:
 * 1. Generadoras de Energía (productores)
 * 2. Eventos (consola central ampliada en tiempo real de la plataforma)
 * 3. Naves Consumidoras (consumidores)
 */
public interface IVistaConcurrencia extends ObservadorConcurrencia {

    String COMANDO_CREAR_GENERADORA = "CREAR_GENERADORA";
    String COMANDO_CREAR_NAVE = "CREAR_NAVE";
    String COMANDO_LIMPIAR_CONSOLA = "LIMPIAR_CONSOLA";

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
