package matt.model.code.delegate

import matt.lang.anno.OnlySynchronizedOnJvm
import matt.lang.require.requireEquals
import matt.lang.require.requireNot
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

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = value

    override fun setValue(
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


class CanOnlySetOnce<V> : ReadWriteProperty<Any?, V?> {
    private var didSet = false
    private var myValue: V? = null

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = myValue

    @OnlySynchronizedOnJvm
    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: V?
    ) {
        requireNot(didSet)
        didSet = true
        myValue = value
    }
}

class RequireAlwaysEverySetEqual<V> : ReadWriteProperty<Any?, V?> {
    private var didSet = false
    private var myValue: V? = null

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = myValue

    @OnlySynchronizedOnJvm
    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: V?
    ) {
        if (didSet) requireEquals(value, myValue)
        else {
            myValue = value
            didSet = true
        }
    }
}


