package matt.model.await

interface Awaitable<T> {
  fun await(): T
}