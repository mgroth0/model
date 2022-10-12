package matt.model.latch.asyncloaded

import matt.model.await.Awaitable
import matt.model.latch.SimpleLatch
import java.lang.Thread.State

class DaemonLoadedValueOp<T>(name: String? = null, private val op: ()->T): Async<T>() {

  private val thread by lazy {
	Thread {
	  value = op()
	  openAndDisposeLatch()
	  if (name != null) println("finished loading $name")
	}.apply {
	  isDaemon = true
	}
  }

  @Synchronized
  fun startLoading() {
	require(latch!!.isClosed)
	require(thread.state == State.NEW)
	thread.start()

  }
}

open class SyncLoadedValueOp<T>(private val op: ()->T): Async<T>() {

  @Synchronized
  fun calc(): T {
	require(latch!!.isClosed)
	value = op()
	openAndDisposeLatch()
	@Suppress("UNCHECKED_CAST")
	return value as T
  }
}

open class LoadedValueSlot<T>(): Async<T>() {
  @Synchronized
  fun putLoadedValue(t: T) {
	require(latch!!.isClosed)
	value = t
	openAndDisposeLatch()
  }
}

open class LoadedThenCachedValueSlot<T>(): LoadedValueSlot<T>() {
  @Synchronized
  override fun await(): T {
	return getFromCache?.invoke() ?: super.await()!!
  }

  private var getFromCache: (()->T)? = null

  override var value: T? = null

  @Synchronized
  fun disposeValueAndSetCacheGetter(op: ()->T) {
	getFromCache = op
	value = null
  }
}

/*is this just a future? or a modified lazy?*/
abstract class Async<T>: Awaitable<T> {
  protected fun openAndDisposeLatch() {
	latch?.open()
	latch = null
  }

  protected var latch: SimpleLatch? = SimpleLatch()
	private set

  protected open var value: T? = null

  override fun await(): T {
	latch?.await()
	@Suppress("UNCHECKED_CAST")
	return value as T
  }
}
