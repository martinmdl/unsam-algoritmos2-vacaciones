import java.time.LocalDate

// #### PUNTO 3 ####
class Agencia {
    val tours = mutableSetOf<Tours>()
    val clientes = mutableSetOf<Personas>()
    val obsConfirmacion = mutableSetOf<ObsConfirmacion>()
}

class Tours(
    val fecha: LocalDate,
    val cantidadPersonas: Int,
    val lugares: MutableList<Lugar>,
    val costoPorPersona: Int
) {

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
class Ciudades(
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
class Pueblos(
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
class Balnearios(
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
class Personas(private var preferencia: Preferencia = SinPreferencia()) {

    fun cambiarPreferencia(nuevaPreferencia: Preferencia) {
        preferencia = nuevaPreferencia
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
    fun tourConfirmado()
}

// #### PUNTO 4.1 ####
interface MailSender { fun sendMail(mail: Mail) }

data class Mail(val from: String, val to: String, val subject: String, val message: String)

class NotificarParticipantes : ObsConfirmacion {

    override fun tourConfirmado() {
        TODO("Not yet implemented")
    }
}

// #### PUNTO 4.2 ####
interface InfoAfip { fun informar(informe: Informe) }

data class Informe(val codigos: String, val dnis: String)

class InformarAfip : ObsConfirmacion {

    override fun tourConfirmado() {
        TODO("Not yet implemented")
    }
}

// #### PUNTO 4.3 ####
class AlternarPreferencia : ObsConfirmacion {

    override fun tourConfirmado() {
        TODO("Not yet implemented")
    }
}
