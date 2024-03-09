package matt.model.flowlogic.keypass

import matt.lang.assertions.require.requireNot
import matt.lang.sync.common.ReferenceMonitor
import matt.lang.sync.common.inSync
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

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

    @PublishedApi
    internal fun setTheHeld(value: Boolean) {
        isHeld = value
    }

    val isNotHeld get() = !isHeld

    inline fun <R> with(op: () -> R): R {
        contract {
            callsInPlace(op, EXACTLY_ONCE)
        }
        inSync {
            setTheHeld(true)
            val r = op()
            setTheHeld(false)
            return r
        }
    }

    fun hold() =
        inSync {
            requireNot(isHeld)
            isHeld = true
        }

    fun release() =
        inSync {
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

    fun <R> with(op: () -> R): R =
        try {
            on()
            op()
        } finally {
            off()
        }

    suspend fun <R> suspendWith(op: suspend () -> R): R =
        try {
            on()
            op()
        } finally {
            off()
        }
}

class Monitor
