package matt.model.prints

import matt.model.idea.ReporterIdea

interface Prints: ReporterIdea {
  fun println(a: Any)
  fun print(a: Any)
}