# Diagrama de clases — Entrega 1

Primero una **vista general** con las clases y sus relaciones principales; después, un diagrama por módulo con
atributos, métodos y excepciones, tal como están en el código.

**Notación:** `+` público, `-` privado, `#` protegido, `~` de paquete; `*` al final = abstracto; `$` al final = estático
(las constantes son `static final`). Se omiten `toString()`, los constructores por defecto sin código y los constructores privados de los `enum`.
`throws` indica las excepciones comprobadas que el método declara. Flecha continua (`-->`, `*--`, `o--`) = el atributo
existe; flecha punteada (`..>`) = dependencia (usa, crea o lanza). `App` (demostración por consola) no forma parte del modelo.

## 1. Vista general

```mermaid
classDiagram
    direction LR
    class CentroDeControl
    class Asistente
    <<interface>> Asistente
    class AsistenteComando
    class Nave
    <<abstract>> Nave
    class NaveFactory
    class MotorWarp
    class State
    <<interface>> State
    class Recursos
    class Tripulacion
    class Tripulante
    <<abstract>> Tripulante
    class Bitacora
    class Mision
    <<abstract>> Mision
    class InformeMision
    class Liquidacion
    <<interface>> Liquidacion
    class Decorator
    <<abstract>> Decorator
    class LiquidadorHaberes

    CentroDeControl o-- Asistente : registra (0..*) y nave en uso (0..1)
    Asistente <|.. AsistenteComando
    AsistenteComando --> Nave : opera (1)
    AsistenteComando *-- Bitacora
    AsistenteComando o-- Mision : pendiente y realizadas
    Mision --> Asistente : encomendada a
    Mision *-- InformeMision
    NaveFactory ..> Nave : crea (Factory)
    Nave *-- Recursos
    Nave *-- MotorWarp
    MotorWarp --> State : estado actual (State)
    Nave o-- Tripulacion
    Tripulacion o-- Tripulante
    Liquidacion <|.. Tripulante
    Liquidacion <|.. Decorator
    Decorator o-- Liquidacion : decorado (Decorator)
    LiquidadorHaberes ..> Decorator : arma la cadena
```

## 2. Nave y fábrica (E1-01, E1-07 Factory, E1-09)

