package matt.model.obsmod.test


import matt.model.flowlogic.controlflowstatement.ControlFlow.BREAK
import matt.model.obsmod.proceeding.man.stop.daemon.DaemonLoopSpawnerProceeding
import matt.test.Tests
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class ObsmodTests : Tests() {
    @Test
    fun useAProceeding() {
        DaemonLoopSpawnerProceeding(
            noun = "dog",
            sleepInterval = 10.milliseconds,
            op = {
                1 + 1
                BREAK
            }
        ).startAndJoin()
    }
}