package modelo.concurrencia;

/**
 * Interfaz del patron Observer.
 */
public interface ObservadorConcurrencia {

    /**
     * Notifica un nuevo evento de la plataforma de carga.
     *
     * @param mensaje          descripcion del acontecimiento.
     * @param bateriasActuales cantidad actual de baterias en la plataforma.
     */
    public void notificarEvento(String mensaje, int bateriasActuales);
}

