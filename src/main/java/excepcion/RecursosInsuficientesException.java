package excepcion;

/**
 * Esta excepcion indica que los recursos para realizar la mision no son suficientes.
 * Funciona como indicador al asistente de comandos que debe realizar mantenimientos o cargar combustibles
 */
public class RecursosInsuficientesException extends Exception{

	public RecursosInsuficientesException(String mensaje) {
		super(mensaje);
	}

}
