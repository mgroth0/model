package matt.model.data.hz



import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import matt.model.data.mathable.DoubleWrapper
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val Number.hz get() = Hz(toDouble())

/*SHOULD BE VALUE CLASS*/

object HzSerializer : KSerializer<Hz> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Hz", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Hz = Hz(decoder.decodeDouble())

    override fun serialize(
        encoder: Encoder,
        value: Hz
    ) {
        encoder.encodeDouble(value.asDouble)
    }

}

@Serializable(with = HzSerializer::class)
data class Hz(override val asNumber: Double) : DoubleWrapper<Hz> {

    companion object {
        val ZERO = Hz(0.0)
    }

    constructor(interval: Duration) : this(1.seconds / interval)

    override val asDouble get() = asNumber
    override fun fromDouble(d: Double): Hz = Hz(d)

    override fun toString(): String = "${asNumber}Hz"


    val interval get() = (1.seconds / asNumber)
}

