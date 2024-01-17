package matt.model.data.sensemod

import kotlinx.serialization.Serializable
import matt.lang.jpy.ExcludeFromPython
import matt.lang.jpy.PyClass
import matt.model.data.mathable.DoubleWrapper
import matt.model.data.mathable.IntWrapper
import matt.model.data.rad.Radians
import matt.prim.double.verifyWholeToInt
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

enum class SensoryModality {
    Visual, Audio
}


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
data class Degrees(val value: Int): IntWrapper<Degrees> {
    companion object {
        const val SYMBOL = "°"
        val ZERO = Degrees(0)
    }

    override fun fromInt(d: Int): Degrees {
        return Degrees(d)
    }

    override val asInt: Int
        get() = value

    override fun toString() = "$value$SYMBOL"

    @ExcludeFromPython
    fun toRadians() = Radians.fromDegrees(value)

}

val Double.degrees get() = DegreesDouble(this)

@PyClass
@JvmInline
@Serializable
value class DegreesDouble(override val asDouble: Double) : DoubleWrapper<DegreesDouble> {
    companion object {
        const val SYMBOL = "°"
        val ZERO = DegreesDouble(0.0)
    }

    override fun fromDouble(d: Double): DegreesDouble {
        return DegreesDouble(d)
    }


    override fun toString() = "$asDouble$SYMBOL"

    fun round() = Degrees(asDouble.roundToInt())

    fun verifyToWholeDegrees() = Degrees(asDouble.verifyWholeToInt())

    fun normalized() = DegreesDouble(asDouble % 360.0)

    @ExcludeFromPython
    fun toRadians() = Radians.fromDegrees(asDouble)
}