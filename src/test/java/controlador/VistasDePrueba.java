package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import modelo.asistente.Asistente;
import modelo.bitacora.Evento;
import modelo.concurrencia.PlataformaDeCarga;
import modelo.liquidacion.ReciboHaberes;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import modelo.nave.TipoNave;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Origen;
import modelo.universo.ElementoDelUniverso;
import modelo.universo.Observador;
import modelo.universo.Posicion;
import modelo.universo.Tablero;
import vista.FabricaDeVistas;
import vista.concurrencia.IVistaConcurrencia;
import vista.principal.IVistaPrincipal;
import vista.principal.panelcontrol.IVistaControl;
import vista.principal.panelcontrol.panelcursomision.IVistaCursoMision;
import vista.principal.panelcontrol.panelcursomision.IVistaTablero;
import vista.principal.panelcontrol.panelrecursos.IVistaRecursos;
import vista.principal.panelcontrol.panelresultadosmision.IVistaResultadosMision;
import vista.principal.panelcontrol.panelselectmision.IVistaSelectMision;
import vista.principal.panelnave.IVistaNave;
import vista.principal.panelnave.panelcrearnave.IVistaCrearNave;
import vista.principal.panelnave.paneldetallesnave.IVistaDetallesNave;
import vista.principal.paneltripulacion.IVistaTripulacion;
import vista.principal.paneltripulacion.panelcreartripulacion.IVistaCrearTripulacion;
import vista.principal.paneltripulacion.paneldetallestripulacion.IVistaDetallesTripulacion;
import vista.usuario.IVistaLogin;
import vista.usuario.IVistaRegistro;

/**
 * Vistas de prueba: implementan las interfaces {@code IVista...} sin crear ventanas Swing (como la
 * InterfazTextualConversor del apunte "MVC - interfaz de usuario": otra vista para el mismo controlador). Permiten
 * probar los controladores sin entorno gráfico.
 *
 * Las vistas observadoras imitan a las reales: se suscriben solas y, en {@code actualizar(String)}, consultan el
 * modelo en el hilo que notifica. Como ese hilo puede ser el de la misión o el de una generadora, guardan lo que
 * observan en estructuras seguras entre hilos.
 */
public final class VistasDePrueba {

    private VistasDePrueba() {
    }

