@file:JvmName("RectJvmAndroidKt")

package matt.model.data.rect

import java.awt.Rectangle
import java.awt.geom.Dimension2D
import java.awt.geom.Rectangle2D


fun UniformRect<Int>.toAwtRectangle() = Rectangle(x, y, width, height)
fun UniformRect<Double>.toAwtRectangle() = Rectangle2D.Double(x, y, width, height)


fun Dimension2D.toDoubleRectSize() = DoubleRectSize(width = width, height = height)