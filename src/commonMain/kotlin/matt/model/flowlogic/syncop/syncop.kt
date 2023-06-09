package matt.model.flowlogic.syncop

import matt.lang.anno.NullToReduceObjects
import matt.lang.anno.OnlySynchronizedOnJvm
import matt.lang.function.Op
import matt.lang.sync.inSync


class AntiDeadlockSynchronizer {
    @PublishedApi
    internal var currentWorkerCount = 0

    @NullToReduceObjects
    @PublishedApi
    internal var opQueue: MutableList<Op>? = null


    inline fun useInternalData(op: Op) {
        inSync(this) {
            currentWorkerCount += 1
        }
        op()
        inSync(this) {
            currentWorkerCount -= 1
            if (currentWorkerCount == 0) {
                opQueue?.forEach { it() }
                opQueue = null
            }
        }
    }

    @OnlySynchronizedOnJvm
    fun operateOnInternalDataNowOrLater(op: Op) {
        if (currentWorkerCount > 0) {
            (opQueue ?: mutableListOf<Op>().also {
                opQueue = it
            }) += op
        } else op()
    }

}