package matt.model.obsmod.proceeding.man.stop

import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.log.profile.err.with
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.model.obsmod.proceeding.Proceeding.Status.STARTING
import matt.model.obsmod.proceeding.Proceeding.Status.STOPPING
import matt.model.obsmod.proceeding.man.ManualProceeding
import matt.model.obsmod.proceeding.stop.StoppableProceeding
import matt.model.code.successorfail.Fail
import matt.model.code.successorfail.Success
import matt.obs.bindings.bool.ObsB
import matt.obs.prop.VarProp
import kotlin.concurrent.thread

abstract class StoppableManualProceeding(
  noun: String,
  exceptionHandler: ExceptionHandler = defaultExceptionHandler
): ManualProceeding(
  startButtonLabel = "Start $noun",
  exceptionHandler = exceptionHandler
), StoppableProceeding {

  override val name = noun

  override val stopButtonLabel = "Stop $noun"

  abstract fun stop()


  @Synchronized final override fun sendStopSignal() {
	require(canStop.value)
	stopSwitch()
  }

  private fun stopSwitch(): Thread? {
	return when (status.value) {
	  RUNNING                 -> {
		statusProp v STOPPING
		messageProp v ""
		thread {
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

  @Synchronized final override fun stopAndJoin() {
	require(canStop.value)
	stopSwitch()?.join()
  }

  override val canStop: ObsB = VarProp(true)
}
