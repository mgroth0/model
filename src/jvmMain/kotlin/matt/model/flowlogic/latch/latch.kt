package matt.model.flowlogic.latch

import matt.model.flowlogic.await.Awaitable
import matt.model.flowlogic.latch.LatchAwaitResult.LATCH_OPENED
import matt.model.flowlogic.latch.LatchAwaitResult.TIMEOUT
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeoutException
import kotlin.time.Duration

enum class LatchAwaitResult {
  LATCH_OPENED,
  TIMEOUT
}

class SimpleLatch: Awaitable<Unit> {


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
  fun openned() = apply {
	open()
  }
}