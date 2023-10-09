package matt.model.data.rect

data class RectSize(
    val width: Double,
    val height: Double
)

data class IntRectSize(
    val width: Int,
    val height: Int
) {
    companion object {
        fun squareWithDim(side: Int) = IntRectSize(width = side, height = side)
    }
}


data class Rect<U>(
    val x: U,
    val y: U,
    val width: U,
    val height: U
)