    /**
     * @return un evento de acción con el comando indicado, como el que genera un botón.
     */
    public static ActionEvent evento(String comando) {
        return new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, comando);
    }

    // =====================================================================
    // Acceso al sistema
    // =====================================================================

    public static class FabricaDePrueba implements FabricaDeVistas {
        public final List<LoginDePrueba> logins = new ArrayList<>();
        public final List<RegistroDePrueba> registros = new ArrayList<>();
        public final List<PrincipalDePrueba> principales = new ArrayList<>();

        @Override
        public IVistaLogin crearVistaLogin() {
            LoginDePrueba vista = new LoginDePrueba("", "");
            logins.add(vista);
            return vista;
        }

        @Override
        public IVistaRegistro crearVistaRegistro() {
            RegistroDePrueba vista = new RegistroDePrueba("", "");
            registros.add(vista);
            return vista;
        }

        @Override
        public IVistaPrincipal crearVistaPrincipal() {
            PrincipalDePrueba vista = new PrincipalDePrueba();
            principales.add(vista);
            return vista;
        }

        public LoginDePrueba ultimoLogin() {
            return logins.get(logins.size() - 1);
        }

        public PrincipalDePrueba ultimaPrincipal() {
            return principales.get(principales.size() - 1);
        }
    }

    private abstract static class VistaUsuarioDePrueba {
        public String nombre;
        public String contrasena;
        public ActionListener controlador;
        public String ultimoError;
        public String ultimoMensaje;
        public boolean arrancada;
        public boolean cerrada;

        VistaUsuarioDePrueba(String nombre, String contrasena) {
            this.nombre = nombre;
            this.contrasena = contrasena;
        }

        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        public void arrancar() {
            arrancada = true;
        }

        public void cerrar() {
            cerrada = true;
        }

        public String getNombreUsuario() {
            return nombre;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }

        public void mostrarMensaje(String mensaje) {
            ultimoMensaje = mensaje;
        }

        public void limpiarCampos() {
            nombre = "";
            contrasena = "";
        }
    }

    public static class LoginDePrueba extends VistaUsuarioDePrueba implements IVistaLogin {
        public boolean contrasenaLimpiada;

        public LoginDePrueba(String nombre, String contrasena) {
            super(nombre, contrasena);
        }

        @Override
        public void limpiarContrasena() {
            contrasena = "";
            contrasenaLimpiada = true;
        }
    }

    public static class RegistroDePrueba extends VistaUsuarioDePrueba implements IVistaRegistro {
        public RegistroDePrueba(String nombre, String contrasena) {
            super(nombre, contrasena);
        }
    }

    // =====================================================================
    // Ventana principal
    // =====================================================================

    public static class PrincipalDePrueba implements IVistaPrincipal {
        public final NaveDePrueba nave = new NaveDePrueba();
        public final TripulacionDePrueba tripulacion = new TripulacionDePrueba();
        public final ControlDePrueba control = new ControlDePrueba();
        public final VisorDePrueba visor = new VisorDePrueba();
        public ActionListener controlador;
        public String operadorMostrado;
        public String panelActual = IVistaPrincipal.PANEL_NAVE;
        public boolean tripulacionHabilitada;
        public boolean controlNaveHabilitado;
        public String ultimoError;
        public boolean arrancada;
        public boolean cerrada;

        @Override
        public IVistaNave getPanelNave() {
            return nave;
        }

        @Override
        public IVistaTripulacion getPanelTripulacion() {
            return tripulacion;
        }

        @Override
        public IVistaControl getPanelControl() {
            return control;
        }

        @Override
        public IVistaConcurrencia getPanelVisor() {
            return visor;
        }

        @Override
        public void mostrarOperador(String nombreUsuario) {
            operadorMostrado = nombreUsuario;
        }

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public void mostrarPanel(String nombrePanel) {
            panelActual = nombrePanel;
        }

        @Override
        public void habilitarTripulacion(boolean habilitar) {
            tripulacionHabilitada = habilitar;
        }

        @Override
        public void habilitarControlNave(boolean habilitar) {
            controlNaveHabilitado = habilitar;
        }

        @Override
        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }

        @Override
        public void arrancar() {
            arrancada = true;
        }

        @Override
        public void cerrar() {
            cerrada = true;
        }
    }

    // =====================================================================
    // Sección Nave
    // =====================================================================

    public static class NaveDePrueba implements IVistaNave {
        public final CrearNaveDePrueba crear = new CrearNaveDePrueba();
        public final DetallesNaveDePrueba detalles = new DetallesNaveDePrueba();
        public boolean mostrandoDetalle;

        @Override
        public void mostrarCrearNave() {
            mostrandoDetalle = false;
        }

        @Override
        public void mostrarDetalleNave() {
            mostrandoDetalle = true;
        }

        @Override
        public IVistaCrearNave getVistaCrearNave() {
            return crear;
        }

        @Override
        public IVistaDetallesNave getVistaDetallesNave() {
            return detalles;
        }
    }

    public static class CrearNaveDePrueba implements IVistaCrearNave {
        public TipoNave tipoSeleccionado;
        public ActionListener controlador;
        public String ultimoError;

        @Override
        public TipoNave getTipoSeleccionado() {
            return tipoSeleccionado;
        }

        @Override
        public void setTipoSeleccionado(TipoNave tipo) {
            tipoSeleccionado = tipo;
        }

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }
    }

    /**
     * Base de las vistas de prueba que observan al asistente: se suscriben solas, cuentan las notificaciones y
     * registran los hilos que las notificaron. Cada subclase consulta lo que le interesa en el hilo que notifica.
     */
    public abstract static class ObservadorDelAsistente implements Observador {
        private volatile Asistente observado;
        public final AtomicInteger notificaciones = new AtomicInteger();
        public final Set<Thread> hilosQueNotificaron = ConcurrentHashMap.newKeySet();

        public void observar(Asistente asistente) {
            dejarDeObservar();
            observado = asistente;
            asistente.agregarObservador(this);
        }

        public void dejarDeObservar() {
            Asistente actual = observado;
            if (actual != null) {
                actual.quitarObservador(this);
                observado = null;
            }
        }

        public Asistente getObservado() {
            return observado;
        }

        @Override
        public final void actualizar(String mensaje) {
            hilosQueNotificaron.add(Thread.currentThread());
            notificaciones.incrementAndGet();
            alNotificar(observado, mensaje);
        }

        protected abstract void alNotificar(Asistente asistente, String mensaje);
    }

    public static class DetallesNaveDePrueba extends ObservadorDelAsistente implements IVistaDetallesNave {
        public volatile String tipoMostrado;
        public volatile int combustibleVisto = -1;

        @Override
        protected void alNotificar(Asistente asistente, String mensaje) {
            combustibleVisto = asistente.getCombustible();
        }

        @Override
        public void mostrarId(int id) {
        }

        @Override
        public void mostrarTipo(String tipo) {
            tipoMostrado = tipo;
        }

        @Override
        public void mostrarCombustible(int combustible) {
        }

        @Override
        public void mostrarEnergia(int energia) {
        }

        @Override
        public void mostrarDesgaste(int desgaste) {
        }

        @Override
        public void mostrarMantenimiento(boolean requiereMantenimiento) {
        }

        @Override
        public void mostrarEstadoMotor(String estado) {
        }

        @Override
        public void mostrarTripulacion(boolean tieneTripulacion) {
        }

        @Override
        public void mostrarEstadoOperativo(boolean listaParaOperar) {
        }
    }

    // =====================================================================
    // Sección Tripulación
    // =====================================================================

    public static class TripulacionDePrueba implements IVistaTripulacion {
        public final CrearTripulacionDePrueba crear = new CrearTripulacionDePrueba();
        public final DetallesTripulacionDePrueba detalles = new DetallesTripulacionDePrueba();
        public boolean mostrandoDetalle;

        @Override
        public void mostrarCrearTripulacion() {
            mostrandoDetalle = false;
        }

        @Override
        public void mostrarDetalleTripulacion() {
            mostrandoDetalle = true;
        }

        @Override
        public IVistaCrearTripulacion getVistaCrearTripulacion() {
            return crear;
        }

        @Override
        public IVistaDetallesTripulacion getVistaDetallesTripulacion() {
            return detalles;
        }
    }

    public static class CrearTripulacionDePrueba implements IVistaCrearTripulacion {
        public String nombre = "";
        public Cargo cargo;
        public Origen origen;
        public String antiguedad = "";
        public String consejos = "";
        public ActionListener controlador;
        public final List<String> lista = new ArrayList<>();
        public String ultimoError;
        public String ultimoMensaje;

        /**
         * Completa el formulario y presiona "Agregar".
         */
        public void agregar(String nombre, Cargo cargo, Origen origen, String antiguedad) {
            this.nombre = nombre;
            this.cargo = cargo;
            this.origen = origen;
            this.antiguedad = antiguedad;
            controlador.actionPerformed(evento(IVistaCrearTripulacion.AGREGAR));
        }

        public void aceptar() {
            controlador.actionPerformed(evento(IVistaCrearTripulacion.ACEPTAR));
        }

        /**
         * Agrega un/a capitán/a y cuatro tripulantes más (el mínimo de la tripulación) y acepta.
         */
        public void armarTripulacionCompleta() {
            agregar("James Kirk", Cargo.CAPITAN, Origen.TERRICOLA, "15");
            consejos = "2";
            agregar("Deanna Troi", Cargo.CONSEJERO, Origen.VULCANO, "8");
            consejos = "";
            agregar("Spock", Cargo.TENIENTE, Origen.VULCANO, "12");
            agregar("Hikaru Sulu", Cargo.ALFEREZ, Origen.TERRICOLA, "5");
            agregar("Pavel Chekov", Cargo.ALFEREZ, Origen.MARCIANO, "2");
            aceptar();
        }

        @Override
        public String getNombre() {
            return nombre;
        }

        @Override
        public Cargo getCargo() {
            return cargo;
        }

        @Override
        public Origen getOrigen() {
            return origen;
        }

        @Override
        public String getTextoAntiguedad() {
            return antiguedad;
        }

        @Override
        public String getTextoConsejos() {
            return consejos;
        }

        @Override
        public void agregarTripulanteLista(String descripcion) {
            lista.add(descripcion);
        }

        @Override
        public void limpiarCampos() {
            nombre = "";
            antiguedad = "";
        }

        @Override
        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }

        @Override
        public void mostrarMensaje(String mensaje) {
            ultimoMensaje = mensaje;
        }

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }
    }

    public static class DetallesTripulacionDePrueba implements IVistaDetallesTripulacion {
        public int cantidad = -1;
        public String capitan;
        public double costoNomina = -1;
        public final List<String> filas = new ArrayList<>();
        public final List<Double> sueldos = new ArrayList<>();
        public final List<ReciboHaberes> recibos = new ArrayList<>();
        public String ultimoError;

        @Override
        public void mostrarCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        @Override
        public void mostrarCapitan(String nombre) {
            capitan = nombre;
        }

        @Override
        public void mostrarCostoNomina(double totalNomina) {
            costoNomina = totalNomina;
        }

        @Override
        public void limpiarTripulantes() {
            filas.clear();
            sueldos.clear();
            recibos.clear();
        }

        @Override
        public void agregarTripulante(int id, String nombre, String cargo) {
            filas.add(id + ":" + nombre + ":" + cargo);
        }

        @Override
        public void agregarTripulante(int id, String nombre, String cargo, double sueldoTotal, ReciboHaberes recibo) {
            filas.add(id + ":" + nombre + ":" + cargo);
            sueldos.add(sueldoTotal);
            recibos.add(recibo);
        }

        @Override
        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }
    }

    // =====================================================================
    // Sección Control de la Nave
    // =====================================================================

    public static class ControlDePrueba implements IVistaControl {
        public final RecursosDePrueba recursos = new RecursosDePrueba();
        public final SelectMisionDePrueba select = new SelectMisionDePrueba();
        public final CursoMisionDePrueba curso = new CursoMisionDePrueba();
        public final ResultadosDePrueba resultados = new ResultadosDePrueba();
        public volatile String etapa = IVistaControl.SELECT_MISION;

        @Override
        public void mostrarSelectMision() {
            etapa = IVistaControl.SELECT_MISION;
        }

        @Override
        public void mostrarMisionCurso() {
            etapa = IVistaControl.MISION_CURSO;
        }

        @Override
        public void mostrarResultadosMision() {
            etapa = IVistaControl.RESULTADOS_MISION;
        }

        @Override
        public IVistaRecursos getVistaRecursos() {
            return recursos;
        }

        @Override
        public IVistaSelectMision getVistaSelectMision() {
            return select;
        }

        @Override
        public IVistaCursoMision getVistaCursoMision() {
            return curso;
        }

        @Override
        public IVistaResultadosMision getVistaResultadosMision() {
            return resultados;
        }
    }

    public static class RecursosDePrueba extends ObservadorDelAsistente implements IVistaRecursos {
        public volatile int combustibleVisto = -1;
        public volatile int energiaVista = -1;
        public volatile int desgasteVisto = -1;
        public volatile int combustibleMostrado = -1;
        public volatile boolean destruidaMostrada;

        @Override
        protected void alNotificar(Asistente asistente, String mensaje) {
            combustibleVisto = asistente.getCombustible();
            energiaVista = asistente.getEnergia();
            desgasteVisto = asistente.getDesgaste();
        }

        @Override
        public void mostrarRecursos(int combustible, int energia, int desgaste, int gemas, String estadoMotor,
                boolean listaParaOperar, boolean destruida) {
            combustibleMostrado = combustible;
            destruidaMostrada = destruida;
        }
    }

    public static class SelectMisionDePrueba extends ObservadorDelAsistente implements IVistaSelectMision {
        public volatile String codigoSeleccionado;
        public volatile int eventosVistos = -1;
        public ActionListener controlador;
        public List<Evento> eventosMostrados = new ArrayList<>();
        public List<Mision> misionesDisponibles = new ArrayList<>();
        public Mision ultimaMisionDetallada;
        public boolean naveListaDetallada;
        public int combustibleDetallado;
        public int energiaDetallada;
        public final List<String> errores = new CopyOnWriteArrayList<>();
        public String ultimoMensaje;

        @Override
        protected void alNotificar(Asistente asistente, String mensaje) {
            eventosVistos = List.copyOf(asistente.getEventos()).size();
        }

        public void seleccionar(String codigo) {
            codigoSeleccionado = codigo;
            controlador.actionPerformed(evento(IVistaSelectMision.COMANDO_SELECCIONAR_MISION));
        }

        public void iniciar() {
            controlador.actionPerformed(evento(IVistaSelectMision.COMANDO_INICIAR_MISION));
        }

        public String ultimoError() {
            return errores.isEmpty() ? null : errores.get(errores.size() - 1);
        }

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public void mostrarBitacora(List<Evento> eventos) {
            eventosMostrados = new ArrayList<>(eventos);
        }

        @Override
        public void mostrarMisionesDisponibles(List<Mision> misiones) {
            misionesDisponibles = new ArrayList<>(misiones);
            if (codigoSeleccionado == null && !misiones.isEmpty()) {
                codigoSeleccionado = misiones.get(0).getCodigo();
            }
        }

        @Override
        public String getMisionSeleccionada() {
            return codigoSeleccionado;
        }

        @Override
        public void mostrarDetallesMision(Mision mision, boolean naveListaParaOperar, int combustibleActual,
                int energiaActual) {
            ultimaMisionDetallada = mision;
            naveListaDetallada = naveListaParaOperar;
            combustibleDetallado = combustibleActual;
            energiaDetallada = energiaActual;
        }

        @Override
        public void habilitarIniciarMision(boolean habilitar) {
        }

        @Override
        public void mostrarError(String mensaje) {
            errores.add(mensaje);
        }

        @Override
        public void mostrarMensaje(String mensaje) {
            ultimoMensaje = mensaje;
        }
    }

    public static class CursoMisionDePrueba extends ObservadorDelAsistente implements IVistaCursoMision {
        public final GrillaDePrueba grilla = new GrillaDePrueba();
        public ActionListener controlador;
        public final List<String> mensajes = new CopyOnWriteArrayList<>();
        public final List<String> iniciadas = new CopyOnWriteArrayList<>();
        public final List<String> errores = new CopyOnWriteArrayList<>();
        /** Resúmenes de fin de misión, en el orden en que llegan desde el hilo de la misión. */
        public final BlockingQueue<String> finalizaciones = new LinkedBlockingQueue<>();
        /** Hilos que avisaron el fin o un error. */
        public final Set<Thread> hilosDeAviso = ConcurrentHashMap.newKeySet();

        @Override
        protected void alNotificar(Asistente asistente, String mensaje) {
            mensajes.add(mensaje);
        }

        public void cancelar() {
            controlador.actionPerformed(evento(IVistaCursoMision.COMANDO_CANCELAR_MISION));
        }

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public IVistaTablero getVistaTablero() {
            return grilla;
        }

        @Override
        public void misionIniciada(String codigo, String nombre) {
            iniciadas.add(codigo);
        }

        @Override
        public void misionFinalizada(String resumen) {
            hilosDeAviso.add(Thread.currentThread());
            finalizaciones.add(resumen);
        }

        @Override
        public void mostrarError(String mensaje) {
            hilosDeAviso.add(Thread.currentThread());
            errores.add(mensaje);
        }
    }

    /**
     * Vista de prueba del tablero: como la real, consulta verTablero(), la nave y el objetivo en el hilo que
     * notifica.
     */
    public static class GrillaDePrueba implements IVistaTablero {
        private volatile Tablero observado;
        public final AtomicInteger notificaciones = new AtomicInteger();
        public final Set<Thread> hilosQueNotificaron = ConcurrentHashMap.newKeySet();
        public final CountDownLatch primeraNotificacion = new CountDownLatch(1);
        public volatile int filasVistas;
        public volatile int columnasVistas;
        public volatile int elementosVistos;
        public volatile Posicion naveVista;
        public volatile Posicion objetivoVisto;

        @Override
        public void observar(Tablero tablero) {
            dejarDeObservar();
            observado = tablero;
            tablero.agregarObservador(this);
            consultar(tablero);
        }

        @Override
        public void dejarDeObservar() {
            Tablero actual = observado;
            if (actual != null) {
                actual.quitarObservador(this);
                observado = null;
            }
        }

        public Tablero getObservado() {
            return observado;
        }

        @Override
        public void actualizar(String mensaje) {
            hilosQueNotificaron.add(Thread.currentThread());
            notificaciones.incrementAndGet();
            consultar(observado);
            primeraNotificacion.countDown();
        }

        private void consultar(Tablero tablero) {
            ElementoDelUniverso[][] matriz = tablero.verTablero();
            int elementos = 0;
            for (ElementoDelUniverso[] fila : matriz) {
                for (ElementoDelUniverso elemento : fila) {
                    elementos += elemento != null && !elemento.getSimbolo().isEmpty() ? 1 : 0;
                }
            }
            filasVistas = matriz.length;
            columnasVistas = matriz.length > 0 ? matriz[0].length : 0;
            elementosVistos = elementos;
            naveVista = tablero.getPosicionNave();
            objetivoVisto = tablero.getObjetivo();
        }
    }

    public static class ResultadosDePrueba extends ObservadorDelAsistente implements IVistaResultadosMision {
        public ActionListener controlador;
        public volatile List<InformeMision> informesVistos = List.of();
        public volatile List<InformeMision> informesMostrados = List.of();

        @Override
        protected void alNotificar(Asistente asistente, String mensaje) {
            List<InformeMision> copia = new ArrayList<>();
            for (Mision mision : asistente.getMisionesRealizadas()) {
                copia.add(mision.getInforme());
            }
            informesVistos = copia;
        }

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public void mostrarInformes(List<InformeMision> informes) {
            informesMostrados = new ArrayList<>(informes);
        }
    }

    // =====================================================================
    // Visor Recóndito
    // =====================================================================

    public static class VisorDePrueba implements IVistaConcurrencia {
        private volatile PlataformaDeCarga observada;
        public ActionListener controlador;
        public final AtomicInteger notificaciones = new AtomicInteger();
        public final List<String> errores = new CopyOnWriteArrayList<>();

        @Override
        public void observar(PlataformaDeCarga plataforma) {
            dejarDeObservar();
            observada = plataforma;
            plataforma.agregarObservador(this);
        }

        @Override
        public void dejarDeObservar() {
            PlataformaDeCarga actual = observada;
            if (actual != null) {
                actual.quitarObservador(this);
                observada = null;
            }
        }

        public PlataformaDeCarga getObservada() {
            return observada;
        }

        @Override
        public void actualizar(String mensaje) {
            notificaciones.incrementAndGet();
        }

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public void agregarEvento(String evento) {
        }

        @Override
        public void limpiarConsola() {
        }

        @Override
        public void actualizarBaterias(int bateriasActuales, int capacidadMaxima) {
        }

        @Override
        public void actualizarEntidades(int generadorasActivas, int navesActivas) {
        }

        @Override
        public void agregarGeneradora(String idGeneradora) {
        }

        @Override
        public void agregarNaveConsumidora(String idNave) {
        }

        @Override
        public void removerGeneradora(String idGeneradora) {
        }

        @Override
        public void removerNaveConsumidora(String idNave) {
        }

        @Override
        public void limpiarEntidades() {
        }

        @Override
        public void setOyenteDetenerGeneradora(Consumer<String> oyente) {
        }

        @Override
        public void setOyenteDetenerNave(Consumer<String> oyente) {
        }

        @Override
        public void mostrarError(String mensaje) {
            errores.add(mensaje);
        }
    }
}
