package matt.model.op.debug

import matt.model.code.idea.DebuggerIdea
import matt.model.code.report.Reporter
import matt.model.op.prints.Prints

class DebugLogger(val name: String): DebuggerIdea, Reporter, Prints {
    override fun local(prefix: String): Prints {
        TODO()
    }

    override fun println(a: Any) {
        kotlin.io.println("$name:\t$a")
    }

    override fun print(a: Any) {
        kotlin.io.print("$name:\t$a")
    }


}
