package matt.model.obsmod.proceeding.man.stop

import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.log.profile.err.with
import matt.model.code.successorfail.Fail
import matt.model.code.successorfail.Success
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.model.obsmod.proceeding.Proceeding.Status.STARTING
import matt.model.obsmod.proceeding.Proceeding.Status.STOPPING
import matt.model.obsmod.proceeding.man.ManualProceeding
import matt.model.obsmod.proceeding.stop.StoppableProceeding
import matt.obs.bindings.bool.ObsB
import matt.obs.prop.VarProp
import kotlin.concurrent.thread

abstract class StoppableManualProceeding(
  override val noun: String,
  exceptionHandler: ExceptionHandler = defaultExceptionHandler
): ManualProceeding(
  startButtonLabel = "Start $noun",
  exceptionHandler = exceptionHandler
), StoppableProceeding {

  override val name = noun

  override val stopButtonLabel = "Stop $noun"

  abstract fun stop()


  @Synchronized final override fun sendStopSignal() {
	require(canStop.value) {
	  "sent stop signal when canStop of $name is false"
	}
	stopSwitch()
  }

  private fun stopSwitch(): Thread? {
	return when (status.value) {
	  RUNNING                 -> {
		statusProp v STOPPING
		messageProp v ""
		thread(name = "Stop $name") {
		  val result = exceptionHandler.with { stop() }
		  messageProp v result.message
		  statusProp v when (result) {
			Success -> OFF
			is Fail -> RUNNING
		  }
		}
	  }

	  STARTING, STOPPING, OFF -> null
	}
  }

  final override fun stopAndJoin() {
	val stopThread = synchronized(this) {
	  require(canStop.value)
	  stopSwitch()
	}
	stopThread?.join()
  }

  override val canStop: ObsB = VarProp(true)
}
