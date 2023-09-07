package matt.model.flowlogic.keypass

import matt.lang.anno.OnlySynchronizedOnJvm
import matt.lang.require.requireNot

class  KeyPass {

    var isHeld = false
        @OnlySynchronizedOnJvm private set
        @OnlySynchronizedOnJvm get

    val isNotHeld get() = !isHeld

    @OnlySynchronizedOnJvm
    fun <R> with(op: () -> R): R {
        isHeld = true
        val r = op()
        isHeld = false
        return r
    }

    @OnlySynchronizedOnJvm
    fun hold() {
        requireNot(isHeld)
        isHeld = true
    }

    @OnlySynchronizedOnJvm
    fun release() {
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