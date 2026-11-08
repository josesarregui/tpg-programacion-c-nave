package vista.principal.panelcontrol;

import vista.principal.panelselectmision.IVistaSelectMision;

/**
 * Interfaz contenedora de la seccion Control de Nave y Ejecucion de Misiones.
 * Coordina la alternancia entre las tres etapas del ciclo de mision
 * 1. Seleccion de Mision y Bitacora (SELECT_MISION)
 * 2. Mision en Curso (MISION_CURSO)
 * 3. Resultados de la Mision (RESULTADOS_MISION)
 */
public interface IVistaControl {
    public String SELECT_MISION = "SELECT_MISION";
    public String MISION_CURSO = "MISION_CURSO";
    public String RESULTADOS_MISION = "RESULTADOS_MISION";

    public void mostrarSelectMision();

    public void mostrarMisionCurso();

    public void mostrarResultadosMision();

    public IVistaSelectMision getVistaSelectMision();
}
