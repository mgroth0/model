package matt.model.flowlogic.keypass

import matt.lang.assertions.require.requireNot
import matt.lang.sync.ReferenceMonitor
import matt.lang.sync.inSync

class KeyPass : ReferenceMonitor {

    var isHeld = false
        private set(value) {
            inSync {
                field = value
            }

        }
        get() {
            inSync {
                return field
            }
        }

    val isNotHeld get() = !isHeld

    fun <R> with(op: () -> R): R = inSync {
        isHeld = true
        val r = op()
        isHeld = false
        return r
    }

    fun hold() = inSync {
        requireNot(isHeld)
        isHeld = true
    }

    fun release() = inSync {
        require(isHeld)
        isHeld = false
    }
}

interface Indicator {
    val isOn: Boolean
}

val Indicator.isOff get() = !isOn

class IndicatorController() : Indicator {
    override var isOn: Boolean = false
        private set

    fun on() {
        isOn = true
    }

    fun off() {
        isOn = false
    }

    fun <R> with(op: () -> R): R {
        return try {
            on()
            op()
        } finally {
            off()
        }
    }

    suspend fun <R> suspendWith(op: suspend () -> R): R {
        return try {
            on()
            op()
        } finally {
            off()
        }
    }
}

class Monitor