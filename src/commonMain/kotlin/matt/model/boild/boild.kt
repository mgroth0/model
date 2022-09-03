package matt.model.boild

import kotlin.reflect.KClass

interface Builder<T: Any>

annotation class BuiltBy<T: Any>(val cls: KClass<out Builder<T>>)

public interface DefaultNewFactory<T: kotlin.Any>: Builder<T> {
  public abstract fun default_new(): T
}

public interface DefaultNewWithCfgFactory<T: kotlin.Any>: Builder<T> {
  public abstract fun defaultNew(cfg: T.()->kotlin.Unit): T
}

public abstract class DumbBuilderEnforcer<T: kotlin.Any, B: Builder<out T>> public constructor() {
  public abstract fun builderOf(r: T): B
}


public interface Instantiator<T: kotlin.Any>: Builder<T> {
  public abstract fun instance(): T
}

public interface TypedIntantiator<T: kotlin.Any>: Instantiator<T> {
  public abstract val creates: kotlin.reflect.KClass<out T>
}



