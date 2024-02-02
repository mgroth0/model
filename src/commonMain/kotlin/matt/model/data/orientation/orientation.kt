package matt.model.data.orientation

import kotlinx.serialization.Serializable
import matt.model.data.sensemod.Degrees
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
        val C00C00 = BinnedOrientation(YawBin(0), PitchBin(0))
    }

    val label = "$yawBin$pitchBin"

    override fun toString() = label
    override fun compareTo(other: BinnedOrientation): Int = comparator.compare(this, other)
}


@JvmInline
@Serializable
value class YawBin(val angle: Int) : Comparable<YawBin> {
    constructor(angle: Degrees) : this(angle.value)

    private val num get() = angle.absoluteValue.prependZeros(ORIENTATION_BIN_NUM_DIGITS)
    override fun toString(): String = when {
        angle == 0 -> "C$num"
        angle > 0  -> "R$num"
        else       -> "L$num"
    }

    override fun compareTo(other: YawBin): Int = angle.compareTo(other.angle)
}

@JvmInline
@Serializable
value class PitchBin(val angle: Int) : Comparable<PitchBin> {
    constructor(angle: Degrees) : this(angle.value)

    private val num get() = angle.absoluteValue.prependZeros(ORIENTATION_BIN_NUM_DIGITS)
    override fun toString(): String = when {
        angle == 0 -> "C$num"
        angle > 0  -> "U$num"
        else       -> "D$num"
    }

    override fun compareTo(other: PitchBin): Int = angle.compareTo(other.angle)
}


interface MightBeNativeExtractFInterface
object NoNativeExtractFInterface : MightBeNativeExtractFInterface
