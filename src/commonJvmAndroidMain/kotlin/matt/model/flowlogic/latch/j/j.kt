
package matt.model.flowlogic.latch.j

import matt.lang.common.go
import matt.lang.function.Op
import matt.lang.function.Produce
import matt.model.code.successorfail.CodeFailedReturn
import matt.model.code.successorfail.Fail
import matt.model.code.successorfail.FailableReturn
import matt.model.code.successorfail.Success
import matt.model.code.successorfail.SuccessOrFail
import matt.model.code.successorfail.SuccessfulReturn
import matt.model.flowlogic.await.ThreadAwaitable
import matt.model.flowlogic.latch.LatchAwaitResult
import matt.model.flowlogic.latch.LatchAwaitResult.LATCH_OPENED
import matt.model.flowlogic.latch.LatchAwaitResult.TIMEOUT
import matt.model.flowlogic.latch.LatchCancelled
import matt.model.flowlogic.latch.SimpleLatch
import matt.model.flowlogic.latch.asyncloaded.LoadedValueSlot
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeoutException
import kotlin.time.Duration

class SimpleThreadLatch : SimpleLatch, ThreadAwaitable<Unit> {
    private var failure: LatchCancelled? = null

    fun cancel(e: Throwable? = null) {
        if (failure != null) {
            println("warning: latch already has failure")
        }
        failure = LatchCancelled(cause = e)
        open()
    }

    fun cancel(message: String) {
        if (failure != null) {
            println("warning: latch already has failure")
        }
        failure = LatchCancelled(message = message)
        open()
    }

    private val latch = CountDownLatch(1)

    override fun await() {
        latch.await()
        failure?.go { throw it }
    }

    fun await(timeout: Duration): LatchAwaitResult =
        if (latch.await(timeout.inWholeMilliseconds, MILLISECONDS)) {
            failure?.go { throw it }
            LATCH_OPENED
        } else {
            TIMEOUT
        }

    fun awaitOrThrow(timeout: Duration) {
        when (await(timeout)) {
            LATCH_OPENED -> Unit
            TIMEOUT -> throw TimeoutException("timeout after waiting $timeout for $this")
        }
    }

    override fun open() = latch.countDown()

    val isOpen get() = latch.count == 0L
    val isClosed get() = !isOpen

    fun opened() =
        apply {
            open()
        }
}

class OpResultHandler(private val failMessage: String) : ThreadAwaitable<SuccessOrFail> {
    private val result = LoadedValueSlot<SuccessOrFail>()

    fun handle(op: Op) {
        var r: SuccessOrFail = Fail(failMessage)
        try {
            op()
            r = Success
        } finally {
            result.putLoadedValue(r)
        }
    }

    override fun await() = result.await()
}

class OpResultWithReturnValueHandler<R>(private val failMessage: String) : ThreadAwaitable<FailableReturn<R>> {
    private val result = LoadedValueSlot<FailableReturn<R>>()

    fun handle(op: Produce<R>) {
        var r: FailableReturn<R>? = null
        try {
            r = SuccessfulReturn(op())
        } finally {
            result.putLoadedValue(r ?: CodeFailedReturn(failMessage))
        }
    }

    override fun await() = result.await()
}
