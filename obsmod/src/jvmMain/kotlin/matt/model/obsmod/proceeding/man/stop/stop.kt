package matt.model.obsmod.proceeding.man.stop

import matt.async.thread.namedThread
import matt.lang.anno.Open
import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.model.code.successorfail.FailWithException
import matt.model.code.successorfail.Failure
import matt.model.code.successorfail.Success
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.model.obsmod.proceeding.Proceeding.Status.STARTING
import matt.model.obsmod.proceeding.Proceeding.Status.STOPPING
import matt.model.obsmod.proceeding.err.with
import matt.model.obsmod.proceeding.man.ManualProceeding
import matt.model.obsmod.proceeding.stop.StoppableProceeding
import matt.obs.bindings.bool.ObsB
import matt.obs.prop.writable.VarProp

abstract class StoppableManualProceeding(
    final override val noun: String,
    exceptionHandler: ExceptionHandler = defaultExceptionHandler
) : ManualProceeding(
        startButtonLabel = "Start $noun", exceptionHandler = exceptionHandler
    ),
    StoppableProceeding {

    final override val name = noun

    final override val stopButtonLabel = "Stop $noun"

    abstract fun stop()


    @Synchronized
    final override fun sendStopSignal() {
        require(canStop.value) {
            "sent stop signal when canStop of $name is false"
        }
        stopSwitch()
    }

    private fun stopSwitch(): Thread? =
        when (status.value) {
            RUNNING                 -> {
                statusProp v STOPPING
                messageProp v ""
                namedThread(name = "Stop $name") {
                    val result = exceptionHandler.with { stop() }
                    messageProp v result.message
                    statusProp v
                        when (result) {
                            Success              -> OFF
                            is Failure           -> RUNNING
                            is FailWithException -> error("this seems like a weird kotlin 2.0.0-Beta1 Switch Statement Compilation Internal Error...")
                        }
                }
            }

            STARTING, STOPPING, OFF -> null
        }

    final override fun stopAndJoin() {
        val stopThread =
            synchronized(this) {
                require(canStop.value)
                stopSwitch()
            }
        stopThread?.join()
    }

    @Open
    override val canStop: ObsB = VarProp(true)
}
