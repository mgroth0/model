package matt.model.flowlogic.await

import matt.lang.function.Consume
import matt.lang.model.value.ValueWrapperIdea

interface Awaitable<T>: ValueWrapperIdea {
  fun await(): T
}

interface SuspendAwaitable<T>: ValueWrapperIdea {
  suspend fun await(): T
}


interface Donable<T>: ValueWrapperIdea {
  fun whenDone(c: Consume<T>)
}