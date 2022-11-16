package matt.model.rect

data class RectSize(
  val width: Double,
  val height: Double
)


data class Rect<U>(
  val x: U,
  val y: U,
  val width: U,
  val height: U
)