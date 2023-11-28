package matt.model.test


import matt.model.code.delegate.ThrowingVetoable
import matt.model.data.range.IntRangeSerializer
import matt.model.data.xyz.Dim2D
import matt.model.flowlogic.command.Command
import matt.model.flowlogic.command.ExitStatus
import matt.model.flowlogic.controlflowstatement.ControlFlow
import matt.model.flowlogic.latch.asyncloaded.LoadedValueSlot
import matt.model.obj.single.SingleCall
import matt.model.obj.single.SingleCallWithArg
import matt.model.obj.single.Singleton
import matt.test.assertions.JupiterTestAssertions.assertRunsInOneMinute
import kotlin.enums.enumEntries
import kotlin.test.Test
import kotlin.test.assertEquals

class ModelTests {
    @Test
    fun initObjects() {
        IntRangeSerializer
    }

    @Test
    fun instantiateClasses() = assertRunsInOneMinute {
        ThrowingVetoable(1.0) { true }
        Singleton()
        SingleCall {}
        SingleCallWithArg<Int> {}
    }

    @Test
    fun initEnums() = assertRunsInOneMinute {
        enumEntries<ControlFlow>()
        enumEntries<ExitStatus>()
        enumEntries<Dim2D>()
    }

    @Test
    fun implementInterfaces() {
        object : Command {
            override fun run(arg: String) {
                TODO("Not yet implemented")
            }
        }
    }

    @Test
    fun slots() {
        val slot = LoadedValueSlot<String>()
        slot.isDoneOrCancelled()
        slot.putLoadedValue("abc")
        assertEquals(slot.await(), "abc")
    }
}