```mermaid
classDiagram
    class Nave {
        <<abstract>>
        - int ultimoIdAsignado$
        - int id
        - Recursos recursos
        - MotorWarp motor
        - Tripulacion tripulacion
        ~ Nave(combustibleInicial: int, energiaInicial: int, desgasteInicial: int)
        + getTipo() TipoNave*
        + getId() int
        + getMotor() MotorWarp
        + getTripulacion() Tripulacion
        + asignarTripulacion(nuevaTripulacion: Tripulacion)
        + getCombustible() int
        + getEnergia() int
        + getDesgaste() int
        + requiereMantenimiento() boolean
        + estaListaParaOperar() boolean
        + cargarCombustible(cantidad: int) throws CantidadInvalidaException, CapacidadExcedidaException
        + cargarEnergia(cantidad: int) throws CantidadInvalidaException, CapacidadExcedidaException
        + consumirRecursos(combustible: int, energia: int, desgaste: int) throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException
        + realizarMantenimiento()
        - invariante() boolean
    }
    class Exploradora {
        - int COMBUSTIBLE_INICIAL = 60$
        - int ENERGIA_INICIAL = 80$
        - int DESGASTE_INICIAL = 0$
        ~ Exploradora()
        + getTipo() TipoNave
    }
    class Carguero {
        - int COMBUSTIBLE_INICIAL = 100$
        - int ENERGIA_INICIAL = 60$
        - int DESGASTE_INICIAL = 0$
        ~ Carguero()
        + getTipo() TipoNave
    }
    class Combate {
        - int COMBUSTIBLE_INICIAL = 80$
        - int ENERGIA_INICIAL = 100$
        - int DESGASTE_INICIAL = 0$
        ~ Combate()
        + getTipo() TipoNave
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
        + getDescripcion() String
    }
    class Recursos {
        <<paquete>>
        ~ int CAPACIDAD_MAXIMA_COMBUSTIBLE = 100$
        ~ int CAPACIDAD_MAXIMA_ENERGIA = 100$
        ~ int DESGASTE_MAXIMO = 100$
        ~ int UMBRAL_MANTENIMIENTO = 80$
        - int combustible
        - int energia
        - int desgaste
        ~ Recursos(combustibleInicial: int, energiaInicial: int, desgasteInicial: int)
        ~ getCombustible() int
        ~ getEnergia() int
        ~ getDesgaste() int
        ~ requiereMantenimiento() boolean
        ~ cargarCombustible(cantidad: int) throws CantidadInvalidaException, CapacidadExcedidaException
        ~ cargarEnergia(cantidad: int) throws CantidadInvalidaException, CapacidadExcedidaException
        ~ consumir(combustibleConsumido: int, energiaConsumida: int, desgasteProducido: int) throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException
        ~ realizarMantenimiento()
        - validarCantidad(recurso: TipoRecurso, cantidad: int) throws CantidadInvalidaException
        - invariante() boolean
    }
    class TipoRecurso {
        <<enumeration>>
        COMBUSTIBLE
        ENERGIA
        DESGASTE
    }
    class MotorWarp
    class Tripulacion
    class CantidadInvalidaException
    class CapacidadExcedidaException
    class RecursoInsuficienteException

    Nave <|-- Exploradora
    Nave <|-- Carguero
    Nave <|-- Combate
    NaveFactory ..> Exploradora : crea
    NaveFactory ..> Carguero : crea
    NaveFactory ..> Combate : crea
    NaveFactory ..> TipoNave
    Nave ..> TipoNave
    Nave *-- Recursos : composición
    Nave *-- MotorWarp : composición
    Nave o-- Tripulacion : agregación (0..1)
    Recursos ..> TipoRecurso
    Recursos ..> CantidadInvalidaException : lanza
    Recursos ..> CapacidadExcedidaException : lanza
    Recursos ..> RecursoInsuficienteException : lanza
```

## 3. Motor Warp (E1-02, patrón State)

```mermaid
classDiagram
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
        <<interface>>
        + prepararSalto() throws EstadoMotorInvalidoException
        + iniciarWarp() throws EstadoMotorInvalidoException
        + desactivarWarp() throws EstadoMotorInvalidoException
        + enfriar() throws EstadoMotorInvalidoException
        + estaDisponible() boolean
    }
    class DisponibleState {
        - String NOMBRE$
        - MotorWarp motor
        ~ DisponibleState(motor: MotorWarp)
        + prepararSalto()
        + iniciarWarp() throws EstadoMotorInvalidoException
        + desactivarWarp() throws EstadoMotorInvalidoException
        + enfriar() throws EstadoMotorInvalidoException
        + estaDisponible() boolean
    }
    class PreparandoSaltoState {
        - String NOMBRE$
        - MotorWarp motor
        ~ PreparandoSaltoState(motor: MotorWarp)
        + prepararSalto() throws EstadoMotorInvalidoException
        + iniciarWarp()
        + desactivarWarp() throws EstadoMotorInvalidoException
        + enfriar() throws EstadoMotorInvalidoException
        + estaDisponible() boolean
    }
    class EnWarpState {
        - String NOMBRE$
        - MotorWarp motor
        ~ EnWarpState(motor: MotorWarp)
        + prepararSalto() throws EstadoMotorInvalidoException
        + iniciarWarp() throws EstadoMotorInvalidoException
        + desactivarWarp()
        + enfriar() throws EstadoMotorInvalidoException
        + estaDisponible() boolean
    }
    class EnfriamientoState {
        - String NOMBRE$
        - MotorWarp motor
        ~ EnfriamientoState(motor: MotorWarp)
        + prepararSalto() throws EstadoMotorInvalidoException
        + iniciarWarp() throws EstadoMotorInvalidoException
        + desactivarWarp() throws EstadoMotorInvalidoException
        + enfriar()
        + estaDisponible() boolean
    }
    class EstadoMotorInvalidoException

    State <|.. DisponibleState
    State <|.. PreparandoSaltoState
    State <|.. EnWarpState
    State <|.. EnfriamientoState
    MotorWarp --> State : estado actual
    DisponibleState --> MotorWarp : motor
    PreparandoSaltoState --> MotorWarp : motor
    EnWarpState --> MotorWarp : motor
    EnfriamientoState --> MotorWarp : motor
    DisponibleState ..> PreparandoSaltoState : prepararSalto()
    PreparandoSaltoState ..> EnWarpState : iniciarWarp()
    EnWarpState ..> EnfriamientoState : desactivarWarp()
    EnfriamientoState ..> DisponibleState : enfriar()
    State ..> EstadoMotorInvalidoException : transición inválida
```

