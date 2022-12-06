package matt.model.data.xyz

import matt.lang.NOT_IMPLEMENTED
import matt.model.data.mathable.Mathable

fun xyz(x: Number = 0, y: Number = 0, z: Number = 0) = XYZ(x = x.toDouble(), y = y.toDouble(), z = z.toDouble())
data class XYZ(val x: Double, val y: Double, val z: Double): Mathable<XYZ> {
  override fun div(n: Number): XYZ {
	TODO("Not yet implemented")
  }

  override fun times(n: Number): XYZ {
	return XYZ(x*n.toDouble(), y*n.toDouble(), z*n.toDouble())
  }

  override fun div(m: XYZ) = NOT_IMPLEMENTED

  override fun toString(): String {
	return "$x,$y,$z"
  }

  override operator fun plus(m: XYZ): XYZ {
	return XYZ(x + m.x, y + m.y, z + m.z)
  }

  override operator fun minus(m: XYZ): XYZ {
	return XYZ(x - m.x, y - m.y, z - m.z)
  }


}


enum class Dim2D { X, Y }
enum class Dim3D { X, Y, Z }