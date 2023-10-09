package matt.model.data.sensemod

import kotlinx.serialization.Serializable
import matt.model.code.jpy.PyClass
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

@Serializable
enum class SensoryModality {
    Visual, Audio
}


@Serializable
enum class WaveForm {
    Square, Sin
}


@Serializable
data class Phase(val degrees: Degrees) {
    companion object {
        val ZERO = Phase(Degrees.ZERO)
        val HALF_CYCLE = Phase(Degrees(180))
    }

    override fun toString() = degrees.toString()
}

@Serializable
@PyClass
data class Degrees(val value: Int) {
    companion object {
        const val SYMBOL = "°"
        val ZERO = Degrees(0)
    }

    override fun toString() = "$value$SYMBOL"
}

@PyClass
@JvmInline
@Serializable
value class DegreesDouble(val value: Double) {
    companion object {
        const val SYMBOL = "°"
        val ZERO = DegreesDouble(0.0)
    }

    override fun toString() = "$value$SYMBOL"

    fun round() = Degrees(value.roundToInt())

    fun normalized() = DegreesDouble(value % 360.0)
}