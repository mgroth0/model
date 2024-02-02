package matt.model.flowlogic.latch

import matt.model.flowlogic.await.Awaitable

interface SimpleLatch : Awaitable<Unit> {
    fun open()
//    fun awaitBlocking()
}

class LatchCancelled(
    message: String? = null,
    cause: Throwable? = null,
) : Exception("Latch was cancelled" + (message?.let { ": $it" } ?: ""), cause)

enum class LatchAwaitResult {
    LATCH_OPENED,
    TIMEOUT,
}
