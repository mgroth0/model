package matt.model.tostringbuilder

import kotlin.reflect.KClass

fun Any.toStringBuilder(
  map: Map<String, Any?>
): String {
  val realMap = map.toMutableMap()
  if (realMap.isEmpty()) {
	realMap["@"] = hashCode()
  }
  return this::class.firstSimpleName() + map.entries.joinToString(prefix = "[", postfix = "]") {
	"${it.key}=${it.value}"
  }
}

expect fun KClass<*>.firstSimpleName(): String