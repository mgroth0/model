package matt.model.obsmod.run

import matt.model.idea.ProceedingIdea
import matt.model.obsmod.run.Proceeding.Status.OFF
import matt.model.obsmod.run.Proceeding.Status.RUNNING
import matt.model.startstop.Startable
import matt.model.startstop.Stoppable
import matt.obs.bindings.bool.ObsB
import matt.obs.prop.ObsVal


interface Proceeding: ProceedingIdea, Startable {
  val name: String
  val startButtonLabel: String
  val canStart: ObsB
  val status: ObsVal<Status>
  val message: ObsVal<String>

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


interface StoppableProceeding: Proceeding, Stoppable {
  val stopButtonLabel: String
  val canStop: ObsB
}