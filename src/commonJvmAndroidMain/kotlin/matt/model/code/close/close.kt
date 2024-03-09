package matt.model.code.close

abstract class SafeClosableProvider<T : AutoCloseable> {
    protected abstract fun instantiate(): T

    fun <R> use(op: T.() -> R): R {
        val instance = instantiate()
        return instance.use { op(it) }
    }
}
