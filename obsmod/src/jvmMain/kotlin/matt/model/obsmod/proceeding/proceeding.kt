package matt.model.obsmod.proceeding

import matt.model.obsmod.proceeding.Proceeding.Status
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.obs.bindings.comp.eq
import matt.obs.prop.ObsVal
import matt.obs.prop.VarProp
import matt.reflect.tostring.PropReflectingStringableClass


abstract class ProceedingImpl : PropReflectingStringableClass(), Proceeding {
    protected val statusProp = VarProp(OFF)
    final override val status: ObsVal<Status> = statusProp
    protected val messageProp = VarProp("")
    final override val message: ObsVal<String> = messageProp
    final override val isOff by lazy { status.eq(OFF) }
    val isRunning by lazy { status.eq(RUNNING) }
}

