/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.nave;

/**
 *
 * @author Sebastian
 */
public class Combate extends Nave {
    
    
    
    public Combate(Recursos recursos) {
        super(recursos);
    }

    @Override
    public String getTipo() {
        return "Combate";
    }
    
    
    
    @Override
    public String toString() {
        return super.toString();
    }
}