Cada estado concreto implementa sin `throws` su única transición válida y rechaza las otras tres con
`EstadoMotorInvalidoException`. Los constructores de los estados y `setEstado()` son de paquete: sólo el motor y los
estados pueden cambiar el estado.

## 4. Misión (E1-06 Template Method, E1-10)

```mermaid
classDiagram
    class Mision {
        <<abstract>>
        + int COMBUSTIBLE_CONSUMIDO = 4$
        + int DESGASTE_PRODUCIDO = 4$
        - String codigo
        - String nombre
        - List~String~ acciones
        - Asistente asistente
        - EtapaMision etapa
        - boolean exitosa
        - InformeMision informe
        # Mision(codigo: String, nombre: String)
        # getEnergiaAdicional() int*
        # realizarAccion()*
        # objetivoCumplido() boolean*
        # getCondicionDeExito() String*
        + asignarAsistente(asistente: Asistente)
        + realizar() InformeMision throws NaveNoDisponibleException, RecursoInsuficienteException
        - preparar() throws NaveNoDisponibleException, RecursoInsuficienteException
        - ejecutar() throws RecursoInsuficienteException
        - evaluar()
        - cerrar()
        # registrarAccion(descripcion: String)
        + getCodigo() String
        + getNombre() String
        + getEtapa() EtapaMision
        + getAsistente() Asistente
        + getInforme() InformeMision
        - invariante() boolean
    }
    note for Mision "realizar() y registrarAccion() son final. realizar() es el método plantilla: llama siempre a preparar, ejecutar, evaluar y cerrar, en ese orden."
    class MisionIntercepcion {
        - int ENERGIA_ADICIONAL = 5$
        - boolean asistenciaRealizada
        + MisionIntercepcion()
        # getEnergiaAdicional() int
        # realizarAccion()
        # objetivoCumplido() boolean
        # getCondicionDeExito() String
    }
    class MisionRecoleccion {
        - int ENERGIA_ADICIONAL = 5$
        - boolean elementoObtenido
        + MisionRecoleccion()
        # getEnergiaAdicional() int
        # realizarAccion()
        # objetivoCumplido() boolean
        # getCondicionDeExito() String
    }
    class MisionRetorno {
        - int ENERGIA_ADICIONAL = 0$
        + MisionRetorno()
        # getEnergiaAdicional() int
        # realizarAccion()
        # objetivoCumplido() boolean
        # getCondicionDeExito() String
    }
    class EtapaMision {
        <<enumeration>>
        CREADA
        PREPARADA
        EJECUTADA
        EVALUADA
        CERRADA
    }
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
        ~ InformeMision(mision, exitosa, acciones, combustibleConsumido, energiaConsumida, desgasteProducido, combustibleFinal, energiaFinal, desgasteFinal, estadoMotorFinal, naveOperativa, observaciones)
        + getMision() String
        + isExitosa() boolean
        + getAcciones() List~String~
        + getCombustibleConsumido() int
        + getEnergiaConsumida() int
        + getDesgasteProducido() int
        + getCombustibleFinal() int
        + getEnergiaFinal() int
        + getDesgasteFinal() int
        + getEstadoMotorFinal() String
        + isNaveOperativa() boolean
        + getObservaciones() String
        - invariante() boolean
    }
    class Asistente
    <<interface>> Asistente
    class NaveNoDisponibleException
    class RecursoInsuficienteException

    Mision <|-- MisionIntercepcion
    Mision <|-- MisionRecoleccion
    Mision <|-- MisionRetorno
    Mision --> EtapaMision
    Mision --> Asistente : encomendada a (0..1)
    Mision *-- InformeMision : genera al cerrar
    Mision ..> NaveNoDisponibleException : lanza
    Mision ..> RecursoInsuficienteException : lanza
```

