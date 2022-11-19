package matt.model.flowlogic.await

import matt.lang.model.value.ValueWrapperIdea

interface Awaitable<T>: ValueWrapperIdea {
  fun await(): T
}