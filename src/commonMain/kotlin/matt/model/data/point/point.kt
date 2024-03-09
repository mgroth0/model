package matt.model.data.point

import kotlinx.serialization.Serializable
import matt.lang.anno.Open
import matt.lang.idea.PointIdea3
import matt.math.langg.arithmetic.exp.sq
import matt.model.data.xyz.Dim2D
import matt.model.data.xyz.Dim2D.X
import matt.model.data.xyz.Dim2D.Y
import kotlin.math.sqrt


@Serializable
data class BasicPoint(
    override val x: Double,
    override val y: Double
) : Point {
    constructor(
        x: Number,
        y: Number
    ) : this(x = x.toDouble(), y = y.toDouble())

    fun normDist(other: BasicPoint) = sqrt((x - other.x).sq() + (y - other.y).sq())

    override val xDouble get() = x
    override val yDouble get() = y
    override fun clone(
        newX: Number?,
        newY: Number?
    ): Point = copy(x = newX?.toDouble() ?: x, y = newY?.toDouble() ?: y)

    override fun toBasicPoint(): BasicPoint = this
}


interface Point : PointIdea3<Any> {
    @Open
    fun getDim(dim: Dim2D) =
        when (dim) {
            X -> x
            Y -> y
        }

    @Open
    fun getDimDouble(dim: Dim2D) =
        when (dim) {
            X -> xDouble
            Y -> yDouble
        }

    @Open
    fun cloneWithNewDim(
        dim: Dim2D,
        newValue: Double
    ): Point =
        when (dim) {
            X -> clone(newX = newValue)
            Y -> clone(newY = newValue)
        }


    override val x: Any
    override val y: Any

    val xDouble: Double
    val yDouble: Double



    fun clone(
        newX: Number? = null,
        newY: Number? = null
    ): Point

    fun toBasicPoint(): BasicPoint
}
