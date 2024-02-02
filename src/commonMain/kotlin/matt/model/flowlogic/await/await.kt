package matt.model.flowlogic.await

import matt.lang.function.Consume
import matt.lang.model.value.ValueWrapperIdea
import matt.model.code.successorfail.Success
import matt.model.code.successorfail.SuccessOrFail

interface Awaitable<T>: ValueWrapperIdea

interface ThreadAwaitable<T>: Awaitable<T> {
    fun await(): T
}

interface SuspendAwaitable<T>: Awaitable<T> {
    suspend fun await(): T
}


interface Donable<T>: ValueWrapperIdea {
    fun whenDone(c: Consume<T>)
}


inline fun ThreadAwaitable<SuccessOrFail>.awaitAndRequireSuccessful(
    onFail: Consume<SuccessOrFail> = { error("Not successful: $it") }
) {
    val r = await()
    if (r !is Success) onFail(r)
}
