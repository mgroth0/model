package matt.model.obsmod.proceeding

import matt.model.code.idea.ProceedingIdea
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.model.flowlogic.startstop.Startable
import matt.obs.bindings.bool.ObsB
import matt.obs.prop.ObsVal


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

fun Proceeding.afterStarted(op: ()->Unit) {
  status.onChange {
	if (it == RUNNING) op()
  }
}

fun Proceeding.afterStopped(op: ()->Unit) {
  status.onChange {
	if (it == OFF) op()
  }
}


