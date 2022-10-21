package matt.model.debug

import matt.model.idea.DebuggerIdea
import matt.model.idea.ReporterIdea
import matt.model.prints.Prints

class DebugLogger(val name: String): DebuggerIdea, ReporterIdea, Prints {
  override fun println(a: Any) {
	System.out.println("$name:\t$a")
  }

  override fun print(a: Any) {
	System.out.print("$name:\t$a")
  }
}