## 5. Asistente, centro de control y Bitácora (E1-03, E1-05; Aclaración R1, R2, R4 y R5)

```mermaid
classDiagram
    class Asistente {
        <<interface>>
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
        + cargarCombustible(cantidad: int) throws CantidadInvalidaException, CapacidadExcedidaException
        + cargarEnergia(cantidad: int) throws CantidadInvalidaException, CapacidadExcedidaException
        + realizarMantenimiento()
        + consumirRecursos(combustible: int, energia: int, desgaste: int) throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException
        + prepararSalto() throws EstadoMotorInvalidoException
        + saltar() throws EstadoMotorInvalidoException
        + tieneMisionPendiente() boolean
        + getMisionPendiente() Mision
        + estaEjecutando(mision: Mision) boolean
        + getMisionesRealizadas() List~Mision~
        + encomendarMision(mision: Mision)
        + cancelarMision()
        + ejecutarMision() InformeMision throws NaveNoDisponibleException, RecursoInsuficienteException
    }
    class AsistenteComando {
        - Nave nave
        - Bitacora bitacora
        - List~Mision~ misionesRealizadas
        - Mision misionPendiente
        - Mision misionEnEjecucion
        + AsistenteComando(nave: Nave)
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
        + cargarCombustible(cantidad: int) throws CantidadInvalidaException, CapacidadExcedidaException
        + cargarEnergia(cantidad: int) throws CantidadInvalidaException, CapacidadExcedidaException
        + realizarMantenimiento()
        + consumirRecursos(combustible: int, energia: int, desgaste: int) throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException
        + prepararSalto() throws EstadoMotorInvalidoException
        + saltar() throws EstadoMotorInvalidoException
        + tieneMisionPendiente() boolean
        + getMisionPendiente() Mision
        + estaEjecutando(mision: Mision) boolean
        + getMisionesRealizadas() List~Mision~
        + encomendarMision(mision: Mision)
        + cancelarMision()
        + ejecutarMision() InformeMision throws NaveNoDisponibleException, RecursoInsuficienteException
        - registrarInforme(informe: InformeMision)
        - registrarRechazoDelMotor(e: EstadoMotorInvalidoException)
        - invariante() boolean
    }
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
    class Nave
    <<abstract>> Nave
    class Mision
    <<abstract>> Mision
    class NaveYaRegistradaException
    class NaveInexistenteException

    Asistente <|.. AsistenteComando
    AsistenteComando --> Nave : opera (1)
    AsistenteComando *-- Bitacora : composición
    AsistenteComando o-- Mision : pendiente (0..1), en ejecución (0..1) y realizadas (0..*)
    CentroDeControl o-- Asistente : registra (0..*) y nave en uso (0..1)
    CentroDeControl ..> NaveYaRegistradaException : lanza
    CentroDeControl ..> NaveInexistenteException : lanza
    Bitacora *-- Evento : composición
    Evento --> TipoEvento
```

`Bitacora` y `Evento` rechazan los eventos nulos, vacíos o fuera de orden con `IllegalArgumentException`
(excepción no comprobada, por eso no figura en `throws`).

## 6. Tripulación (E1-04)

