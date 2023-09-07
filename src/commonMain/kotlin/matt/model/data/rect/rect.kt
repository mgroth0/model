package matt.model.data.rect

data class RectSize(
    val width: Double,
    val height: Double
)

data class IntRectSize(
    val width: Int,
    val height: Int
)


data class Rect<U>(
    val x: U,
    val y: U,
    val width: U,
    val height: U
)