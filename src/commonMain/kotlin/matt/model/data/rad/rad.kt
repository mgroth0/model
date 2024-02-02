package matt.model.data.rad

import kotlinx.serialization.Serializable
import matt.model.data.mathable.DoubleWrapper
import matt.model.data.sensemod.DegreesDouble
import kotlin.jvm.JvmInline
import kotlin.math.PI

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

    fun cos() = kotlin.math.cos(radians)
    fun sin() = kotlin.math.sin(radians)
    fun tan() = kotlin.math.tan(radians)
    override fun fromDouble(d: Double): Radians = Radians(d)

    override val asDouble: Double
        get() = radians
}


fun degreesToRadians(degrees: Double) = degrees * kotlin.math.PI / 180
fun degreesToRadians(degrees: Int) = degrees.toDouble() * kotlin.math.PI / 180
