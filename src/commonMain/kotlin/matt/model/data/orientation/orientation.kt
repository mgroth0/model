package matt.model.data.orientation

import kotlinx.serialization.Serializable
import matt.prim.str.prependZeros
import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue

private const val ORIENTATION_BIN_NUM_DIGITS = 2

@Serializable
data class BinnedOrientation(
    val yawBin: YawBin,
    val pitchBin: PitchBin
) : Comparable<BinnedOrientation> {
    companion object {
        private val comparator = compareBy<BinnedOrientation> { it.yawBin }.thenBy { it.pitchBin }
    }

    val label = "$yawBin$pitchBin"

    override fun toString() = label
    override fun compareTo(other: BinnedOrientation): Int {
        return comparator.compare(this, other)
    }
}


@JvmInline
@Serializable
value class YawBin(val angle: Int) : Comparable<YawBin> {
    private val num get() = angle.absoluteValue.prependZeros(ORIENTATION_BIN_NUM_DIGITS)
    override fun toString(): String {
        return when {
            angle == 0 -> "C$num"
            angle > 0  -> "R$num"
            else       -> "L$num"
        }
    }

    override fun compareTo(other: YawBin): Int {
        return angle.compareTo(other.angle)
    }
}

@JvmInline
@Serializable
value class PitchBin(val angle: Int) : Comparable<PitchBin> {
    private val num get() = angle.absoluteValue.prependZeros(ORIENTATION_BIN_NUM_DIGITS)
    override fun toString(): String {
        return when {
            angle == 0 -> "C$num"
            angle > 0  -> "U$num"
            else       -> "D$num"
        }
    }

    override fun compareTo(other: PitchBin): Int {
        return angle.compareTo(other.angle)
    }
}
