package matt.model.flowlogic.radio

import matt.lang.function.Consume
import matt.lang.sync.ReferenceMonitor
import matt.lang.sync.inSync


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

/*consider renaming it LiveList?*/
class Poster<T>: ReferenceMonitor {

    private val listeners = mutableListOf<Consume<T>>()

    private val board = mutableListOf<T>()

    fun post(t: T) = inSync {
        board += t
        listeners.forEach {
            it(t)
        }
    }

    inner class PostBoard {
        fun onEach(op: (T) -> Unit) {
            inSync(this@Poster) {
                board.forEach(op)
            }
            listeners += op
        }
    }

    fun onEach(op: (T) -> Unit) = PostBoard().onEach(op)

}