```mermaid
classDiagram
    class Tripulacion {
        + int TRIPULANTES_ADICIONALES_MINIMOS = 4$
        + int TAMANIO_MINIMO = 5$
        - List~Tripulante~ tripulantes
        + Tripulacion(integrantes: List~Tripulante~) throws TripulacionInvalidaException
        + incorporar(tripulante: Tripulante) throws TripulacionInvalidaException
        + desembarcar(tripulante: Tripulante) throws TripulacionInvalidaException
        + buscarPorId(id: int) Tripulante throws TripulanteInexistenteException
        + getCapitan() Tripulante
        + getTripulantes() List~Tripulante~
        + getTripulantes(cargo: Cargo) List~Tripulante~
        + getCantidad() int
        - validarIncorporacion(tripulante: Tripulante) throws TripulacionInvalidaException
        - contarCapitanes() int
        - invariante() boolean
    }
    class Tripulante {
        <<abstract>>
        - int ultimoIdAsignado$
        - int id
        - String nombre
        - int antiguedad
        - Origen origen
        # Tripulante(nombre: String, antiguedad: int, origen: Origen) throws TripulanteInvalidoException
        - siguienteId() int$
        + getCargo() Cargo*
        # getRemuneracionCargo() double*
        + calcularAdicionalAntiguedad() double*
        + calcularConceptos(periodo: YearMonth) List~ConceptoHaber~
        + calcularHaberTotal(periodo: YearMonth) double
        + incrementarAntiguedad()
        + getId() int
        + getNombre() String
        + getAntiguedad() int
        + getOrigen() Origen
        - invariante() boolean
    }
    class Capitan {
        - double REMUNERACION_CARGO = 1000$
        - double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.20$
        + Capitan(nombre: String, antiguedad: int, origen: Origen) throws TripulanteInvalidoException
        + getCargo() Cargo
        # getRemuneracionCargo() double
        + calcularAdicionalAntiguedad() double
    }
    class Consejero {
        - double REMUNERACION_CARGO = 600$
        - double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.05$
        - double IMPORTE_POR_CONSEJO = 2$
        - List~YearMonth~ periodosDeConsejos
        + Consejero(nombre: String, antiguedad: int, origen: Origen) throws TripulanteInvalidoException
        + getCargo() Cargo
        # getRemuneracionCargo() double
        + calcularAdicionalAntiguedad() double
        + registrarConsejo(periodo: YearMonth) throws LiquidacionInvalidaException
        + getCantidadConsejos(periodo: YearMonth) int
        + calcularAdicionalConsejos(periodo: YearMonth) double
        + calcularConceptos(periodo: YearMonth) List~ConceptoHaber~
        - invarianteConsejos() boolean
    }
    class Teniente {
        - double REMUNERACION_CARGO = 400$
        - double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.03$
        + Teniente(nombre: String, antiguedad: int, origen: Origen) throws TripulanteInvalidoException
        + getCargo() Cargo
        # getRemuneracionCargo() double
        + calcularAdicionalAntiguedad() double
    }
    class Alferez {
        - double REMUNERACION_CARGO = 200$
        - double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.005$
        + Alferez(nombre: String, antiguedad: int, origen: Origen) throws TripulanteInvalidoException
        + getCargo() Cargo
        # getRemuneracionCargo() double
        + calcularAdicionalAntiguedad() double
    }
    class Cargo {
        <<enumeration>>
        CAPITAN
        CONSEJERO
        TENIENTE
        ALFEREZ
        - String nombre
        + getNombre() String
    }
    class Origen {
        <<enumeration>>
        TERRICOLA
        VULCANO
        MARCIANO
        - String nombre
        - double subsidioMensual
        + getNombre() String
        + getSubsidioMensual() double
    }
    class Liquidacion
    <<interface>> Liquidacion
    class TripulacionInvalidaException
    class TripulanteInexistenteException
    class TripulanteInvalidoException
    class LiquidacionInvalidaException

    Tripulacion o-- Tripulante : agregación (5..*)
    Liquidacion <|.. Tripulante : componente concreto
    Tripulante <|-- Capitan
    Tripulante <|-- Consejero
    Tripulante <|-- Teniente
    Tripulante <|-- Alferez
    Tripulante ..> Cargo
    Tripulante --> Origen
    Tripulacion ..> TripulacionInvalidaException : lanza
    Tripulacion ..> TripulanteInexistenteException : lanza
    Tripulante ..> TripulanteInvalidoException : lanza
    Consejero ..> LiquidacionInvalidaException : lanza
```

