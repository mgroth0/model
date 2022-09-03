package matt.model.single

import kotlin.reflect.KClass

open class Singleton {

  companion object {
	private val instanced = mutableSetOf<KClass<out Singleton>>()
  }

  init {
	if (this::class in instanced) throw RuntimeException("can only create one instance of a singleton")
	else {
	  instanced.add(this::class)
	}
  }
}