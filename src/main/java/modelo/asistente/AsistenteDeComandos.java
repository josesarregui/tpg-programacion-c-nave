package modelo.asistente;

import modelo.mision.Mision;
import modelo.nave.*;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursosInsuficientesException;

public class AsistenteDeComandos {
	private Nave nave;
	private Mision m1;

	public AsistenteDeComandos() {
		// TODO Auto-generated constructor stub
	}
	
	public Nave getNave() {
		return this.nave;
	}
	//ESTA PENDIENTE PARA CORROBORAR, PRIMERO VER SI ESTA CORRECTO EL ARMADO DE LAS MISIONES
	public void realizarMision(AsistenteDeComandos asistente) {
		
		try {
			m1.realizarMision(this);
		}
		catch(NaveNoDisponibleException e) {
			
		}
		catch(RecursosInsuficientesException e) {
			
		}
	}

}
