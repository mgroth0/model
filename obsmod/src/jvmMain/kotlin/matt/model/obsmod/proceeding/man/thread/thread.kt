package matt.model.obsmod.proceeding.man.thread

import matt.lang.go
import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.log.profile.err.with
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.model.obsmod.proceeding.man.ManualProceeding
import kotlin.concurrent.thread

abstract class ThreadProceeding(
  startButtonLabel: String,
  exceptionHandler: ExceptionHandler = defaultExceptionHandler
): ManualProceeding(startButtonLabel, exceptionHandler) {
  abstract fun run()
  private var thr: Thread? = null
  final override fun Startup.startup() {
	thr = thread(name = "ThreadProceeding ($startButtonLabel)") {
	  val result = exceptionHandler.with(InterruptedException::class) {
		run()
	  }
	  require(status.value == RUNNING)
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