## 7. Liquidación de haberes (E1-08, patrón Decorator)

```mermaid
classDiagram
    class Liquidacion {
        <<interface>>
        + calcularConceptos(periodo: YearMonth) List~ConceptoHaber~
        + calcularHaberTotal(periodo: YearMonth) double
        + calcularAdicionalAntiguedad() double
        + getOrigen() Origen
    }
    class Decorator {
        <<abstract>>
        - Liquidacion decorado
        - TipoConcepto conceptoAgregado
        # Decorator(decorado: Liquidacion, conceptoAgregado: TipoConcepto) throws LiquidacionInvalidaException
        # describirConcepto() String*
        # calcularImporte() double*
        # getDecorado() Liquidacion
        + calcularConceptos(periodo: YearMonth) List~ConceptoHaber~
        + calcularHaberTotal(periodo: YearMonth) double
        + calcularAdicionalAntiguedad() double
        + getOrigen() Origen
        - conceptoYaAplicado(liquidacion: Liquidacion, concepto: TipoConcepto) boolean$
        - invariante() boolean
    }
    class AntiguedadDecorator {
        + AntiguedadDecorator(decorado: Liquidacion) throws LiquidacionInvalidaException
        # describirConcepto() String
        # calcularImporte() double
    }
    class OrigenDecorator {
        + OrigenDecorator(decorado: Liquidacion) throws LiquidacionInvalidaException
        # describirConcepto() String
        # calcularImporte() double
    }
    class ConceptoHaber {
        - TipoConcepto tipo
        - String descripcion
        - double importe
        + ConceptoHaber(tipo: TipoConcepto, descripcion: String, importe: double)
        + getTipo() TipoConcepto
        + getDescripcion() String
        + getImporte() double
        - invariante() boolean
    }
    class TipoConcepto {
        <<enumeration>>
        CARGO
        CONSEJOS
        ANTIGUEDAD
        ORIGEN
    }
    class LiquidadorHaberes {
        + liquidar(tripulante: Tripulante, periodo: YearMonth) ReciboHaberes throws LiquidacionInvalidaException
        + liquidar(tripulacion: Tripulacion, periodo: YearMonth) LiquidacionTripulacion throws LiquidacionInvalidaException
    }
    class ReciboHaberes {
        - double TOLERANCIA = 0.0001$
        - Tripulante tripulante
        - YearMonth periodo
        - List~ConceptoHaber~ conceptos
        - double total
        ~ ReciboHaberes(tripulante: Tripulante, periodo: YearMonth, conceptos: List~ConceptoHaber~, total: double)
        + getTripulante() Tripulante
        + getPeriodo() YearMonth
        + getConceptos() List~ConceptoHaber~
        + getTotal() double
        - invariante() boolean
    }
    class LiquidacionTripulacion {
        - YearMonth periodo
        - List~ReciboHaberes~ recibos
        ~ LiquidacionTripulacion(periodo: YearMonth, recibos: List~ReciboHaberes~)
        + getPeriodo() YearMonth
        + getRecibos() List~ReciboHaberes~
        + calcularTotal() double
        - invariante() boolean
    }
    class Tripulante
    <<abstract>> Tripulante
    class LiquidacionInvalidaException

    Liquidacion <|.. Tripulante : componente concreto
    Liquidacion <|.. Decorator
    Decorator <|-- AntiguedadDecorator
    Decorator <|-- OrigenDecorator
    Decorator o-- Liquidacion : decorado
    Decorator --> TipoConcepto : concepto agregado
    Liquidacion ..> ConceptoHaber : detalle
    ConceptoHaber --> TipoConcepto
    LiquidadorHaberes ..> AntiguedadDecorator : crea
    LiquidadorHaberes ..> OrigenDecorator : crea
    LiquidadorHaberes ..> ReciboHaberes : crea
    LiquidadorHaberes ..> LiquidacionTripulacion : crea
    LiquidacionTripulacion o-- ReciboHaberes
    ReciboHaberes --> Tripulante
    ReciboHaberes o-- ConceptoHaber
    Decorator ..> LiquidacionInvalidaException : lanza
    LiquidadorHaberes ..> LiquidacionInvalidaException : lanza
```

