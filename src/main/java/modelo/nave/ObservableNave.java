package modelo.nave;

/**
 * Interfaz para sujetos observables en el patron Observer (E2 / E3).
 * Define los métodos para suscribir, desuscribir y notificar a los observadores
 * ante modificaciones de estado de la nave.
 */
public interface ObservableNave {

    /**
     * Registra un observador.
     *
     * @param observador el observador a agregar (no nulo).
     */
    void agregarObservador(ObservadorNave observador);

    /**
     * Elimina un observador previamente registrado.
     *
     * @param observador el observador a eliminar.
     */
    void eliminarObservador(ObservadorNave observador);

    /**
     * Notifica a todos los observadores registrados que el estado de la nave ha cambiado.
     */
    void notificarObservadores();
}
