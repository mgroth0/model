package matt.model.data.rad

import matt.model.data.sensemod.DegreesDouble
import kotlin.jvm.JvmInline
import kotlin.math.PI

@JvmInline
value class Radians(val radians: Double) {
    companion object {
        val ZERO = Radians(0.0)
        private const val PI_OVER_180 = (PI / 180)
        private const val PI_UNDER_180 = (180 / PI)
        fun fromDegrees(degrees: Number) = Radians(degrees.toDouble() * PI_OVER_180)
    }

    fun roundToWholeDegrees() = toDegrees().round()
    fun toDegrees(): DegreesDouble {
        return DegreesDouble(radians * PI_UNDER_180)
    }
}