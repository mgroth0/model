package matt.model.xyz

fun xyz(x: Number = 0, y: Number = 0, z: Number = 0) = XYZ(x = x.toDouble(), y = y.toDouble(), z = z.toDouble())
data class XYZ(val x: Double, val y: Double, val z: Double) {
  override fun toString(): String {
	return "$x,$y,$z"
  }

  operator fun plus(xyz: XYZ): XYZ {
	return XYZ(x + xyz.x, y + xyz.y, z + xyz.z)
  }
}