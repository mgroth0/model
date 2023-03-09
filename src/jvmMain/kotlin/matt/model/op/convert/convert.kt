@file:JvmName("ConvertJvmKt")
/*if I use classes only, don't need JvmName?*/
package matt.model.op.convert

import matt.prim.byte.encodeToBase64
import matt.prim.str.decodeFromBase64


@Suppress("unused")
val RATIO_TO_PERCENT_FORMATTER = object: StringConverter<Number> {
  override fun toString(t: Number): String {
	return "%.3f".format(t.toDouble()*100) + "%"
  }

  override fun fromString(s: String) = TODO()
}



object Base64StringConverter: StringConverter<ByteArray> {
  override fun toString(t: ByteArray) = t.encodeToBase64()
  override fun fromString(s: String) = s.decodeFromBase64()
}