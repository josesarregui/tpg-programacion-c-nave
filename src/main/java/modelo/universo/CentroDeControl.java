package modelo.universo;

import excepcion.NaveInexistenteException;
import excepcion.NaveYaRegistradaException;
import modelo.asistente.Asistente;
import modelo.bitacora.TipoEvento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centro de control del universo (Aclaración, R1): registra las naves listas para operar y devuelve
 * cualquiera de ellas cuando se la pide. Es el punto de entrada para quien use el sistema
 * (hoy el programa principal; en la E2, una pantalla).
 *
 * Como toda consulta u orden a una nave pasa por su asistente (R2), el centro guarda los asistentes,
 * cada uno a cargo de su nave, y nunca entrega la nave directamente.
 *
 * Pedidos del diseño:
 *  - No depende de cómo se construyen los objetos que guarda: no crea naves ni asistentes, los recibe
 *    ya construidos (la nave, por la fábrica; el asistente, por quien usa el sistema).
 *  - Guarda los asistentes por la interfaz {@link Asistente}: una nueva variante de asistente puede
 *    registrarse sin modificar esta clase (principio abierto/cerrado e inversión de dependencias).
 *
 * El sistema usa una única nave a la vez aunque haya varias registradas (R2): el centro recuerda cuál es
 * la nave en uso, que se elige con {@link #seleccionar(int)}. Elegir otra la reemplaza.
 *
 * Invariante:
 *  - asistentes != null, sin nulos y sin dos asistentes para la misma nave;
 *  - la nave en uso, si existe, está registrada.
 */
public class CentroDeControl {

    private final List<Asistente> asistentes;
    private Asistente asistenteEnUso;

    /**
     * @post El centro de control no tiene naves registradas ni nave en uso.
     */
    public CentroDeControl() {
        this.asistentes = new ArrayList<>();
        this.asistenteEnUso = null;
        assert invariante() : "Fallo invariante tras crear el centro de control.";
    }

    /**
     * Registra una nave lista para operar, junto con el asistente que la opera. "Lista para operar" significa
     * creada por la fábrica en un estado válido y con su asistente: la tripulación puede asignarse después,
     * cuando se la selecciona (Escenario A: se crean las naves y luego se tripula la elegida).
     *
     * @pre asistente != null.
     * @post La nave queda registrada al final de getAsistentes() y el registro consta en su Bitácora.
     * @throws NaveYaRegistradaException si la nave ya estaba registrada; el centro no cambia.
     */
    public void registrar(Asistente asistente) throws NaveYaRegistradaException {
        assert asistente != null : "Se debe registrar el asistente que opera la nave.";
        if (estaRegistrada(asistente.getIdNave())) {
            throw new NaveYaRegistradaException(asistente.getIdNave());
        }
        int cantidadAnterior = asistentes.size();
        asistentes.add(asistente);
        asistente.registrarEvento(TipoEvento.SISTEMA, "Nave registrada en el centro de control.");
        assert asistentes.size() == cantidadAnterior + 1 : "Fallo postcondición al registrar la nave.";
        assert invariante() : "Fallo invariante tras registrar la nave.";
    }

    /**
     * Devuelve la nave pedida, representada por el asistente que la opera.
     *
     * @return el asistente de la nave con ese id.
     * @throws NaveInexistenteException si no hay una nave registrada con ese id.
     */
    public Asistente buscar(int idNave) throws NaveInexistenteException {
        for (Asistente asistente : asistentes) {
            if (asistente.getIdNave() == idNave) {
                return asistente;
            }
        }
        throw new NaveInexistenteException(idNave);
    }

    /**
     * Elige la nave que se va a usar (R2: una única nave a la vez). Si había otra en uso, la reemplaza.
     *
     * @post getAsistenteEnUso() es el asistente de la nave con ese id y la selección consta en su Bitácora.
     * @return el asistente de la nave seleccionada.
     * @throws NaveInexistenteException si no hay una nave registrada con ese id; la nave en uso no cambia.
     */
    public Asistente seleccionar(int idNave) throws NaveInexistenteException {
        Asistente seleccionado = buscar(idNave);
        asistenteEnUso = seleccionado;
        seleccionado.registrarEvento(TipoEvento.SISTEMA, "Nave seleccionada en el centro de control: es la nave en uso.");
        assert asistenteEnUso.getIdNave() == idNave : "Fallo postcondición al seleccionar la nave.";
        assert invariante() : "Fallo invariante tras seleccionar la nave.";
        return seleccionado;
    }

    /**
     * @return true si hay una nave seleccionada para usar.
     */
    public boolean hayNaveEnUso() {
        return asistenteEnUso != null;
    }

    /**
     * @return el asistente de la nave en uso, o null si todavía no se seleccionó ninguna.
     */
    public Asistente getAsistenteEnUso() {
        return asistenteEnUso;
    }

    /**
     * @return las naves registradas, representadas por sus asistentes, en orden de registro (lista de sólo lectura).
     */
    public List<Asistente> getAsistentes() {
        return Collections.unmodifiableList(asistentes);
    }

    public int getCantidadNaves() {
        return asistentes.size();
    }

    private boolean estaRegistrada(int idNave) {
        boolean encontrada = false;
        for (Asistente asistente : asistentes) {
            encontrada = encontrada || asistente.getIdNave() == idNave;
        }
        return encontrada;
    }

    private boolean invariante() {
        boolean valido = asistentes != null;
        // Invariante de ciclo: los asistentes ya recorridos no son nulos ni repiten la nave de uno anterior.
        for (int i = 0; valido && i < asistentes.size(); i++) {
            valido = asistentes.get(i) != null;
            for (int j = 0; valido && j < i; j++) {
                valido = asistentes.get(j).getIdNave() != asistentes.get(i).getIdNave();
            }
        }
        return valido && (asistenteEnUso == null || asistentes.contains(asistenteEnUso));
    }
}
