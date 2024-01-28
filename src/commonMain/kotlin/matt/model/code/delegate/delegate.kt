package matt.model.code.delegate

import matt.lang.assertions.require.requireEquals
import matt.lang.assertions.require.requireNot
import matt.lang.sync.ReferenceMonitor
import matt.lang.sync.inSync
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class ThrowingVetoable<V>(
    private var value: V,
    private val check: (V) -> Boolean
) : ReadWriteProperty<Any?, V> {
    init {
        require(check(value))
    }

    final override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = value

    final override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: V
    ) {
        require(check(value))
        this.value = value
    }
}

class LimitedInt(
    value: Int,
    private val min: Int = Int.MIN_VALUE,
    private val max: Int = Int.MAX_VALUE
) : ThrowingVetoable<Int>(
    value,
    { it in min..max }
)


class SimpleGetter<T, V>(private val o: V) : ReadOnlyProperty<T, V> {
    override fun getValue(
        thisRef: T,
        property: KProperty<*>
    ) = o
}

class SimpleVar<T, V>(
    private val getter: () -> V,
    private val setter: (V) -> Unit
) : ReadWriteProperty<T, V> {
    override fun getValue(
        thisRef: T,
        property: KProperty<*>
    ) = getter()

    override fun setValue(
        thisRef: T,
        property: KProperty<*>,
        value: V
    ) {
        setter(value)
    }
}


class CanOnlySetOnce<V> : ReadWriteProperty<Any?, V?>, ReferenceMonitor {
    private var didSet = false
    private var myValue: V? = null

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = myValue

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: V?
    ) = inSync {
        requireNot(didSet)
        didSet = true
        myValue = value
    }
}

class RequireAlwaysEverySetEqual<V> : ReadWriteProperty<Any?, V?>, ReferenceMonitor {
    private var didSet = false
    private var myValue: V? = null

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = myValue

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: V?
    ) = inSync {
        if (didSet) requireEquals(value, myValue)
        else {
            myValue = value
            didSet = true
        }
    }
}


