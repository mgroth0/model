@file:JvmName("SingleJvmKt")

package matt.model.obj.single

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun deregisterAllSingletonsSubclassing(cls: KClass<*>) {
  singletons.removeAll { it.isSubclassOf(cls) }
}
