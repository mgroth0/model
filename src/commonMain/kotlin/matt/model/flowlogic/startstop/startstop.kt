package matt.model.flowlogic.startstop

import matt.model.code.idea.ProceedingIdea

interface Startable: ProceedingIdea {
  fun sendStartSignal()
  fun startAndJoin()
}

interface Stoppable: ProceedingIdea {
  fun sendStopSignal()
  fun stopAndJoin()
}