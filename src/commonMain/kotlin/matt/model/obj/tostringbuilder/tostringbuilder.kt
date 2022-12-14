

package matt.model.obj.tostringbuilder

import kotlin.reflect.KClass

fun Any.toStringBuilder(
  vararg kvPairs: Pair<String, Any?>
) = toStringBuilder(mapOf(*kvPairs))


fun Any.toStringBuilder(
  map: Map<String, Any?> = mapOf()
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