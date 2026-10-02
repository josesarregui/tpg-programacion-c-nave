package modelo.tripulacion;

import excepcion.TripulacionInvalidaException;
import excepcion.TripulanteInexistenteException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tripulación de una nave (E1-04 y Ficha de Inicio, punto 3).
 *
 * Invariante:
 *  - tiene exactamente un/a capitán/a (la autoridad general de la operación es única);
 *  - tiene al menos TAMANIO_MINIMO integrantes (capitán/a + 4 tripulantes adicionales);
 *  - no contiene tripulantes repetidos ni nulos.
 *
 * Toda operación que violaría el invariante se rechaza con una excepción, sin modificar la tripulación.
 */
public class Tripulacion {

    public static final int TRIPULANTES_ADICIONALES_MINIMOS = 4;
    public static final int TAMANIO_MINIMO = 1 + TRIPULANTES_ADICIONALES_MINIMOS;

    private final List<Tripulante> tripulantes = new ArrayList<>();

    /**
     * Crea la tripulación a partir de una lista de tripulantes.
     * La lista se copia: cambios posteriores sobre la lista recibida no afectan a la tripulación.
     *
     * @param integrantes tripulantes que forman la tripulación.
     * @throws TripulacionInvalidaException si la lista es nula, tiene nulos o repetidos,
     *                                      no tiene exactamente un/a capitán/a o tiene menos de TAMANIO_MINIMO integrantes.
     * @post La tripulación cumple el invariante de la clase.
     */
    public Tripulacion(List<Tripulante> integrantes) throws TripulacionInvalidaException {
        if (integrantes == null) {
            throw new TripulacionInvalidaException("La lista de tripulantes no puede ser nula.", null);
        }
        for (Tripulante integrante : integrantes) {
            validarIncorporacion(integrante);
            tripulantes.add(integrante);
        }
        if (contarCapitanes() != 1) {
            throw new TripulacionInvalidaException("La tripulación debe tener un/a capitán/a.", null);
        }
        if (tripulantes.size() < TAMANIO_MINIMO) {
            throw new TripulacionInvalidaException("La tripulación debe tener un/a capitán/a y al menos "
                    + TRIPULANTES_ADICIONALES_MINIMOS + " tripulantes adicionales.", null);
        }
        assert invariante() : "Fallo invariante: la tripulación quedó inconsistente tras su creación.";
    }

    /**
     * Incorpora un tripulante a bordo.
     *
     * @throws TripulacionInvalidaException si el tripulante es nulo, ya pertenece a la tripulación o es un segundo capitán.
     * @post getCantidad() == cantidad anterior + 1.
     */
    public void incorporar(Tripulante tripulante) throws TripulacionInvalidaException {
        validarIncorporacion(tripulante);
        int cantidadAnterior = tripulantes.size();
        tripulantes.add(tripulante);
        assert tripulantes.size() == cantidadAnterior + 1 : "Fallo postcondición: el tripulante no fue incorporado.";
        assert invariante() : "Fallo invariante tras incorporar un tripulante.";
    }

    /**
     * Retira a un tripulante de la nave.
     *
     * @throws TripulacionInvalidaException si el tripulante no pertenece a la tripulación, es el/la capitán/a
     *                                      o su salida deja a la tripulación por debajo del mínimo.
     * @post getCantidad() == cantidad anterior - 1.
     */
    public void desembarcar(Tripulante tripulante) throws TripulacionInvalidaException {
        if (tripulante == null || !tripulantes.contains(tripulante)) {
            throw new TripulacionInvalidaException("El tripulante no pertenece a esta tripulación.", tripulante);
        }
        if (tripulante.getCargo() == Cargo.CAPITAN) {
            throw new TripulacionInvalidaException("No se puede desembarcar al/a la capitán/a: la nave quedaría sin mando.", tripulante);
        }
        if (tripulantes.size() - 1 < TAMANIO_MINIMO) {
            throw new TripulacionInvalidaException("No se puede desembarcar: la tripulación quedaría por debajo del mínimo de "
                    + TAMANIO_MINIMO + " integrantes.", tripulante);
        }
        int cantidadAnterior = tripulantes.size();
        tripulantes.remove(tripulante);
        assert tripulantes.size() == cantidadAnterior - 1 : "Fallo postcondición: el tripulante no fue desembarcado.";
        assert invariante() : "Fallo invariante tras desembarcar un tripulante.";
    }

    /**
     * Busca un tripulante por su id.
     *
     * @throws TripulanteInexistenteException si ningún tripulante tiene ese id.
     * @post El tripulante retornado tiene el id buscado.
     */
    public Tripulante buscarPorId(int id) throws TripulanteInexistenteException {
        Tripulante buscado = null;
        for (Tripulante tripulante : tripulantes) {
            if (tripulante.getId() == id) {
                buscado = tripulante;
            }
        }
        if (buscado == null) {
            throw new TripulanteInexistenteException(id);
        }
        return buscado;
    }

    /**
     * @post El valor retornado es distinto de null y tiene cargo CAPITAN.
     */
    public Tripulante getCapitan() {
        Tripulante capitan = null;
        for (Tripulante tripulante : tripulantes) {
            if (tripulante.getCargo() == Cargo.CAPITAN) {
                capitan = tripulante;
            }
        }
        assert capitan != null : "Fallo invariante: la tripulación no tiene capitán/a.";
        return capitan;
    }

    /**
     * @return lista de solo lectura, en orden de incorporación.
     */
    public List<Tripulante> getTripulantes() {
        return Collections.unmodifiableList(tripulantes);
    }

    /**
     * @return lista nueva con los tripulantes del cargo indicado.
     */
    public List<Tripulante> getTripulantes(Cargo cargo) {
        List<Tripulante> delCargo = new ArrayList<>();
        for (Tripulante tripulante : tripulantes) {
            if (tripulante.getCargo() == cargo) {
                delCargo.add(tripulante);
            }
        }
        return delCargo;
    }

    public int getCantidad() {
        return tripulantes.size();
    }

    private void validarIncorporacion(Tripulante tripulante) throws TripulacionInvalidaException {
        if (tripulante == null) {
            throw new TripulacionInvalidaException("No se puede incorporar un tripulante nulo.", null);
        }
        if (tripulantes.contains(tripulante)) {
            throw new TripulacionInvalidaException("El tripulante " + tripulante.getNombre() + " ya forma parte de la tripulación.", tripulante);
        }
        if (tripulante.getCargo() == Cargo.CAPITAN && contarCapitanes() > 0) {
            throw new TripulacionInvalidaException("La tripulación ya tiene un/a capitán/a.", tripulante);
        }
    }

    private int contarCapitanes() {
        int capitanes = 0;
        for (Tripulante tripulante : tripulantes) {
            if (tripulante.getCargo() == Cargo.CAPITAN) {
                capitanes++;
            }
        }
        return capitanes;
    }

    private boolean invariante() {
        boolean sinNulosNiRepetidos = true;
        // Invariante de ciclo: entre las posiciones 0..i-1 no hay nulos ni tripulantes repetidos.
        for (int i = 0; i < tripulantes.size(); i++) {
            Tripulante actual = tripulantes.get(i);
            sinNulosNiRepetidos = sinNulosNiRepetidos && actual != null && tripulantes.indexOf(actual) == i;
        }
        return sinNulosNiRepetidos && contarCapitanes() == 1 && tripulantes.size() >= TAMANIO_MINIMO;
    }
}
