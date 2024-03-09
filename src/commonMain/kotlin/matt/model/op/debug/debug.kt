package matt.model.op.debug

import matt.model.code.idea.DebuggerIdea
import matt.model.code.report.Reporter
import matt.model.op.prints.Prints
import kotlin.io.print as kotlinPrint
import kotlin.io.println as kotlinPrintln

class DebugLogger(val name: String): DebuggerIdea, Reporter, Prints {
    override fun local(prefix: String): Prints {
        TODO()
    }

    override fun println(a: Any) {
        kotlinPrintln("$name:\t$a")
    }

    override fun print(a: Any) {
        kotlinPrint("$name:\t$a")
    }
}
