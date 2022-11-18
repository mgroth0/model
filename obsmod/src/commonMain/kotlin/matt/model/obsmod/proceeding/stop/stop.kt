package matt.model.obsmod.proceeding.stop

import matt.model.obsmod.proceeding.Proceeding
import matt.model.startstop.Stoppable
import matt.obs.bindings.bool.ObsB

interface StoppableProceeding: Proceeding, Stoppable {
  val stopButtonLabel: String
  val canStop: ObsB
}