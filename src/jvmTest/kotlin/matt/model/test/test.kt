package matt.model.test


import matt.model.code.delegate.ThrowingVetoable
import matt.model.flowlogic.command.ExitStatus
import matt.model.obj.single.SingleCall
import matt.model.obj.single.SingleCallWithArg
import matt.model.obj.single.Singleton
import matt.test.JupiterTestAssertions.assertRunsInOneMinute
import kotlin.test.Test

class ModelTests {
    @Test
    fun instantiateClasses() = assertRunsInOneMinute {
        ThrowingVetoable(1.0) { true }
        Singleton()
        SingleCall {}
        SingleCallWithArg<Int> {}
    }

    @Test
    fun initEnums() = assertRunsInOneMinute {
        ExitStatus.entries
    }
}