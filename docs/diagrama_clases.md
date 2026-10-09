```mermaid
classDiagram
    %% Módulo Nave y Fábrica (Factory)
    class Nave {
        <<Abstract>>
        - static int ultimoIdAsignado
        - int id
        - Recursos recursos
        - MotorWarp motor
        - Tripulacion tripulacion
        ~ Nave(combustibleInicial, energiaInicial, desgasteInicial)
        + getTipo() TipoNave*
        + getId() int
        + getMotor() MotorWarp
        + getTripulacion() Tripulacion
        + asignarTripulacion(tripulacion: Tripulacion)
        + getCombustible() int
        + getEnergia() int
        + getDesgaste() int
        + requiereMantenimiento() boolean
        + estaListaParaOperar() boolean
        + cargarCombustible(cantidad: int)
        + cargarEnergia(cantidad: int)
        + consumirRecursos(combustible: int, energia: int, desgaste: int)
        + realizarMantenimiento()
        - invariante() boolean
    }
    class Exploradora {
        - int COMBUSTIBLE_INICIAL = 60
        - int ENERGIA_INICIAL = 80
        ~ Exploradora()
    }
    class Carguero {
        - int COMBUSTIBLE_INICIAL = 100
        - int ENERGIA_INICIAL = 60
        ~ Carguero()
    }
    class Combate {
        - int COMBUSTIBLE_INICIAL = 80
        - int ENERGIA_INICIAL = 100
        ~ Combate()
    }
    class NaveFactory {
        + crearNave(tipo: TipoNave) Nave
    }
    class TipoNave {
        <<enumeration>>
        EXPLORADORA
        CARGUERO
        COMBATE
        - String descripcion
    }
    class Recursos {
        <<paquete>>
        ~ int CAPACIDAD_MAXIMA_COMBUSTIBLE = 100
        ~ int CAPACIDAD_MAXIMA_ENERGIA = 100
        ~ int DESGASTE_MAXIMO = 100
        ~ int UMBRAL_MANTENIMIENTO = 80
        - int combustible
        - int energia
        - int desgaste
        ~ Recursos(combustibleInicial, energiaInicial, desgasteInicial)
        ~ getCombustible() int
        ~ getEnergia() int
        ~ getDesgaste() int
        ~ cargarCombustible(cantidad: int)
        ~ cargarEnergia(cantidad: int)
        ~ consumir(combustible: int, energia: int, desgaste: int)
        ~ realizarMantenimiento()
        ~ requiereMantenimiento() boolean
        - invariante() boolean
    }
    class TipoRecurso {
        <<enumeration>>
        COMBUSTIBLE
        ENERGIA
        DESGASTE
    }

    Nave <|-- Exploradora
    Nave <|-- Carguero
    Nave <|-- Combate
    NaveFactory ..> Nave : crea
    NaveFactory ..> TipoNave
    Nave --> TipoNave
    Nave *-- Recursos : Composición
    Nave *-- MotorWarp : Composición
    Nave o-- Tripulacion : Agregación (0..1)

    %% Módulo Motor Warp (State)
    class MotorWarp {
        - State estado
        + MotorWarp()
        + getEstado() State
        ~ setEstado(nuevoEstado: State)
        + prepararSalto() throws EstadoMotorInvalidoException
        + iniciarWarp() throws EstadoMotorInvalidoException
        + desactivarWarp() throws EstadoMotorInvalidoException
        + enfriar() throws EstadoMotorInvalidoException
        + estaDisponible() boolean
        - invariante() boolean
    }
    class State {
        <<Interface>>
        + prepararSalto() throws EstadoMotorInvalidoException
        + iniciarWarp() throws EstadoMotorInvalidoException
        + desactivarWarp() throws EstadoMotorInvalidoException
        + enfriar() throws EstadoMotorInvalidoException
        + estaDisponible() boolean
    }
    class DisponibleState {
        - MotorWarp motor
        + prepararSalto()
        + iniciarWarp()
        + desactivarWarp()
        + enfriar()
        + estaDisponible() boolean
    }
    class PreparandoSaltoState {
        - MotorWarp motor
        + prepararSalto()
        + iniciarWarp()
        + desactivarWarp()
        + enfriar()
        + estaDisponible() boolean
    }
    class EnWarpState {
        - MotorWarp motor
        + prepararSalto()
        + iniciarWarp()
        + desactivarWarp()
        + enfriar()
        + estaDisponible() boolean
    }
    class EnfriamientoState {
        - MotorWarp motor
        + prepararSalto()
        + iniciarWarp()
        + desactivarWarp()
        + enfriar()
        + estaDisponible() boolean
    }

    State <|.. DisponibleState
    State <|.. PreparandoSaltoState
    State <|.. EnWarpState
    State <|.. EnfriamientoState
    MotorWarp --> State : estado actual
    DisponibleState --> MotorWarp : motor
    PreparandoSaltoState --> MotorWarp : motor
    EnWarpState --> MotorWarp : motor
    EnfriamientoState --> MotorWarp : motor

    %% Módulo Misiones (Template Method - E1-06, E1-10)
    class Mision {
        <<Abstract>>
        + int COMBUSTIBLE_CONSUMIDO = 4
        + int DESGASTE_PRODUCIDO = 4
        - String codigo
        - String nombre
        - List~String~ acciones
        - Asistente asistente
        - EtapaMision etapa
        - boolean exitosa
        - InformeMision informe
        + asignarAsistente(asistente: Asistente)
        + getAsistente() Asistente
        + realizar() InformeMision
        - preparar()
        - ejecutar()
        - evaluar()
        - cerrar()
        # getEnergiaAdicional()* int
        # realizarAccion()*
        # objetivoCumplido()* boolean
        # getCondicionDeExito()* String
        # registrarAccion(descripcion: String)
        + getEtapa() EtapaMision
        + getInforme() InformeMision
        - invariante() boolean
    }
    class MisionIntercepcion {
        - boolean asistenciaRealizada
    }
    class MisionRecoleccion {
        - boolean elementoObtenido
    }
    class MisionRetorno {
    }
    class EtapaMision {
        <<enumeration>>
        CREADA
        PREPARADA
        EJECUTADA
        EVALUADA
        CERRADA
    }

    Mision <|-- MisionIntercepcion
    Mision <|-- MisionRecoleccion
    Mision <|-- MisionRetorno
    Mision --> EtapaMision

    %% Centro de control (Aclaración R1 y R2)
    class CentroDeControl {
        - List~Asistente~ asistentes
        - Asistente asistenteEnUso
        + CentroDeControl()
        + registrar(asistente: Asistente) throws NaveYaRegistradaException
        + buscar(idNave: int) Asistente throws NaveInexistenteException
        + seleccionar(idNave: int) Asistente throws NaveInexistenteException
        + hayNaveEnUso() boolean
        + getAsistenteEnUso() Asistente
        + getAsistentes() List~Asistente~
        + getCantidadNaves() int
        - estaRegistrada(idNave: int) boolean
        - invariante() boolean
    }
    CentroDeControl o-- Asistente : registra (0..*) y nave en uso (0..1)

    %% Módulo Asistente de Comandos (E1-03; Aclaración R2, R4 y R5)
    class Asistente {
        <<Interface>>
        + getIdNave() int
        + getTipoNave() TipoNave
        + getCombustible() int
        + getEnergia() int
        + getDesgaste() int
        + requiereMantenimiento() boolean
        + tieneTripulacion() boolean
        + naveListaParaOperar() boolean
        + getEstadoMotor() String
        + getEventos() List~Evento~
        + registrarEvento(tipo: TipoEvento, descripcion: String)
        + asignarTripulacion(tripulacion: Tripulacion)
        + cargarCombustible(cantidad: int)
        + cargarEnergia(cantidad: int)
        + realizarMantenimiento()
        + consumirRecursos(combustible: int, energia: int, desgaste: int)
        + prepararSalto() throws EstadoMotorInvalidoException
        + saltar() throws EstadoMotorInvalidoException
        + tieneMisionPendiente() boolean
        + getMisionPendiente() Mision
        + estaEjecutando(mision: Mision) boolean
        + getMisionesRealizadas() List~Mision~
        + encomendarMision(mision: Mision)
        + cancelarMision()
        + ejecutarMision() InformeMision
    }
    class AsistenteComando {
        - Nave nave
        - Bitacora bitacora
        - List~Mision~ misionesRealizadas
        - Mision misionPendiente
        - Mision misionEnEjecucion
        + AsistenteComando(nave: Nave)
        - registrarInforme(informe: InformeMision)
        - registrarRechazoDelMotor(e: EstadoMotorInvalidoException)
        - invariante() boolean
    }
    Asistente <|.. AsistenteComando
    %% Módulo Bitácora (E1-05) - implementado
    class Bitacora {
        - List~Evento~ eventos
        + registrarEvento(evento: Evento)
        + getEventos() List~Evento~
        - invariante() boolean
    }
    class Evento {
        - LocalDateTime fechaHora
        - TipoEvento tipo
        - String descripcion
        + Evento(fechaHora: LocalDateTime, tipo: TipoEvento, descripcion: String)
        + Evento(tipo: TipoEvento, descripcion: String)
        + getFechaHora() LocalDateTime
        + getTipo() TipoEvento
        + getDescripcion() String
        - invariante() boolean
    }
    class TipoEvento {
        <<enumeration>>
        MOTOR
        MISION
        RECURSO
        ERROR
        SISTEMA
        RELEVANTE
    }
    Evento --> TipoEvento
    class InformeMision {
        - String mision
        - boolean exitosa
        - List~String~ acciones
        - int combustibleConsumido
        - int energiaConsumida
        - int desgasteProducido
        - int combustibleFinal
        - int energiaFinal
        - int desgasteFinal
        - String estadoMotorFinal
        - boolean naveOperativa
        - String observaciones
    }

    AsistenteComando --> Nave : opera (1)
    AsistenteComando *-- Bitacora : Composición
    AsistenteComando o-- Mision : pendiente (0..1) y realizadas (0..*)
    Mision --> Asistente : encomendada a
    Mision *-- InformeMision : Composición
    Bitacora *-- Evento : Composición

    %% Módulo Tripulación (E1-04)
    class Tripulacion {
        + int TRIPULANTES_ADICIONALES_MINIMOS = 4
        + int TAMANIO_MINIMO = 5
        - List~Tripulante~ tripulantes
        + Tripulacion(integrantes: List~Tripulante~)
        + incorporar(tripulante: Tripulante)
        + desembarcar(tripulante: Tripulante)
        + buscarPorId(id: int) Tripulante
        + getCapitan() Tripulante
        + getTripulantes() List~Tripulante~
        + getTripulantes(cargo: Cargo) List~Tripulante~
        + getCantidad() int
        - invariante() boolean
    }
    class Tripulante {
        <<Abstract>>
        - static int ultimoIdAsignado
        - int id
        - String nombre
        - int antiguedad
        - Origen origen
        # Tripulante(nombre, antiguedad, origen)
        + getCargo() Cargo*
        # getRemuneracionCargo() double*
        + calcularAdicionalAntiguedad() double*
        + calcularConceptos(periodo: YearMonth) List~ConceptoHaber~
        + calcularHaberTotal(periodo: YearMonth) double
        + incrementarAntiguedad()
        + getOrigen() Origen
        - invariante() boolean
    }
    class Capitan {
        - double REMUNERACION_CARGO = 1000
        - double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.20
        + calcularAdicionalAntiguedad() double
    }
    class Consejero {
        - double REMUNERACION_CARGO = 600
        - double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.05
        - double IMPORTE_POR_CONSEJO = 2
        - List~YearMonth~ periodosDeConsejos
        + calcularAdicionalAntiguedad() double
        + registrarConsejo(periodo: YearMonth) throws LiquidacionInvalidaException
        + getCantidadConsejos(periodo: YearMonth) int
        + calcularAdicionalConsejos(periodo: YearMonth) double
        + calcularConceptos(periodo: YearMonth) List~ConceptoHaber~
    }
    class Teniente {
        - double REMUNERACION_CARGO = 400
        - double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.03
        + calcularAdicionalAntiguedad() double
    }
    class Alferez {
        - double REMUNERACION_CARGO = 200
        - double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.005
        + calcularAdicionalAntiguedad() double
    }
    class Cargo {
        <<enumeration>>
        CAPITAN
        CONSEJERO
        TENIENTE
        ALFEREZ
    }
    class Origen {
        <<enumeration>>
        TERRICOLA
        VULCANO
        MARCIANO
        - double subsidioMensual
        + getSubsidioMensual() double
    }

    Tripulacion o-- Tripulante : Agregación (5..*)
    Tripulante <|-- Capitan
    Tripulante <|-- Consejero
    Tripulante <|-- Teniente
    Tripulante <|-- Alferez
    Tripulante --> Cargo
    Tripulante --> Origen

    %% Módulo Liquidación de haberes (Decorator - E1-08)
    class Liquidacion {
        <<Interface>>
        + calcularConceptos(periodo: YearMonth) List~ConceptoHaber~
        + calcularHaberTotal(periodo: YearMonth) double
        + calcularAdicionalAntiguedad() double
        + getOrigen() Origen
    }
    class Decorator {
        <<Abstract>>
        - Liquidacion decorado
        - TipoConcepto conceptoAgregado
        # Decorator(decorado: Liquidacion, conceptoAgregado: TipoConcepto)
        # describirConcepto() String*
        # calcularImporte() double*
        # getDecorado() Liquidacion
        + calcularConceptos(periodo: YearMonth) List~ConceptoHaber~
        + calcularHaberTotal(periodo: YearMonth) double
        - conceptoYaAplicado(liquidacion, concepto)$ boolean
    }
    class AntiguedadDecorator {
        # describirConcepto() String
        # calcularImporte() double
    }
    class OrigenDecorator {
        # describirConcepto() String
        # calcularImporte() double
    }
    class ConceptoHaber {
        - TipoConcepto tipo
        - String descripcion
        - double importe
    }
    class TipoConcepto {
        <<enumeration>>
        CARGO
        CONSEJOS
        ANTIGUEDAD
        ORIGEN
    }
    class LiquidadorHaberes {
        + liquidar(tripulante: Tripulante, periodo: YearMonth) ReciboHaberes
        + liquidar(tripulacion: Tripulacion, periodo: YearMonth) LiquidacionTripulacion
    }
    class ReciboHaberes {
        - Tripulante tripulante
        - YearMonth periodo
        - List~ConceptoHaber~ conceptos
        - double total
        ~ ReciboHaberes(tripulante, periodo, conceptos, total)
        - invariante() boolean
    }
    class LiquidacionTripulacion {
        - YearMonth periodo
        - List~ReciboHaberes~ recibos
        ~ LiquidacionTripulacion(periodo, recibos)
        + calcularTotal() double
        - invariante() boolean
    }

    Liquidacion <|.. Tripulante : componente concreto
    Liquidacion <|.. Decorator
    Decorator <|-- AntiguedadDecorator
    Decorator <|-- OrigenDecorator
    Decorator o-- Liquidacion : decorado
    Liquidacion ..> ConceptoHaber : detalle
    ConceptoHaber --> TipoConcepto
    LiquidadorHaberes ..> Decorator : arma la cadena
    LiquidadorHaberes ..> ReciboHaberes : crea
    LiquidacionTripulacion o-- ReciboHaberes
    ReciboHaberes --> Tripulante

    %% Excepciones propias (comprobadas, extienden Exception)
    class TripulanteInvalidoException {
        - String nombreRecibido
        - int antiguedadRecibida
        - Origen origenRecibido
    }
    class TripulacionInvalidaException {
        - Tripulante tripulanteRechazado
    }
    class TripulanteInexistenteException {
        - int idBuscado
    }
    class LiquidacionInvalidaException {
        - TipoConcepto conceptoRechazado
    }
    Exception <|-- TripulanteInvalidoException
    Exception <|-- TripulacionInvalidaException
    Exception <|-- TripulanteInexistenteException
    Exception <|-- LiquidacionInvalidaException
    class CantidadInvalidaException {
        - TipoRecurso recurso
        - int cantidadRecibida
    }
    class CapacidadExcedidaException {
        - TipoRecurso recurso
        - int valorActual
        - int cantidadSolicitada
        - int maximo
    }
    class RecursoInsuficienteException {
        - TipoRecurso recurso
        - int disponible
        - int cantidadSolicitada
    }
    Exception <|-- CantidadInvalidaException
    Exception <|-- CapacidadExcedidaException
    Exception <|-- RecursoInsuficienteException
    Recursos ..> CantidadInvalidaException : lanza
    Recursos ..> CapacidadExcedidaException : lanza
    Recursos ..> RecursoInsuficienteException : lanza
    class NaveNoDisponibleException {
        - String codigoMision
        - boolean tieneTripulacion
        - boolean requiereMantenimiento
        - String estadoMotor
    }
    Exception <|-- NaveNoDisponibleException
    Mision ..> NaveNoDisponibleException : lanza
    Mision ..> RecursoInsuficienteException : lanza

    %% Excepción del Motor Warp (comprobada)
    class EstadoMotorInvalidoException {
        - String estadoActual
        + getEstadoActual() String
    }
    Exception <|-- EstadoMotorInvalidoException
    State ..> EstadoMotorInvalidoException : transición inválida

    %% Excepciones del centro de control
    class NaveInexistenteException {
        - int idNaveBuscada
        + getIdNaveBuscada() int
    }
    class NaveYaRegistradaException {
        - int idNave
        + getIdNave() int
    }
    Exception <|-- NaveInexistenteException
    Exception <|-- NaveYaRegistradaException
    CentroDeControl ..> NaveInexistenteException : lanza
    CentroDeControl ..> NaveYaRegistradaException : lanza
```