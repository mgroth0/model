package matt.model.flowlogic.radio

import matt.lang.function.Consume


class RadioTransmitter<T> {
  private val listeners = mutableListOf<Consume<T>>()
  fun broadcast(t: T) {
	listeners.forEach { it.invoke(t) }
  }

  inner class Radio {
	fun listen(listener: Consume<T>) {
	  listeners += listener
	}
  }
}

