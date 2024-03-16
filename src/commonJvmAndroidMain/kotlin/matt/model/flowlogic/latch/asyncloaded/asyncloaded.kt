package matt.model.flowlogic.latch.asyncloaded

import matt.lang.assertions.require.requireEquals
import matt.lang.atomic.AtomicInt
import matt.lang.common.go
import matt.lang.model.value.Value
import matt.lang.model.value.ValueWrapperIdea
import matt.lang.service.ThreadProvider
import matt.lang.weak.common.lazyWeak
import matt.model.flowlogic.await.ThreadAwaitable
import matt.model.flowlogic.latch.LatchCancelled
import matt.model.flowlogic.latch.j.SimpleThreadLatch
import java.lang.Thread.State
import java.lang.Thread.State.NEW

class DaemonLoadedValueOp<T>(
    threadProvider: ThreadProvider,
    name: String? = null,

    private val op: () -> T
) : Async<T>() {

    companion object {
        val threadIndex = AtomicInt(0)
    }

    private val myThread by lazy {
        threadProvider.newThread(
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
        requireEquals(myThread.state, NEW)
        myThread.start()
    }

    @Synchronized
    fun startLoadingIfNotStarted() {
        if (myThread.state == State.NEW) myThread.start()
    }
}

open class SyncLoadedValueOp<T>(private val op: () -> T) : Async<T>() {

    @Synchronized
    fun calc(): T {
        require(latch!!.isClosed)
        value = op()
        openAndDisposeLatch()

        return value!!
    }
}

open class LoadedValueSlot<T> : Async<T>() {


    @Synchronized
    fun putLoadedValue(t: T) {
        require(latch!!.isClosed)
        value = t
        openAndDisposeLatch()
    }

    fun isDoneOrCancelled() = latch?.isOpen ?: true
}


class DelegatedSlot<T : Any> : AsyncBase<T>() {


    private var getter: (() -> T)? = null

    @Synchronized
    override fun await(): T {
        latch?.await()
        return getter!!.invoke()
    }

    @Synchronized
    fun putGetter(op: () -> T) {
        getter = op
        openAndDisposeLatch()
    }

    @Synchronized
    fun putLazyWeakGetter(op: () -> T) {
        val lw by lazyWeak {
            op()
        }
        getter = { lw }
        openAndDisposeLatch()
    }
}


abstract class Async<T> : AsyncBase<T>(), ValueWrapperIdea {

    private var setListeners: Lazy<MutableList<(T) -> Unit>>? = lazy { mutableListOf() }

    private var failure: LatchCancelled? = null

    @Synchronized
    fun cancel(e: Throwable? = null) {
        failure = LatchCancelled(cause = e)
        value = null
        cancelLatch(e)
    }

    @Synchronized
    fun cancel(message: String) {
        failure = LatchCancelled(message = message)
        value = null
        cancelLatch(message)
    }



    protected var value: T? = null
        @Synchronized
        get() {
            failure?.go { throw it }
            return field
        }
        @Synchronized set(value) {
            if (failure == null) {
                field = value
                setListeners?.go {
                    if (it.isInitialized()) {
                        it.value.forEach { operation ->
                            operation(value!!)
                        }
                    }
                    setListeners = null
                }
            } else {
                field = null
                if (value != null) {
                    println("WARNING: Async value was set to $value after it was cancelled. Overriding with null.")
                }
            }
        }


    fun wrappedLoadedValueOrNull() = if (latch?.isOpen != false) Value(value) else null

    final override fun await(): T {
        latch?.await()

        return value!!
    }

    @Synchronized
    fun whenReady(op: (T) -> Unit) {
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
abstract class AsyncBase<T> : ThreadAwaitable<T> {
    protected fun openAndDisposeLatch() {
        latch?.open()
        latch = null
    }

    protected fun cancelLatch(e: Throwable?) {
        latch?.cancel(e)
    }

    protected fun cancelLatch(message: String) {
        latch?.cancel(message)
    }

    protected var latch: SimpleThreadLatch? = SimpleThreadLatch()
        private set
}
