package matt.model.code.delegate

import kotlin.jvm.Synchronized
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SimpleGetter<T, V>(private val o: V): ReadOnlyProperty<T, V> {
  override fun getValue(thisRef: T, property: KProperty<*>) = o
}


class CanOnlySetOnce<V>: ReadWriteProperty<Any?, V?> {
  private var didSet = false
  private var myValue: V? = null

  override fun getValue(thisRef: Any?, property: KProperty<*>) = myValue

  @Synchronized
  override fun setValue(thisRef: Any?, property: KProperty<*>, value: V?) {
	require(!didSet)
	didSet = true
	myValue = value
  }
}

class RequireAlwaysEverySetEqual<V>: ReadWriteProperty<Any?, V?> {
  private var didSet = false
  private var myValue: V? = null

  override fun getValue(thisRef: Any?, property: KProperty<*>) = myValue

  @Synchronized
  override fun setValue(thisRef: Any?, property: KProperty<*>, value: V?) {
	if (didSet) require(value == myValue)
	else {
	  myValue = value
	  didSet = true
	}
  }
}