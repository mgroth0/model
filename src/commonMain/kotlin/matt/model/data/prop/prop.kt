package matt.model.data.prop

import matt.lang.anno.Open
import matt.lang.convert.BiConverter
import kotlin.reflect.KProperty

interface SimpleSuspendProperty<T : Any> {
    suspend fun get(): T?
    suspend fun set(value: T?)

    @Open
    suspend fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T? = get()

    @Open
    suspend fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T?
    ) {
        set(value)
    }
}

suspend fun <T : Any> SimpleSuspendProperty<T>.nonSynchronizedGetOrPut(op: () -> T): T {
    val t = get()
    return if (t == null) {
        val r = op()
        set(r)
        r
    } else t
}

suspend fun <T : Any> SimpleSuspendProperty<T>.nonSynchronizedGetOrPutIfNotNull(op: () -> T?): T? {
    val t = get()
    return if (t == null) {
        val r = op()
        if (r != null) set(r)
        r
    } else t
}


class ConvertedSuspendProperty<S : Any, T : Any>(
    private val prop: SimpleSuspendProperty<S>,
    private val converter: BiConverter<S, T>
) : SimpleSuspendProperty<T> {
    override suspend fun get(): T? =
        prop.get()?.let {
            converter.convertToB(it)
        }

    override suspend fun set(value: T?) {
        prop.set(value?.let { converter.convertToA(it) })
    }
}
