package matt.model.data.prop

interface SimpleSuspendProperty<T : Any> {
    suspend fun get(): T?
    suspend fun set(value: T?)
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
        if (r!=null) set(r)
        r
    } else t
}