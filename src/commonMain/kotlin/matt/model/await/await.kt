package matt.model.await

import matt.lang.model.value.ValueWrapperIdea

interface Awaitable<T>: ValueWrapperIdea {
  fun await(): T
}