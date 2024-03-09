package matt.model.data.xyz

import kotlinx.serialization.Serializable
import matt.lang.common.NOT_IMPLEMENTED
import matt.lang.jpy.ExcludeFromPython
import matt.lang.jpy.PyClass
import matt.model.data.mathable.Mathable
import matt.prim.str.joinWithCommas
import kotlin.math.absoluteValue

fun xyz(
    x: Number = 0,
    y: Number = 0,
    z: Number = 0
) = Xyz(x = x.toDouble(), y = y.toDouble(), z = z.toDouble())

@Serializable
@PyClass
data class Xyz(
    override val x: Double,
    override val y: Double,
    override val z: Double
) : Mathable<Xyz>, XYZBase<Double> {

    companion object {
        val ZERO = Xyz(0.0, 0.0, 0.0)
    }

    @ExcludeFromPython
    override fun div(n: Number): Xyz {
        TODO()
    }

    @ExcludeFromPython
    override fun times(n: Number): Xyz = Xyz(x * n.toDouble(), y * n.toDouble(), z * n.toDouble())

    @ExcludeFromPython
    override fun div(m: Xyz) = NOT_IMPLEMENTED

    override fun toString(): String = withCommas()

    @ExcludeFromPython
    override operator fun plus(m: Xyz): Xyz = Xyz(x + m.x, y + m.y, z + m.z)

    @ExcludeFromPython
    override operator fun minus(m: Xyz): Xyz = Xyz(x - m.x, y - m.y, z - m.z)

    @ExcludeFromPython
    operator fun minus(n: Number): Xyz = Xyz(x - n.toDouble(), y - n.toDouble(), z - n.toDouble())

    @ExcludeFromPython
    operator fun plus(n: Number): Xyz = Xyz(x + n.toDouble(), y + n.toDouble(), z + n.toDouble())

    @ExcludeFromPython
    override val abs: Xyz
        get() = Xyz(x = x.absoluteValue, y = y.absoluteValue, z = z.absoluteValue)

    override fun floatingPointDiv(m: Xyz): Double {
        TODO()
    }
}

enum class Dim2D { X, Y }

enum class Dim3D { X, Y, Z }

fun <D> genericXyz(
    x: D? = null,
    y: D? = null,
    z: D? = null
) = GenericXYZ(x = x, y = y, z = z)

data class GenericXYZ<D>(
    override val x: D,
    override val y: D,
    override val z: D
) : XYZBase<D>

fun <D, R> GenericXYZ<D>.map(op: (D) -> R) = GenericXYZ(x = op(x), y = op(y), z = op(z))

interface XYZBase<D> {
    val x: D
    val y: D
    val z: D
}

operator fun <D> XYZBase<D>.component1() = x

operator fun <D> XYZBase<D>.component2() = y

operator fun <D> XYZBase<D>.component3() = z

fun <D> XYZBase<D>.toList() = listOf(x, y, z)

private const val DEFAULT_TRIM_NULL_END = false

fun <D> XYZBase<D>.withCommas(trimNullEnd: Boolean = DEFAULT_TRIM_NULL_END): String =
    when {
        trimNullEnd && z == null ->
            when {
                y == null ->
                    when {
                        x == null -> error("all are null. Is this really valid?")
                        else -> listOf(x)
                    }

                else -> listOf(x, y)
            }

        else -> toList()
    }.joinWithCommas()

fun <D> XYZBase<D>.asTuple(
    trimNullEnd: Boolean = DEFAULT_TRIM_NULL_END,
    trailingComma: Boolean = true
): String = "(${withCommas(trimNullEnd = trimNullEnd)}${if (trailingComma) "," else ""})"
