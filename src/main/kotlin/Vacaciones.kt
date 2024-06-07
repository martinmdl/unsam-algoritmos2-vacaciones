import java.time.LocalDate

class BusinessException(message: String) : Exception()

// #### PUNTO 3 ####
class Agencia {

    private val tours = mutableSetOf<Tour>()
    val clientes = mutableSetOf<Persona>()
    val obsConfirmacion = mutableSetOf<ObsConfirmacion>()
    val clientesPendientes = mutableSetOf<Persona>()

    fun crearTour(fechaSalida: LocalDate, cantidadPersonas: Int, lugares: MutableList<Lugar>, costoPorPersona: Int) {
        tours.add(Tour(
            fechaSalida = fechaSalida,
            cantidadPersonasRequerida = cantidadPersonas,
            lugares = lugares,
            costoPorPersona = costoPorPersona
        ))
    }

    fun seleccionarTourAdecuado(persona: Persona) {

        val toursDestinosAdecuados: List<Tour> = tours.filter { it.lugares.all { lugar -> persona.esDestinoAdecuado(lugar) } }
        val precioAdecuado: List<Tour> = toursDestinosAdecuados.filter { persona.esPrecioAdecuado(it) }

        try {
            val tourMasBarato: Tour = precioAdecuado.minByOrNull { it.costoPorPersona } ?: throw BusinessException("No se encontró un tour adecuado para $persona.")
            tourMasBarato.agregarPersona(persona)
        } catch(e: BusinessException) {
            clientesPendientes.add(persona)
        }
    }

    fun confirmarTour(tour: Tour) {

        if(!tour.estaLleno()) throw BusinessException("$tour tiene más capacidad.")
        tour.confirmarTour()
        obsConfirmacion.forEach { it.tourConfirmado(tour) }
    }

    fun agregarClienteAlTour(tour: Tour, persona: Persona) {
        tour.agregarPersona(persona)
    }

    fun eliminarClienteDelTour(tour: Tour, persona: Persona) {
        tour.eliminarPersona(persona)
    }

    fun modificarPresupuestoCliente(persona: Persona, nuevoPresupuesto: Int) {
        persona.modificarPresupuesto(nuevoPresupuesto)
    }
}

class Tour(
    val fechaSalida: LocalDate,
    val cantidadPersonasRequerida: Int,
    val lugares: MutableList<Lugar>,
    val costoPorPersona: Int
) {

    private var confirmado = false
    val anotados = mutableListOf<Persona>()

    fun confirmarTour() {
        confirmado = true
    }

    fun estaLleno(): Boolean = anotados.size >= cantidadPersonasRequerida

    fun agregarPersona(persona: Persona) {
        if(confirmado) throw BusinessException("$this ya está confirmado.")
        anotados.add(persona)
    }

    fun eliminarPersona(persona: Persona) {
        anotados.remove(persona)
    }
}

// #### PUNTO 1 ####
abstract class Lugar {

    abstract val nombre: String

    // Template Method
    fun esDivertido(): Boolean = letrasNombrePar() && esParticularmenteDivertido()

    abstract fun esTranquilo(): Boolean

    private fun letrasNombrePar(): Boolean = nombre.length % 2 == 0

    // Primitive
    abstract fun esParticularmenteDivertido(): Boolean
}

// #### PUNTO 1.1 ####
class Ciudad(
    override val nombre: String,
    private val cantAtracciones: Int,
    private val habitantes: Int,
    private val decibeles: Double,
) : Lugar() {

    companion object {
        private const val MIN_ATRACCIONES = 3
        private const val MIN_HABITANTES = 100000
        private const val MAX_DECIBELES = 20
    }

    override fun esTranquilo(): Boolean = decibeles < MAX_DECIBELES

    override fun esParticularmenteDivertido(): Boolean = tieneMuchasAtracciones() && esMuyHabitado()

    private fun tieneMuchasAtracciones(): Boolean = cantAtracciones > MIN_ATRACCIONES

    private fun esMuyHabitado(): Boolean = habitantes > MIN_HABITANTES
}

// #### PUNTO 1.2 ####
class Pueblo(
    override val nombre: String,
    private val provincia: String,
    private val fundacion: LocalDate
) : Lugar() {

    companion object { private val FECHA_MAX = LocalDate.of(1800, 1, 1) }

    override fun esTranquilo(): Boolean = provincia == "La Pampa"

    override fun esParticularmenteDivertido(): Boolean = esAntiguo() && esDelLitoral()

    private fun esDelLitoral(): Boolean = mutableSetOf("Entre Ríos", "Corrientes", "Misiones").contains(provincia)

    private fun esAntiguo(): Boolean = fundacion < FECHA_MAX
}

