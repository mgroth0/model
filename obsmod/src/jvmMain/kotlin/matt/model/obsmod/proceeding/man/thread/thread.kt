package matt.model.obsmod.proceeding.man.thread

import matt.async.thread.namedThread
import matt.lang.go
import matt.lang.assertions.require.requireEquals
import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.model.obsmod.proceeding.err.with
import matt.model.obsmod.proceeding.man.ManualProceeding

abstract class ThreadProceeding(
    startButtonLabel: String,
    exceptionHandler: ExceptionHandler = defaultExceptionHandler
) : ManualProceeding(startButtonLabel, exceptionHandler) {
    abstract fun run()
    private var thr: Thread? = null
    final override fun Startup.startup() {
        thr = namedThread(name = "ThreadProceeding ($startButtonLabel)") {
            val result = exceptionHandler.with(InterruptedException::class) {
                run()
            }
            requireEquals(status.value, RUNNING)
            result.message.takeIf { it.isNotBlank() }?.go {
                messageProp v it
            }
            statusProp.value = OFF
            thr = null
        }
    }

    @Synchronized
    fun forceStop() {
        thr?.apply {
            interrupt()
            join()
        }
    }

}