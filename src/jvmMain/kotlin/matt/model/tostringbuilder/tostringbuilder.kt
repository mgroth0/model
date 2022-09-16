@file:JvmName("ToStringBuilderJvmKt")

package matt.model.tostringbuilder

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.jvm.isAccessible

fun Any.toStringBuilder(
  vararg props: KProperty<*>
): String {
  return toStringBuilder(props.associate {
	it.name to it.apply { isAccessible = true }.getter.call()
  })
}

fun Any.toStringBuilder(
  vararg kvPairs: Pair<String, Any?>
) = toStringBuilder(mapOf(*kvPairs))


//fun Any.matt.model.tostringbuilder.toStringBuilder(vararg values: Pair<String, Any?>): String {
//  val suffix = if (values.isEmpty()) "@" + this.hashCode() else values.joinToString(" ") {
//	it.first + "=" + it.second
//  }
//  return "${this::class.simpleName} [$suffix]"
//}


actual fun KClass<*>.firstSimpleName() = this.simpleName ?: this.allSuperclasses.first().simpleName!!