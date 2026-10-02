/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.nave;

import java.util.ArrayList;
import java.util.List;
import modelo.tripulacion.Tripulante;

/**
 *
 * @author Sebastian
 * 
 * Invariante de clase:
 *   - id > 0
 *   - recursos != null
 *   - tripulacion != null (nunca contiene elementos nulos)
 */
public abstract class Nave {
    private static int idAuto = 0;
    private int id;
    private Recursos recursos;
    private List<Tripulante> tripulacion;

    public Nave(Recursos recursos) {
        assert recursos != null : "Los recursos no pueden ser nulos.";

        this.id = siguienteId();
        this.recursos = recursos;
        this.tripulacion = new ArrayList<>();

        assert invariante();
    }

    /**
     * Invariante de clase: verifica la consistencia interna de la nave.
     * @return true si el estado interno es consistente
     */
    private boolean invariante() {
        return id > 0 && recursos != null && tripulacion != null;
    }

    
    private static int siguienteId(){
        idAuto += 1;
        return idAuto;
    }
    public int getId() {
        return id;
    }

    
    
    public Recursos getRecursos() {
        return recursos;
    }

    public List<Tripulante> getTripulacion() {
        return tripulacion;
    }

    public void agregarTripulante(Tripulante tripulante) {
        assert tripulante != null : "El tripulante no puede ser nulo.";

        this.tripulacion.add(tripulante);

        assert invariante();
    }

    public void eliminarTripulante(int idTripulante) {
        assert idTripulante > 0 : "El id del tripulante debe ser mayor a 0.";
        assert buscarTripulantePorId(idTripulante) != null : "No existe un tripulante con el id: " + idTripulante;

        this.tripulacion.removeIf(t -> t.getId() == idTripulante);

        assert invariante();
    }

    public Tripulante buscarTripulantePorId(int idTripulante) {
        assert idTripulante > 0 : "El id del tripulante debe ser mayor a 0.";

        for (Tripulante t : this.tripulacion) {
            if (t.getId() == idTripulante) {
                return t;
            }
        }
        return null;
    }

    public Tripulante buscarTripulantePorNombre(String nombre) {
        assert nombre != null && !nombre.isBlank() : "El nombre no puede ser nulo ni estar en blanco.";

        for (Tripulante t : this.tripulacion) {
            if (t.getNombre().equalsIgnoreCase(nombre)) {
                return t;
            }
        }
        return null;
    }

    public boolean contieneTripulante(Tripulante tripulante) {
        assert tripulante != null : "El tripulante no puede ser nulo.";

        return this.tripulacion.contains(tripulante);
    }

    public int getCantidadTripulantes() {
        return this.tripulacion.size();
    }
    
    public abstract String getTipo();

    @Override
    public String toString() {
        return "Nave{" 
                + "id=" + id + ", "
                + "recursos=" + recursos + ", "
                + "tripulacion=" + tripulacion + '}';
    }
}
