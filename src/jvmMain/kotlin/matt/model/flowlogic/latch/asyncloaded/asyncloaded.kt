package matt.model.flowlogic.latch.asyncloaded

import matt.lang.go
import matt.lang.model.value.Value
import matt.lang.model.value.ValueWrapperIdea
import matt.lang.weak.lazyWeak
import matt.model.flowlogic.await.Awaitable
import matt.model.flowlogic.latch.SimpleLatch
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

  @Synchronized
  fun startLoadingIfNotStarted() {
	if (myThread.state == State.NEW) myThread.start()
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

  fun isDone() = latch?.isOpen ?: true
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

  private var setListeners: Lazy<MutableList<(T)->Unit>>? = lazy { mutableListOf() }

  @Suppress("UNCHECKED_CAST")
  protected var value: T? = null
	@Synchronized set(value) {
	  field = value
	  setListeners?.go {
		if (it.isInitialized()) {
		  it.value.forEach { operation ->
			operation(value as T)
		  }
		}
		setListeners = null
	  }

	}


  fun wrappedLoadedValueOrNull() = if (latch!!.isOpen) Value(value) else null

  override fun await(): T {
	latch?.await()
	@Suppress("UNCHECKED_CAST")
	return value as T
  }

  @Synchronized
  fun whenReady(op: (T)->Unit) {
	val ready = latch?.isOpen ?: true
	if (ready) op(await())
	else {
	  setListeners!!.value.add {
		op(it)
	  }
	}
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