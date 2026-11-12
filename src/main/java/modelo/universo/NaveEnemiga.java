package modelo.universo;

import modelo.nave.Carguero;
import modelo.nave.Combate;
import modelo.nave.Exploradora;
import modelo.nave.TipoNave;

/**
 * Interfaz para naves enemigas/externas
 * para resolver combates segun el tipo de cada nave.
 */
public interface NaveEnemiga extends ElementoDelUniverso {

    ResultadoCombate enfrentarCon(Exploradora exploradora);

    ResultadoCombate enfrentarCon(Carguero carguero);

    ResultadoCombate enfrentarCon(Combate combate);

    TipoNave getTipoNave();
}
