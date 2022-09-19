package matt.model.latch

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.time.Duration

class SimpleLatch {
  private val latch = CountDownLatch(1)
  fun await() = latch.await()
  fun await(timeout: Duration) = latch.await(timeout.inWholeMilliseconds, MILLISECONDS)
  fun open() = latch.countDown()
  val isOpen get() = latch.count == 0L
  val isClosed get() = !isOpen
}