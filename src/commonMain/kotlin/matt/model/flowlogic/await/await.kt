package matt.model.flowlogic.await

import matt.lang.function.Consume
import matt.lang.model.value.ValueWrapperIdea

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