// #### PUNTO 1.3 ####
class Balneario(
    override val nombre: String,
    private val extension: Int,
    private val marPeligroso: Boolean,
    private val peatonal: Boolean
) : Lugar() {

    companion object { private const val EXTENSION_MIN = 300 }

    override fun esTranquilo(): Boolean = !tienePeatonal()

    private fun tienePeatonal(): Boolean = peatonal

    override fun esParticularmenteDivertido(): Boolean = esExtenso() && tieneMarPeligroso()

    private fun tieneMarPeligroso(): Boolean = marPeligroso

    private fun esExtenso() = extension > EXTENSION_MIN
}

// #### PUNTO 2 ####
class Persona(
    var preferencia: Preferencia,
    private var presupuesto: Int,
    val email: String
) {

    fun cambiarPreferencia(nuevaPreferencia: Preferencia) {
        preferencia = nuevaPreferencia
    }

    fun definirPresupuesto(monto: Int) {
        presupuesto = monto
    }

    fun esPrecioAdecuado(tour: Tour): Boolean = tour.costoPorPersona <= presupuesto

    fun esDestinoAdecuado(lugar: Lugar): Boolean = preferencia.esDestinoAdecuado(lugar)

    fun modificarPresupuesto(nuevoPresupuesto: Int) {
        presupuesto = nuevoPresupuesto
    }
}

// #### PUNTO 2 ####
interface Preferencia {
    fun esDestinoAdecuado(lugar: Lugar): Boolean
}

// Null-Object Pattern
class SinPreferencia : Preferencia {
    override fun esDestinoAdecuado(lugar: Lugar): Boolean = true
}

// #### PUNTO 2.1 ####
class Tranquila : Preferencia {
    override fun esDestinoAdecuado(lugar: Lugar): Boolean = lugar.esTranquilo()
}

// #### PUNTO 2.2 ####
class Divertida : Preferencia {
    override fun esDestinoAdecuado(lugar: Lugar): Boolean = lugar.esDivertido()
}

// #### PUNTO 2.3 ####
class Alternada(private var preferenciaActual: Preferencia) : Preferencia {

    override fun esDestinoAdecuado(lugar: Lugar): Boolean =
        preferenciaActual.esDestinoAdecuado(lugar)

    fun alternarPreferencia() {
        if (preferenciaActual is Tranquila) Divertida() else Tranquila()
    }
}

// #### PUNTO 2.4 ####
class CombinadaOr(private val preferencias: MutableSet<Preferencia>) : Preferencia {

    override fun esDestinoAdecuado(lugar: Lugar): Boolean =
        preferencias.any { it.esDestinoAdecuado(lugar) }
}

// #### PUNTO 4 ####
interface ObsConfirmacion {
    fun tourConfirmado(tour: Tour)
}

// #### PUNTO 4.1 ####
interface MailSender { fun sendMail(mail: Mail) }

data class Mail(val from: String, val to: String, val subject: String, val message: String)

class NotificarParticipantes(private val mailSender: MailSender) : ObsConfirmacion {

    override fun tourConfirmado(tour: Tour) {

        val emails = tour.anotados.joinToString(", ") { it.email }
        val lugares = tour.lugares.joinToString(", ") { it.nombre }
        val fechaLimitePago = tour.fechaSalida.minusDays(30)

        mailSender.sendMail(Mail(
            from = "agenciaViajes@gmail.com",
            to = emails,
            subject = "Info: $tour",
            message = "Fecha de salida: ${tour.fechaSalida}, Fecha límite de pago: $fechaLimitePago, Destinos: $lugares"
        ))
    }

}

// #### PUNTO 4.2 ####
interface InfoAfip { fun informar(informe: Informe) }

data class Informe(val codigos: String, val dnis: String)

class InformarAfip : ObsConfirmacion {

    override fun tourConfirmado(tour: Tour) {
        TODO("Not yet implemented")
    }
}

// #### PUNTO 4.3 ####
class AlternarPreferencia : ObsConfirmacion {

    override fun tourConfirmado(tour: Tour) {
        val anotadosPrefeAlternada = tour.anotados.filter { it.preferencia is Alternada }
        // anotadosPrefeAlternada.forEach { it.preferencia.alternarPreferencia() }
    }
}

// prueba
