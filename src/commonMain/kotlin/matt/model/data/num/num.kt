package matt.model.data.num

import kotlinx.serialization.Serializable
import matt.lang.assertions.require.require
import kotlin.jvm.JvmInline


@Serializable
@JvmInline
value class PositiveInteger(
    val value: Int
) {
    init {
        check(value > 0) {
            "value must be positive, not $value"
        }
    }

    override fun toString(): String = value.toString()
}



@Serializable
@JvmInline
value class NonNegativeFloat(
    val value: Float
) {
    init {
        require(value) isGreaterThanOrEqualTo  0f
    }

    override fun toString(): String = value.toString()
}
