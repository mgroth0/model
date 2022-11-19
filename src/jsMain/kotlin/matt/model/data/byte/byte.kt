package matt.model.data.byte

import matt.model.data.byte.ByteSize.ByteUnit
import matt.model.data.byte.ByteSize.ByteUnit.B
import kotlin.math.roundToLong

actual class FormattedByteSize actual constructor(val num: Double, val unit: ByteUnit) {
  override fun toString(): String {
	return if (unit == B) "$num ${unit.name}"
	else "${(num*1000).roundToLong()/1000.0} ${unit.name}"
  }
}