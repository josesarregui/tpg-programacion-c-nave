package modelo.nave;

/**
 * Interfaz de observador en el patron Observer (E2 / E3).
 * Permite a las vistas u otros componentes ser notificados en tiempo real
 * cuando se modifican los recursos o el estado de una nave.
 */
public interface ObservadorNave {

    /**
     * Se invoca cuando la nave notifica que sus recursos o su estado cambiaron.
     *
     * @param nave la nave cuyo estado fue actualizado (no nula).
     */
    void notificarCambio(Nave nave);
}
