package modelo.mision;

/**
 * Esta clase lo que hace es darme un informe completo con los datos de la mision, para que el asistente determine que datos estaran escritos en la bitacora
 */
public class InformeMision {
	/**
	 * Inicializacion, la descripcion dependera de la mision a ejecutarse. Lo demas se inicializa como una mision no completa
	 */
	private String descripcion;
    private boolean completada;
    private boolean exitosa;
    private String observaciones;
    private int recursosConsumidos;
    private boolean informefinalizado;
    
    public InformeMision(String tipoMision) {
    	this.descripcion = tipoMision;
    	this.completada = false;
    	this.exitosa = false;
    	this.observaciones = "";
    	this.recursosConsumidos = 0; 
    	this.informefinalizado = false;
    }
    
    
	public boolean isInformefinalizado() {
		return informefinalizado;
	}

	public void setInformefinalizado(boolean informefinalizado) {
		this.informefinalizado = informefinalizado;
	}

	public void setCompletada(boolean completada) {
		this.completada = completada;
	}

	public void setExitosa(boolean exitosa) {
		this.exitosa = exitosa;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public void setRecursosConsumidos(int recursosConsumidos) {
		this.recursosConsumidos = recursosConsumidos;
	}
	
	public void agregarObservaciones(String observaciones) {
		this.observaciones += " " + observaciones;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public boolean isCompletada() {
		return completada;
	}
	
	public boolean isExitosa() {
		return exitosa;
	}
	
	public String getObservaciones() {
		return observaciones;
	}
	
	public int getRecursosConsumidos() {
		return recursosConsumidos;
	}

}
