package matt.model.flowlogic.singlerunlambda

import matt.lang.sync.ReferenceMonitor
import matt.lang.sync.inSync

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
