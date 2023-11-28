package matt.model.flowlogic.syncop

import matt.lang.anno.NullToReduceObjects
import matt.lang.function.Op
import matt.lang.sync.ReferenceMonitor
import matt.lang.sync.inSync


class AntiDeadlockSynchronizer : ReferenceMonitor {
    @PublishedApi
    internal var currentWorkerCount = 0

    @NullToReduceObjects
    @PublishedApi
    internal var opQueue: MutableList<Op>? = null


    inline fun useInternalData(op: Op) {
        inSync {
            currentWorkerCount += 1
        }
        op()
        inSync {
            currentWorkerCount -= 1
            if (currentWorkerCount == 0) {
                opQueue?.forEach { it() }
                opQueue = null
            }
        }
    }

    fun operateOnInternalDataNowOrLater(op: Op) = inSync {
        if (currentWorkerCount > 0) {
            (opQueue ?: mutableListOf<Op>().also {
                opQueue = it
            }) += op
        } else op()
    }

}