package modelo.mision;

import modelo.asistente.AsistenteDeComandos;
import modelo.nave.*;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursosInsuficientesException;

/**
 * Clase abstracta que representa los conceptos generales que tendra cualquier mision<br>
 * Mediante el TEMPLATE METHOD, definire las particularidades que tiene cada mision<br>
 * <b>Invariante de Clase: </b>Descripcion != null, Descripcion != ""<br>
 * 
 */
public abstract class Mision {
	protected InformeMision informe;
    
    /**
     * TEMPLATE METHOD: Forma el ciclo completo de la misión
     * <b>pre:</b> La misión no fue ejecutada previamente
     * <b>post:</b> La misión fue preparada, ejecutada, evaluada y cerrada, En caso contrario, donde no se logre alguna de las faces se cierra automaticamente la mision
     * @param asistente, encargado de brindar los datos y mediante el cual haremos los cambios para hacer la mision. asistente != null
     */
    public final void realizarMision(AsistenteDeComandos asistente) throws NaveNoDisponibleException,RecursosInsuficientesException{
    	assert asistente != null : "No es posible ejecutar una mision sin un asistente";
    	//Por el hecho de querer realizar la mision se cierra el informe y se vera si hizo algun cambio o no, dependera de las demas etapas modificarlo
    	this.informe.setInformefinalizado(true);
        preparar(asistente.getNave());
        ejecutar(asistente.getNave());
        evaluar();
        cerrar(asistente.getNave());        
    }
    
    // Pasos del Template Method
    protected abstract void preparar(Nave nave) throws NaveNoDisponibleException, RecursosInsuficientesException;
    protected abstract void ejecutar(Nave nave);
    protected abstract void evaluar();
    protected abstract void cerrar(Nave nave);
    
    /**
     * Al ejecutarse la mision se iran realizando los cambios pertinentes en el informe, solamente se podra acceder cuando se ejecute la mision
     * @return
     */
    public InformeMision informe() {
    	return this.informe;
    }
    
}