package matt.model.obj.single

import kotlin.jvm.Synchronized
import kotlin.reflect.KClass

private val singletons = mutableSetOf<KClass<*>>()

@Synchronized
fun Any.registerSingleton() {
  require(this::class !in singletons) {
	"tried to create 2 of registered singleton ${this::class.simpleName}"
  }
  singletons += this::class
}

open class Singleton {

  companion object {
	private val instanced = mutableSetOf<KClass<out Singleton>>()
  }

  init {
	if (this::class in instanced) throw RuntimeException("can only create one instance of a singleton")
	else instanced.add(this::class)
  }
}

abstract class SingleCallBase {

  var called: Boolean = false
	@Synchronized get
	@Synchronized protected set


}

class SingleCall(protected val op: ()->Unit = {}): SingleCallBase() {

  @Synchronized
  operator fun invoke() {
	require(!called)
	op()
	called = true
  }


}

class SingleCallWithArg<A>(val op: (A)->Unit = {}): SingleCallBase() {
  @Synchronized
  operator fun invoke(arg: A) {
	require(!called)
	op(arg)
	called = true
  }

}