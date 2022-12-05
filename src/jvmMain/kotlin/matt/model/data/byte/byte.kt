@file:JvmName("ByteJvmKt")

package matt.model.data.byte

import matt.model.data.byte.ByteSize.ByteUnit
import matt.model.data.byte.ByteSize.ByteUnit.B


actual class FormattedByteSize actual constructor(val num: Double, val unit: ByteUnit) {
  override fun toString(): String {
	return if (unit == B) "$num ${unit.name}"
	else "%.3f ${unit.name}".format(num)
  }
}