## 8. Excepciones propias (comprobadas: extienden `Exception`)

```mermaid
classDiagram
    class Exception
    class CantidadInvalidaException {
        - TipoRecurso recurso
        - int cantidadRecibida
        + CantidadInvalidaException(recurso: TipoRecurso, cantidadRecibida: int)
        + getRecurso() TipoRecurso
        + getCantidadRecibida() int
    }
    class CapacidadExcedidaException {
        - TipoRecurso recurso
        - int valorActual
        - int cantidadSolicitada
        - int maximo
        + CapacidadExcedidaException(recurso: TipoRecurso, valorActual: int, cantidadSolicitada: int, maximo: int)
        + getRecurso() TipoRecurso
        + getValorActual() int
        + getCantidadSolicitada() int
        + getMaximo() int
    }
    class RecursoInsuficienteException {
        - TipoRecurso recurso
        - int disponible
        - int cantidadSolicitada
        + RecursoInsuficienteException(recurso: TipoRecurso, disponible: int, cantidadSolicitada: int)
        + getRecurso() TipoRecurso
        + getDisponible() int
        + getCantidadSolicitada() int
    }
    class EstadoMotorInvalidoException {
        - String estadoActual
        + EstadoMotorInvalidoException(mensaje: String, estadoActual: String)
        + getEstadoActual() String
    }
    class NaveNoDisponibleException {
        - String codigoMision
        - boolean tieneTripulacion
        - boolean requiereMantenimiento
        - String estadoMotor
        + NaveNoDisponibleException(codigoMision: String, tieneTripulacion: boolean, requiereMantenimiento: boolean, estadoMotor: String)
        + getCodigoMision() String
        + tieneTripulacion() boolean
        + requiereMantenimiento() boolean
        + getEstadoMotor() String
    }
    class NaveInexistenteException {
        - int idNaveBuscada
        + NaveInexistenteException(idNaveBuscada: int)
        + getIdNaveBuscada() int
    }
    class NaveYaRegistradaException {
        - int idNave
        + NaveYaRegistradaException(idNave: int)
        + getIdNave() int
    }
    class TripulanteInvalidoException {
        - String nombreRecibido
        - int antiguedadRecibida
        - Origen origenRecibido
        + TripulanteInvalidoException(mensaje: String, nombreRecibido: String, antiguedadRecibida: int, origenRecibido: Origen)
        + getNombreRecibido() String
        + getAntiguedadRecibida() int
        + getOrigenRecibido() Origen
    }
    class TripulacionInvalidaException {
        - Tripulante tripulanteRechazado
        + TripulacionInvalidaException(mensaje: String, tripulanteRechazado: Tripulante)
        + getTripulanteRechazado() Tripulante
    }
    class TripulanteInexistenteException {
        - int idBuscado
        + TripulanteInexistenteException(idBuscado: int)
        + getIdBuscado() int
    }
    class LiquidacionInvalidaException {
        - TipoConcepto conceptoRechazado
        + LiquidacionInvalidaException(mensaje: String, conceptoRechazado: TipoConcepto)
        + getConceptoRechazado() TipoConcepto
    }

    Exception <|-- CantidadInvalidaException
    Exception <|-- CapacidadExcedidaException
    Exception <|-- RecursoInsuficienteException
    Exception <|-- EstadoMotorInvalidoException
    Exception <|-- NaveNoDisponibleException
    Exception <|-- NaveInexistenteException
    Exception <|-- NaveYaRegistradaException
    Exception <|-- TripulanteInvalidoException
    Exception <|-- TripulacionInvalidaException
    Exception <|-- TripulanteInexistenteException
    Exception <|-- LiquidacionInvalidaException
```
