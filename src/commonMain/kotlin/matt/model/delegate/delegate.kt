package matt.model.delegate

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class SimpleGetter<T, V>(private val o: V): ReadOnlyProperty<T, V> {
  override fun getValue(thisRef: T, property: KProperty<*>) = o
}