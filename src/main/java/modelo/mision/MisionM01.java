package modelo.mision;

import modelo.nave.Nave;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursosInsuficientesException;

/**
 * Esta clase representa al tipo de mision M-01
 */
public class MisionM01 extends Mision{

	/**
	 * Este es el constructor de la mision M-01, lo que hace es a la descripcion del informe inicilizarlo decribiendo que tipo de mision es y su descripcion
	 */
	public MisionM01() {
		this.informe = new InformeMision("M-01: Intercepción y asistencia");
	}
	
	/**
	 * Este metodo lo que hace es informar al asistente de comandos las necesidades que tiene la mision y los inconvenientes para realizarla mediante las excepciones 
	 */
	protected void preparar(Nave nave) throws NaveNoDisponibleException,RecursosInsuficientesException{
		if(nave == null)
			throw new NullPointerException("No tiene una nave asignada");
		if(!nave.estaListaParaOperar())
			throw new NaveNoDisponibleException("La nave no esta disponible");
		if(nave.getRecursos().getCombustible() < 4)
			throw new RecursosInsuficientesException("La nave no cuenta con combustible suficiente para la mision");
		if(nave.getRecursos().getDesgaste() > 96)
			throw new RecursosInsuficientesException("La nave tiene demasiado desgaste para afrontar la mision");
	}
	/**
	 * Este metodo busca ejecutar la mision, para ello actualiza los recursos de la nave como lo son el combustible y desgaste
	 * <b>pre: </b>Se espera que la nave ya sea valida y este lista para operar<br>
	 * <b>post: </b>Actualizara los gastos de recursos y el informe<br>
	 * @param nave != null
	 */
	protected void ejecutar(Nave nave) {
		nave.getRecursos().aumentarDesgaste(4);
		nave.getRecursos().consumirCombustible(4);
		this.informe.setRecursosConsumidos(8);
	}
	/**
	 * Este metodo evalua la ejecucion de la mision depende de lo que surje y los inconvenientes que tuviese el metodo ejecutar<br>
	 * Como en esta etapa son solo carteles, supongo que no hay problemas en la ejecucion y todos resultan exitosas, como si estuviera en un universo vacio
	 */
	protected void evaluar() {
		this.informe.setCompletada(true);
		this.informe.agregarObservaciones("Se llego al objetivo simulado y realizo la asistencia");
	}
	/**
	 * Este metodo cierra la ejecucion de la mision para ello se indica como mision exitosa
	 * Se agregan las condiciones de exito en las observaciones
	 * <b>pre: </b>Se espera que nuevamente se ingrese una nave valida la misma de la mision<br>
	 * <b>post: </b>Cambiara el informe de la mision asignandole exitosa, agrega las observaciones de una mision exitosa, carga energia a la nave como extra y prepara a la nave para realizar el salto<br>
	 * @param nave != null
	 */
	protected void cerrar(Nave nave) {
		this.informe.setExitosa(true);
		this.informe.agregarObservaciones("Se completo con los recursos suficientes");;
		nave.getRecursos().cargarEnergia(5);
		nave.getMotor().prepararSalto();;
	}
}
