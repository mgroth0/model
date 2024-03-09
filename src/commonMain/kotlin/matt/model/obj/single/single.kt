package matt.model.obj.single

import matt.lang.assertions.require.requireNot
import matt.lang.sync.common.ReferenceMonitor
import matt.lang.sync.common.inSync
import kotlin.reflect.KClass

open class Singleton {

    companion object {
        private val instanced = mutableSetOf<KClass<out Singleton>>()
    }

    init {
        if (this::class in instanced) throw RuntimeException("can only create one instance of a singleton")
        else instanced.add(this::class)
    }
}

abstract class SingleCallBase : ReferenceMonitor {

    var called: Boolean = false
        get() {
            inSync {
                return field
            }
        }
        protected set(value) {
            inSync {
                field = value
            }
        }
}

class SingleCall(protected val op: () -> Unit = {}) : SingleCallBase() {

    operator fun invoke() =
        inSync {
            requireNot(called)
            op()
            called = true
        }
}

class SingleCallWithArg<A>(val op: (A) -> Unit = {}) : SingleCallBase() {
    operator fun invoke(arg: A) =
        inSync {
            requireNot(called)
            op(arg)
            called = true
        }
}
