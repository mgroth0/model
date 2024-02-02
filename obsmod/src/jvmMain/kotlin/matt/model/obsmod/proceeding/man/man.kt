package matt.model.obsmod.proceeding.man

import matt.async.thread.namedThread
import matt.lang.anno.Open
import matt.lang.go
import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.model.code.successorfail.Fail
import matt.model.code.successorfail.FailWithException
import matt.model.code.successorfail.Failure
import matt.model.code.successorfail.Success
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.model.obsmod.proceeding.Proceeding.Status.STARTING
import matt.model.obsmod.proceeding.Proceeding.Status.STOPPING
import matt.model.obsmod.proceeding.ProceedingImpl
import matt.model.obsmod.proceeding.err.with
import matt.obs.bindings.bool.ObsB
import matt.obs.prop.VarProp

abstract class ManualProceeding(
    final override val startButtonLabel: String,
    protected val exceptionHandler: ExceptionHandler = defaultExceptionHandler
) : ProceedingImpl() {

    protected abstract fun Startup.startup()


    @Synchronized
    final override fun sendStartSignal() {
        require(canStart.value)
        startSwitch()
    }


    private fun startSwitch(): Thread? = when (status.value) {
        OFF                         -> {
            statusProp v STARTING/*messageProp v ""*/
            val t = namedThread(name = "OFF Thread") {
                val startup = Startup()
                val result = exceptionHandler.with { startup.startup() }
                val realResult = startup.failure.takeIf { it is Fail } ?: result
                realResult.message.takeIf { it.isNotBlank() }?.go {
                    messageProp v it
                }
                statusProp v when (realResult) {
                    is Success           -> RUNNING
                    is Failure           -> OFF
                    is FailWithException -> error("this seems like a weird kotlin 2.0.0-Beta1 Switch Statement Compilation Internal Error...")
                }
            }
            t
        }

        STARTING, STOPPING, RUNNING -> null
    }

    final override fun startAndJoin() {
        val startThread = synchronized(this) {
            require(canStart.value) {
                "$this cannot start"
            }
            startSwitch()
        }
        startThread?.join()
    }

    @Open
    override val canStart: ObsB = VarProp(true)


    inner class Startup internal constructor() {
        internal var failure: Fail? = null
        fun failed(message: String) {
            failure = Fail(message)
        }
    }

    final override fun reflectingToStringProps() = setOf(::name)

}



