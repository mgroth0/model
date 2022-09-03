@file:JvmName("ByteJvmKt")

package matt.model.byte

import matt.model.byte.ByteSize.ByteUnit
import matt.model.byte.ByteSize.ByteUnit.B
import kotlin.experimental.and


actual class FormattedByteSize actual constructor(val num: Double, val unit: ByteUnit) {
  override fun toString(): String {
	return if (unit == B) "$num ${unit.name}"
	else "%.3f ${unit.name}".format(num)
  }
}


private val hexArray = "0123456789ABCDEF".toCharArray()

@Suppress("unused")
fun ByteArray.toHex(): String {
  val hexChars = CharArray(size*2)
  for (j in indices) {
	val v = (this[j] and 0xFF.toByte()).toInt()

	hexChars[j*2] = hexArray[v ushr 4]
	hexChars[j*2 + 1] = hexArray[v and 0x0F]
  }
  return String(hexChars)
}