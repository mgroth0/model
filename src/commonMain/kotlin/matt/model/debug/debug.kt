package matt.model.debug

import matt.model.idea.DebuggerIdea
import matt.model.prints.Prints
import matt.model.report.Reporter

class DebugLogger(val name: String): DebuggerIdea, Reporter, Prints {
  override fun local(prefix: String): Prints {
	TODO("Not yet implemented")
  }

  override fun println(a: Any) {
	kotlin.io.println("$name:\t$a")
  }

  override fun print(a: Any) {
	kotlin.io.print("$name:\t$a")
  }
}