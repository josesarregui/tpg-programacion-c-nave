/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.nave;

/**
 *
 * @author Sebastian
 */
public class NaveFactory {
    

    public Nave crearNave(String tipoDeNave) {
        assert tipoDeNave != null : "El tipo de nave no puede ser nulo.";
        
        String tipo = tipoDeNave.toLowerCase();
        assert tipo.equals("exploradora") || tipo.equals("carguero") || tipo.equals("combate") : "Tipo de nave desconocido: " + tipoDeNave;

        switch (tipo) {
            case "exploradora":
                return new Exploradora(new Recursos(60, 80, 0));
            case "carguero":
                return new Carguero(new Recursos(100, 60, 0));
            case "combate":
                return new Combate(new Recursos(80, 100, 0));
            default:
                // Inalcanzable gracias al assert anterior
                return null;
        }
    }
}
