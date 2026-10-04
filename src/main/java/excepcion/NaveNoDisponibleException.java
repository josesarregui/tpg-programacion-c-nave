package excepcion;

/**
 * Esta excepcion informa que se intenta usar la nave, pero que no esta disponible.
 * Ya sea porque no tiene el motor warp o porque no tiene tripulacion.
 * Su resolucion deberia ser asignar una tripulacion o poner el motor warp, la responsabilidad recaera en el asistente de comandos.
 */
public class NaveNoDisponibleException extends Exception{

	public NaveNoDisponibleException(String mensaje) {
		super(mensaje);
	}

}
