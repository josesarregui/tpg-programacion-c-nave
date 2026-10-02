/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.nave;

/**
 *
 * @author Sebastian
 */
public class Exploradora extends Nave {

    
    public Exploradora(Recursos recursos) {
        super(recursos);
    }
    
    
    @Override
    public String getTipo() {
        return "Exploradora";
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}
