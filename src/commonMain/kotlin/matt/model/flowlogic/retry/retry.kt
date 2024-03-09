package matt.model.flowlogic.retry

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Retryable<V : Any>(private val op: () -> V?) : ReadOnlyProperty<Any?, V?> {
    private var value: V? = null

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): V? {
        if (value == null) {
            value = op()
        }
        return value
    }
}
