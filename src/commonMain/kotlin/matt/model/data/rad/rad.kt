package matt.model.data.rad

import kotlin.jvm.JvmInline
import kotlin.math.PI

@JvmInline
value class Radians(val radians: Double) {
    companion object {
        val ZERO = Radians(0.0)
        private const val PI_OVER_180 = (PI / 180)
        fun fromDegrees(degrees: Number) = Radians(degrees.toDouble() * PI_OVER_180)
    }
}