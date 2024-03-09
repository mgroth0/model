package matt.model.data.rad

import kotlinx.serialization.Serializable
import matt.model.data.mathable.DoubleWrapper
import matt.model.data.sensemod.DegreesDouble
import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.cos as kotlinCos
import kotlin.math.sin as kotlinSin
import kotlin.math.tan as kotlinTan

val Double.radians get() = Radians(this)

@Serializable
@JvmInline
value class Radians(val radians: Double): DoubleWrapper<Radians> {
    companion object {
        val ZERO = Radians(0.0)
        private const val PI_OVER_180 = (PI / 180)
        private const val PI_UNDER_180 = (180 / PI)
        fun fromDegrees(degrees: Number) = Radians(degrees.toDouble() * PI_OVER_180)
    }

    fun roundToWholeDegrees() = toDegrees().round()
    fun toDegrees(): DegreesDouble = DegreesDouble(radians * PI_UNDER_180)

    fun cos() = kotlinCos(radians)
    fun sin() = kotlinSin(radians)
    fun tan() = kotlinTan(radians)
    override fun fromDouble(d: Double): Radians = Radians(d)

    override val asDouble: Double
        get() = radians
}


fun degreesToRadians(degrees: Double) = degrees * PI / 180
fun degreesToRadians(degrees: Int) = degrees.toDouble() * PI / 180
