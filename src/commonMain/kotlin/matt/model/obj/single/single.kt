package matt.model.obj.single

import matt.lang.assertions.require.requireNot
import matt.lang.sync.ReferenceMonitor
import matt.lang.sync.inSync
import kotlin.reflect.KClass

//internal val singletons = mutableSetOf<KClass<*>>()
//dd
//@Synchronized
//fun Any.registerSingleton() {
//  require(this::class !in singletons) {
//	"tried to create 2 of registered singleton ${this::class.simpleName}"
//  }
//  singletons += this::class
//}

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

    operator fun invoke() = inSync {
        requireNot(called)
        op()
        called = true
    }


}

class SingleCallWithArg<A>(val op: (A) -> Unit = {}) : SingleCallBase() {
    operator fun invoke(arg: A) = inSync {
        requireNot(called)
        op(arg)
        called = true
    }

}
