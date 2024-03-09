package matt.model.obsmod.proceeding

import matt.lang.idea.ProceedingIdea
import matt.model.flowlogic.startstop.Startable
import matt.model.obsmod.proceeding.Proceeding.Status
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.obs.bindings.bool.ObsB
import matt.obs.bindings.comp.eq
import matt.obs.prop.ObsVal
import matt.obs.prop.writable.VarProp
import matt.reflect.tostring.PropReflectingStringableClass


interface Proceeding: ProceedingIdea, Startable {
    val name: String
    val startButtonLabel: String
    val canStart: ObsB
    val status: ObsVal<Status>
    val message: ObsVal<String>
    val isOff: ObsB

    enum class Status {
        OFF,
        STARTING,
        RUNNING,
        STOPPING
    }
}
val Proceeding.isRunning get() = status.value == RUNNING

fun Proceeding.afterStarted(op: () -> Unit) {
    status.onChange {
        if (it == RUNNING) op()
    }
}

fun Proceeding.afterStopped(op: () -> Unit) {
    status.onChange {
        if (it == OFF) op()
    }
}






abstract class ProceedingImpl : PropReflectingStringableClass(), Proceeding {
    protected val statusProp = VarProp(OFF)
    final override val status: ObsVal<Status> = statusProp
    protected val messageProp = VarProp("")
    final override val message: ObsVal<String> = messageProp
    final override val isOff by lazy { status.eq(OFF) }
    val isRunning by lazy { status.eq(RUNNING) }
}
