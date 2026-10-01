/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

import modelo.liquidacion.Liquidacion;

/**
 * Clase abstracta que representa un tripulante de la nave.
 * Implementa la interfaz {@link Liquidacion} para el calculo de sueldos.
 *
 * @author Sebastian
 */
public abstract class Tripulante implements Liquidacion{
    private static int idAuto = 0;
    protected int id;
    protected String nombre;
    protected int antiguedad;
    protected Origenes origen;

    
    /**
     * Constructor que crea un instancia de un Tripulante.
     * 
     * @pre nombre != null y nombre no esta en blanco.
     * @pre antiguedad >= 0.
     * @pre origen != null.
     * @post Se crea el tripulante con un id unico auto-generado.
     * @post Los atributos nombre, antiguedad y origen quedan inicializados.
     * @param nombre Nombre correspondiente al tripulante.
     * @param antiguedad Años de antiguedad del tripulante.
     * @param origen Planeta de origen del tripulante.
     */
    public Tripulante(String nombre, int antiguedad, Origenes origen) {
        assert this.validarNombre(nombre) : "El nombre del tripulante debe ser valido.";
        assert this.validarAntiguedad(antiguedad) : "La antiguedad no puede ser un valor negativo.";
        assert this.validarOrigen(origen) : "El origen debe ser distinto de null.";
        
        this.id = siguienteId();
        this.nombre = nombre;
        this.antiguedad = antiguedad;
        this.origen = origen;
    }

    
    /**
     * Genera el siguiente id de Tripulante disponible.
     * 
     * @pre true (metodo interno estatico).
     * @post El id retornado es mayor que cualquier id generado previamente.
     * @post idAuto queda incrementado en 1.
     * @return siguiente id disponible.
     */
    private static int siguienteId(){
        idAuto += 1;
        return idAuto;
    }
    /**
     * Consulta el id correspondiente al Tripulante.
     * 
     * @pre true (el tripulante esta correctamente inicializado).
     * @post El valor retornado es mayor que 0.
     * @return id del Tripulante.
     */
    public int getId() {
        return id;
    }

    
    /**
     * Consulta el nombre del Tripulante.
     * 
     * @pre true (el tripulante esta correctamente inicializado).
     * @post El valor retornado es distinto de null y no esta en blanco.
     * @return Nombre del Tripulante.
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Modifica el nombre del Tripulante.
     * 
     * @pre nombre != null y nombre no esta en blanco.
     * @post El nombre del tripulante queda actualizado con el nuevo valor.
     * @param nombre nuevo nombre del Tripulante.
     */
    public void setNombre(String nombre) {
        assert this.validarNombre(nombre) : "El nombre del tripulante debe ser valido.";
        this.nombre = nombre;
    }

    

    /**
     * Consulta la antiguedad del Tripulante.
     * 
     * @pre true (el tripulante esta correctamente inicializado).
     * @post El valor retornado es mayor o igual a 0.
     * @return Antiguedad del Tripulante.
     */
    public int getAntiguedad() {
        return this.antiguedad;
    }
    /**
     * Modifica la antiguedad del Tripulante.
     * 
     * @pre antiguedad >= 0.
     * @post La antiguedad del tripulante queda actualizada con el nuevo valor.
     * @param antiguedad Nueva antiguedad del Tripulante.
     */
    public void setAntiguedad(int antiguedad) {
        assert this.validarAntiguedad(antiguedad) : "La antiguedad no puede ser un valor negativo.";
        this.antiguedad = antiguedad;
    }
    /**
     * Agrega un año a la antiguedad del Tripulante.
     * 
     * @pre true (siempre se puede incrementar la antiguedad).
     * @post La antiguedad del tripulante se incrementa en 1.
     */
    public void agregarAntiguedad() {
        this.antiguedad += 1;
    }

    
    /**
     * Consulta el origen del Tripulante.
     * 
     * @pre true (el tripulante esta correctamente inicializado).
     * @post El valor retornado es distinto de null y pertenece al enum {@link Origenes}.
     * @return Origen del Tripulante.
     */
    @Override
    public Origenes getOrigen() {
        return this.origen;
    }
    /**
     * Modifica el origen del tripulante.
     * 
     * @pre origen != null.
     * @post El origen del tripulante queda actualizado con el nuevo valor.
     * @param origen nuevo origen del Tripulante
     */
    public void setOrigen(Origenes origen) {
        assert this.validarOrigen(origen) : "El origen debe ser distinto de null.";
        this.origen = origen;
    }

    
     @Override
    public String toString() {
        return "Tripulante{" +
                    "id=" + id +
                    ", nombre=" + nombre +
                    ", antiguedad=" + antiguedad +
                    ", origen=" + origen +
                    '}';
    }
    
    
    
    /*
     * Invariantes:
     * nombre != null && !nombre.isBlank()
     * antiguedad >= 0
     * origen != null
     */
    private boolean validarNombre(String nombre) {
        return nombre != null && !nombre.isBlank();
    }
    private boolean validarAntiguedad(int antiguedad) {
        return antiguedad >= 0;
    }
    private boolean validarOrigen(Origenes origen) {
        return origen != null;
    }
}
