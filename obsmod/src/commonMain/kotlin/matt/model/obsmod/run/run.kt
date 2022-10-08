package matt.model.obsmod.run

import matt.model.idea.ProceedingIdea
import matt.obs.bindings.bool.ObsB

interface Proceeding: ProceedingIdea {
  val running: ObsB
  fun startIfNotRunning()
  val canStart: ObsB
}

fun Proceeding.onStart(op: ()->Unit) {
  running.onChange {
	if (it) op()
  }
}

fun Proceeding.onStop(op: ()->Unit) {
  running.onChange {
	if (!it) op()
  }
}

interface StoppableProceeding: Proceeding {
  fun stopAndJoin()
  val canStop: ObsB
}

interface AsyncStoppableProceeding: StoppableProceeding {
  fun sendStopSignal()
}

