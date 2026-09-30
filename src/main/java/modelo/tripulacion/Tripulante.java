/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.tripulacion;

import modelo.liquidacion.Liquidacion;

/**
 *
 * @author Sebastian
 */
public abstract class Tripulante implements Liquidacion{
    private static int idAuto = 1;
    protected int id;
    protected String nombre;
    protected int antiguedad;
    protected Origenes origen;

    
    public Tripulante(String nombre, int antiguedad, Origenes origen) {
        this.id = siguienteId();
        this.nombre = nombre;
        this.antiguedad = antiguedad;
        this.origen = origen;
    }

    
    
    private static int siguienteId(){
        idAuto += 1;
        return idAuto;
    }
    public int getId() {
        return id;
    }

    
    
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
    
    @Override
    public int getAntiguedad() {
        return this.antiguedad;
    }
    public void setAntiguedad(int antiguedad) {
        this.antiguedad = antiguedad;
    }
    public void agregarAntiguedad() {
        this.antiguedad += 1;
    }

    
    
    @Override
    public Origenes getOrigen() {
        return this.origen;
    }
    public void setOrigen(Origenes origen) {
        this.origen = origen;
    }

    
    @Override
    public int getConsejos() {
        return 0;
    }
    
    
    @Override
    public String toString() {
        return "Tripulante{" + "id=" + id + ", nombre=" + nombre + ", antiguedad=" + antiguedad + ", origen=" + origen + '}';
    }
}
