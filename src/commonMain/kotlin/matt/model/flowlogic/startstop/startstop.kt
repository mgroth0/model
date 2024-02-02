package matt.model.flowlogic.startstop

import matt.lang.idea.ProceedingIdea

interface Startable : ProceedingIdea {
    fun sendStartSignal()

    fun startAndJoin()
}

interface Stoppable : ProceedingIdea {
    fun sendStopSignal()

    fun stopAndJoin()
}
