package matt.model.obj.boild

import kotlin.reflect.KClass

interface Builder<T: Any>

annotation class BuiltBy<T: Any>(val cls: KClass<out Builder<T>>)

interface DefaultNewFactory<T: Any>: Builder<T> {
  fun default_new(): T
}

interface DefaultNewWithCfgFactory<T: Any>: Builder<T> {
  fun defaultNew(cfg: T.()->Unit): T
}

abstract class DumbBuilderEnforcer<T: Any, B: Builder<out T>> public constructor() {
  abstract fun builderOf(r: T): B
}


interface Instantiator<T: Any>: Builder<T> {
  fun instance(): T
}

interface TypedIntantiator<T: Any>: Instantiator<T> {
  val creates: KClass<out T>
}



