package matt.model.latch.asyncloaded

import matt.lang.model.value.ValueWrapperIdea
import matt.lang.weak.lazyWeak
import matt.model.await.Awaitable
import matt.model.latch.SimpleLatch
import java.lang.Thread.State
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class DaemonLoadedValueOp<T>(name: String? = null, private val op: ()->T): Async<T>() {

  companion object {
	val threadIndex = AtomicInteger(0)
  }

  private val myThread by lazy {
	thread(
	  name = "DaemonLoadedValue Thread ${threadIndex.getAndIncrement()}",
	  start = false,
	  isDaemon = true
	) {
	  value = op()
	  openAndDisposeLatch()
	  if (name != null) println("finished loading $name")
	}
  }

  @Synchronized
  fun startLoading() {
	require(latch!!.isClosed)
	require(myThread.state == State.NEW)
	myThread.start()

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

open class LoadedValueSlot<T>: Async<T>() {
  @Synchronized
  fun putLoadedValue(t: T) {
	require(latch!!.isClosed)
	value = t
	openAndDisposeLatch()
  }
}

class DelegatedSlot<T: Any>: AsyncBase<T>() {


  private var getter: (()->T)? = null

  @Synchronized
  override fun await(): T {
	latch?.await()
	return getter!!.invoke()
  }

  @Synchronized
  fun putGetter(op: ()->T) {
	getter = op
	openAndDisposeLatch()
  }

  @Synchronized
  fun putLazyWeakGetter(op: ()->T) {
	val lw by lazyWeak {
	  op()
	}
	getter = { lw }
	openAndDisposeLatch()
  }
}

abstract class Async<T>: AsyncBase<T>(), ValueWrapperIdea {

  protected open var value: T? = null


  override fun await(): T {
	latch?.await()
	@Suppress("UNCHECKED_CAST")
	return value as T
  }
}

/*is this just a future? or a modified lazy?*/
abstract class AsyncBase<T>: Awaitable<T> {
  protected fun openAndDisposeLatch() {
	latch?.open()
	latch = null
  }

  protected var latch: SimpleLatch? = SimpleLatch()
	private set

}