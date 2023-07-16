package matt.model.data.prop

interface SimpleSuspendProperty<T : Any> {
    suspend fun get(): T?
    suspend fun set(value: T?)
}

suspend fun <T : Any> SimpleSuspendProperty<T>.unsynchronizedGetOrPut(op: () -> T): T {
    val t = get()
    return if (t == null) {
        val r = op()
        set(r)
        r
    } else t
}