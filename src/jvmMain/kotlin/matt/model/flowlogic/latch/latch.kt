package matt.model.flowlogic.latch

import matt.lang.function.Op
import matt.lang.function.Produce
import matt.model.code.successorfail.Fail
import matt.model.code.successorfail.FailableReturn
import matt.model.code.successorfail.FailedReturn
import matt.model.code.successorfail.Success
import matt.model.code.successorfail.SuccessOrFail
import matt.model.code.successorfail.SuccessfulReturn
import matt.model.flowlogic.await.Awaitable
import matt.model.flowlogic.latch.LatchAwaitResult.LATCH_OPENED
import matt.model.flowlogic.latch.LatchAwaitResult.TIMEOUT
import matt.model.flowlogic.latch.asyncloaded.LoadedValueSlot
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeoutException
import kotlin.time.Duration

enum class LatchAwaitResult {
    LATCH_OPENED,
    TIMEOUT
}

class SimpleLatch : Awaitable<Unit> {


    private val latch = CountDownLatch(1)
    override fun await() = latch.await()
    fun await(timeout: Duration): LatchAwaitResult {
        return if (latch.await(timeout.inWholeMilliseconds, MILLISECONDS)) LATCH_OPENED
        else TIMEOUT
    }

    fun awaitOrThrow(timeout: Duration) {
        when (await(timeout)) {
            LATCH_OPENED -> Unit
            TIMEOUT      -> throw TimeoutException("timeout after waiting $timeout for $this")
        }
    }

    fun open() = latch.countDown()
    val isOpen get() = latch.count == 0L
    val isClosed get() = !isOpen
    fun opened() = apply {
        open()
    }
}


class OpResultHandler(private val failMessage: String) : Awaitable<SuccessOrFail> {
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


class OpResultWithReturnValueHandler<R>(private val failMessage: String) : Awaitable<FailableReturn<R>> {
    private val result = LoadedValueSlot<FailableReturn<R>>()

    fun handle(op: Produce<R>) {
        var r: FailableReturn<R> = FailedReturn<R>(failMessage)
        try {
            r = SuccessfulReturn<R>(op())
        } finally {
            result.putLoadedValue(r)
        }
    }

    override fun await() = result.await()

}
