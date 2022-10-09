package matt.model.single

import matt.model.obj.single.singletons
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun deregisterAllSingletonsSubclassing(cls: KClass<*>) {
  singletons.removeAll { it.isSubclassOf(cls) }
}
