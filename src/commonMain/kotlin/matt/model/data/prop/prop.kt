package matt.model.data.prop

interface SimpleSuspendProperty<T : Any> {
    suspend fun get(): T?
    suspend fun set(value: T?)
}