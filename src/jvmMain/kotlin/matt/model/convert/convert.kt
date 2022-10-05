@file:JvmName("ConvertJvmKt")
/*if I use classes only, don't need JvmName?*/
package matt.model.convert


@Suppress("unused")
val RATIO_TO_PERCENT_FORMATTER = object: StringConverter<Number> {
  override fun toString(t: Number): String {
	return "%.3f".format(t.toDouble()*100) + "%"
  }

  override fun fromString(s: String) = TODO()
}