package matt.model.data.xyz

import matt.lang.NOT_IMPLEMENTED
import matt.model.data.mathable.Mathable
import matt.prim.str.joinWithCommas
import kotlin.math.absoluteValue


fun xyz(
    x: Number = 0,
    y: Number = 0,
    z: Number = 0
) = XYZ(x = x.toDouble(), y = y.toDouble(), z = z.toDouble())

data class XYZ(
    override val x: Double,
    override val y: Double,
    override val z: Double
) : Mathable<XYZ>, XYZBase<Double> {
    override fun div(n: Number): XYZ {
        TODO("Not yet implemented")
    }

    override fun times(n: Number): XYZ {
        return XYZ(x * n.toDouble(), y * n.toDouble(), z * n.toDouble())
    }


    override fun div(m: XYZ) = NOT_IMPLEMENTED


    override fun toString(): String {
        return withCommas()
    }

    override operator fun plus(m: XYZ): XYZ {
        return XYZ(x + m.x, y + m.y, z + m.z)
    }

    override operator fun minus(m: XYZ): XYZ {
        return XYZ(x - m.x, y - m.y, z - m.z)
    }

    operator fun minus(n: Number): XYZ {
        return XYZ(x - n.toDouble(), y - n.toDouble(), z - n.toDouble())
    }

    operator fun plus(n: Number): XYZ {
        return XYZ(x + n.toDouble(), y + n.toDouble(), z + n.toDouble())
    }


    override val abs: XYZ
        get() = XYZ(x = x.absoluteValue, y = y.absoluteValue, z = z.absoluteValue)

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


interface XYZBase<D> {
    val x: D
    val y: D
    val z: D


}

operator fun <D> XYZBase<D>.component1() = x
operator fun <D> XYZBase<D>.component2() = y
operator fun <D> XYZBase<D>.component3() = z
fun <D> XYZBase<D>.toList() = listOf(x, y, z)

const private val DEFAULT_TRIM_NULL_END = false

fun <D> XYZBase<D>.withCommas(trimNullEnd: Boolean = DEFAULT_TRIM_NULL_END): String {
    return when {
        trimNullEnd && z == null -> when {
            y == null -> when {
                x == null -> error("all are null. Is this really valid?")
                else      -> listOf(x)
            }

            else      -> listOf(x, y)
        }

        else                     -> toList()
    }.joinWithCommas()
}

fun <D> XYZBase<D>.asTuple(
    trimNullEnd: Boolean = DEFAULT_TRIM_NULL_END,
    trailingComma: Boolean = true
): String = "(${withCommas(trimNullEnd = trimNullEnd)}${if (trailingComma) "," else ""})"

