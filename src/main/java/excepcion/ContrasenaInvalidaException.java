package excepcion;

/**
 * Excepción lanzada cuando la contraseña recibida no cumple las reglas del registro (vacía o más larga de lo
 * permitido). No guarda la contraseña rechazada: un dato secreto no debe viajar dentro de una excepción.
 */
public class ContrasenaInvalidaException extends Exception {

    public ContrasenaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
