package matt.model.startstop

import matt.model.idea.ProceedingIdea

interface Startable: ProceedingIdea {
  fun sendStartSignal()
  fun startAndJoin()
}

interface Stoppable: ProceedingIdea {
  fun sendStopSignal()
  fun stopAndJoin()
}