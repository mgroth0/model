package matt.model.data.unitless

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import matt.model.data.mathable.DoubleWrapper


/*I CANT USE VALUE CLASSES*/
/*https://youtrack.jetbrains.com/issue/KT-54513/java.lang.NoSuchMethodError-with-value-class-implementing-an-interface*/
/*10 months later... changing this back from data to value... does it work now?*/
/*

11 months after creating of original issue... This exact issue came up again in compnet (of course) with a NoSuchMethodError
Original I thought it was a classloader/plugin issue. But of course it was just this again.

I am once again changing this from a value class to a normal(data) class, because I am in a huge rush.
I will make a custom serializer though to serialize it like a value class, so if I ever really fix this and make it inline again all my serialized data will be compatible.


before doing anything read this:
https://kotlinlang.org/docs/inline-classes.html#mangling
sections: "mangling" and "calling from java code"


*/

private object TempUnitlessSerializerToMakeItCompatibleWithFutureInlineVersion : KSerializer<UnitLess> {
    override val descriptor by lazy { serialDescriptor<Double>() }

    override fun deserialize(decoder: Decoder): UnitLess {
        return UnitLess(decoder.decodeDouble())
    }

    override fun serialize(
        encoder: Encoder,
        value: UnitLess
    ) {
        encoder.encodeDouble(value.asDouble)
    }

}

@Serializable
data class UnitLess(override val asNumber: Double) : DoubleWrapper<UnitLess> {
    companion object {
        val ZERO = UnitLess(0.0)
        val ONE = UnitLess(1.0)
        val TWO = UnitLess(2.0)
    }

    override val asDouble get() = asNumber
    override fun fromDouble(d: Double): UnitLess {
        return UnitLess(d)
    }

    override fun toString(): String {
        return "$asNumber"
    }
}
