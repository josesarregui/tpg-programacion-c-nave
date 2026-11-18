package modelo.concurrencia;

import modelo.universo.Observador;

import java.util.ArrayList;
import java.util.List;

/**
 * Observador de prueba de la {@link PlataformaDeCarga}: se suscribe solo y, por cada evento, guarda el mensaje (push)
 * y el stock que consulta en ese momento (pull), y lleva el mínimo y el máximo observados.
 *
 * Permite esperar un evento sin pausas fijas: es un monitor (synchronized, while + wait con tiempo límite y
 * notifyAll al registrar). actualizar(...) se ejecuta con el monitor de la plataforma tomado y después toma el de
 * este registro; las consultas de la prueba sólo toman el del registro, así que no hay orden de bloqueo cruzado.
 */
class RegistroDeEventos implements Observador {
    static final long LIMITE_MS = 5000;

    private final PlataformaDeCarga plataforma;
    private final List<String> mensajes = new ArrayList<>();
    private final List<Integer> stocks = new ArrayList<>();
    private int stockMinimo = Integer.MAX_VALUE;
    private int stockMaximo = Integer.MIN_VALUE;

    RegistroDeEventos(PlataformaDeCarga plataforma) {
        this.plataforma = plataforma;
        plataforma.agregarObservador(this);
    }

    @Override
    public synchronized void actualizar(String mensaje) {
        int stock = this.plataforma.getBaterias();
        this.mensajes.add(mensaje);
        this.stocks.add(stock);
        this.stockMinimo = Math.min(this.stockMinimo, stock);
        this.stockMaximo = Math.max(this.stockMaximo, stock);
        notifyAll();
    }

    /** Espera, como máximo {@link #LIMITE_MS}, a que el mensaje se haya publicado al menos {@code veces} veces. */
    synchronized boolean esperar(String mensaje, int veces) throws InterruptedException {
        long limite = System.currentTimeMillis() + LIMITE_MS;
        while (contar(mensaje) < veces) {
            long resta = limite - System.currentTimeMillis();
            if (resta <= 0) {
                return false;
            }
            wait(resta);
        }
        return true;
    }

    /** Espera, como máximo {@link #LIMITE_MS}, a que se hayan publicado al menos {@code veces} mensajes con ese texto. */
    synchronized boolean esperarQueContengan(String fragmento, int veces) throws InterruptedException {
        long limite = System.currentTimeMillis() + LIMITE_MS;
        while (contarQueContengan(fragmento) < veces) {
            long resta = limite - System.currentTimeMillis();
            if (resta <= 0) {
                return false;
            }
            wait(resta);
        }
        return true;
    }

    synchronized int contar(String mensaje) {
        int cantidad = 0;
        for (String m : this.mensajes) {
            if (m.equals(mensaje)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    synchronized int contarQueContengan(String fragmento) {
        int cantidad = 0;
        for (String m : this.mensajes) {
            if (m.contains(fragmento)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    synchronized List<String> mensajes() {
        return new ArrayList<>(this.mensajes);
    }

    synchronized int posicion(String mensaje) {
        return this.mensajes.indexOf(mensaje);
    }

    synchronized int stockAl(String mensaje) {
        return this.stocks.get(this.mensajes.indexOf(mensaje));
    }

    synchronized int stockMinimo() {
        return this.stockMinimo;
    }

    synchronized int stockMaximo() {
        return this.stockMaximo;
    }
}
