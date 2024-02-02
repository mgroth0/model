package matt.model.data.rect

import kotlinx.serialization.Serializable
import matt.lang.idea.PointIdea2
import matt.lang.idea.PointIdea3
import matt.lang.safeconvert.verifyToUInt
import kotlin.jvm.JvmName

data class GenericPoint<out X, out Y>(
    override val x: X,
    override val y: Y
) : PointIdea2<X, Y>


/*naming this function is a complete guess. There may be a more formal name.*/
fun <X, Y> GenericPoint<X, Y>.invert(): GenericPoint<Y, X> = GenericPoint(x = y, y = x)


interface RectSize<X, Y> {
    val width: X
    val height: Y
}

interface UniformRectSize<T> : RectSize<T, T>


fun UniformRectSize<Int>.toUnitRectangle(): IntRect {
    check(width > 0)
    check(height > 0)
    return IntRect(
        x = 0,
        y = 0,
        height = height,
        width = width
    )
}


@JvmName("contains1")
operator fun UniformRectangle<UInt>.contains(other: UniformRectangle<UInt>): Boolean = other.x >= x && other.y >= y && other.endX <= endX && other.endY <= endY

@JvmName("contains2")
operator fun UniformRectangle<Int>.contains(other: UniformRectangle<Int>): Boolean {

    checkSizeIsNonNegative()
    other.checkSizeIsNonNegative()
    return other.x >= x && other.y >= y && other.endX <= endX && other.endY <= endY
}

interface Rectangle<X, Y> : RectSize<X, Y> {
    val x: X
    val y: Y
}

interface UniformRectangle<T> : Rectangle<T, T>, UniformRectSize<T>


@get:JvmName("endX1")
val UniformRectangle<UInt>.endX get() = x + width

@get:JvmName("endY1")
val UniformRectangle<UInt>.endY get() = y + height


@get:JvmName("endX2")
val UniformRectangle<Int>.endX: Int
    get() {
        check(width >= 0)
        return x + width
    }

@get:JvmName("endY2")
val UniformRectangle<Int>.endY: Int
    get() {
        check(height >= 0)
        return y + height
    }

fun <T> UniformRectSize<T>.originatingAt(point: PointIdea3<T>) =
    UniformRect(x = point.x, y = point.y, width = width, height = height)

fun <T> UniformRectSize<T>.originatingAt(
    x: T,
    y: T
) = UniformRect(x = x, y = y, width = width, height = height)

data class GenericRectSizeAnyType<X, Y>(
    override val width: X,
    override val height: Y
) : RectSize<X, Y>

fun <T> SquareSize(side: T) = GenericRectSize(side, side)
data class GenericRectSize<T>(
    override val width: T,
    override val height: T
) : UniformRectSize<T>

fun DoubleSquareSize(side: Double) = DoubleRectSize(side, side)
data class DoubleRectSize(
    override val width: Double,
    override val height: Double
) : UniformRectSize<Double> {
    companion object {
        val ZERO = DoubleRectSize(0.0, 0.0)
    }

    fun toIntSize() = IntRectSize(width = width.toInt(), height = height.toInt())
}

fun IntSquareSize(side: Int) = IntRectSize(side, side)
data class IntRectSize(
    override val width: Int,
    override val height: Int
) : UniformRectSize<Int> {
    companion object {
        val ZERO = IntRectSize(0, 0)
    }

    fun toDoubleSize() = DoubleRectSize(width = width.toDouble(), height = height.toDouble())
}


data class UniformRect<U>(
    override val x: U,
    override val y: U,
    override val width: U,
    override val height: U
) : UniformRectangle<U> {
    constructor(
        origin: PointIdea3<U>,
        size: UniformRectSize<U>
    ) : this(x = origin.x, y = origin.y, width = size.width, height = size.height)
}


data class Rect<X, Y>(
    override val x: X,
    override val y: Y,
    override val width: X,
    override val height: Y
) : Rectangle<X, Y> {
    constructor(
        origin: PointIdea2<X, Y>,
        size: RectSize<X, Y>
    ) : this(x = origin.x, y = origin.y, width = size.width, height = size.height)
}


fun UniformRectangle<Int>.isAllEven() = x % 2 == 0 && y % 2 == 0 && width % 2 == 0 && height % 2 == 0
fun UniformRectangle<Int>.verifyToUIntRect() = UIntRect(
    x = x.verifyToUInt(),
    y = y.verifyToUInt(),
    width = width.verifyToUInt(),
    height = height.verifyToUInt()
)

fun UniformRectangle<Int>.checkAllNonNegative() {
    check(x >= 0)
    check(y >= 0)
    check(width >= 0)
    check(height >= 0)
}

fun UniformRectangle<Int>.checkSizeIsPositive() {
    check(width > 0)
    check(height > 0)
}

fun UniformRectangle<Int>.checkSizeIsNonNegative() {
    check(width >= 0)
    check(height >= 0)
}

typealias IntRect = UniformRect<Int>
typealias UIntRect = UniformRect<UInt>
typealias DoubleRect = UniformRect<Double>

fun <R : UniformRectangle<Double>> R.takeIfFinite() =
    takeIf { x.isFinite() && y.isFinite() && width.isFinite() && height.isFinite() }

data class IntPoint(
    override val x: Int,
    override val y: Int
) : PointIdea3<Int>

data class DoublePoint(
    override val x: Double,
    override val y: Double
) : PointIdea3<Double>

@Serializable
data class Box(
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int
) : UniformRectangle<Int> {
    init {
        check(height > 0 && width > 0) {
            "width and height must be positive: x=$x,y=$y,width=$width,height=$height"
        }
    }
}

@get:JvmName("widthHeightRatio1")
val UniformRectangle<Int>.widthHeightRatio get() = width.toDouble() / height

@get:JvmName("widthHeightRatio2")
val UniformRectangle<Double>.widthHeightRatio get() = width / height


@Serializable
data class DoubleBox(
    override val x: Double,
    override val y: Double,
    override val width: Double,
    override val height: Double
) : UniformRectangle<Double> {
    init {
        check(height > 0 && width > 0) {
            "width and height must be positive: x=$x,y=$y,width=$width,height=$height"
        }
    }
}
