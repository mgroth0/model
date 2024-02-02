package matt.model.obsmod.proceeding.stop

import matt.model.flowlogic.startstop.Stoppable
import matt.model.obsmod.proceeding.Proceeding
import matt.obs.bindings.bool.ObsB

interface StoppableProceeding : Proceeding, Stoppable {
    val stopButtonLabel: String
    val canStop: ObsB
    val noun: String
}
