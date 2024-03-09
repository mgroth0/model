package matt.model.flowlogic.singlerunlambda

import matt.lang.sync.common.ReferenceMonitor
import matt.lang.sync.common.inSync

class SingleRunLambda(private val op: () -> Unit) : ReferenceMonitor {
    private var ran = false

    operator fun invoke() =
        inSync {
            if (!ran) {
                ran = true
                op()
            }
        }
}
