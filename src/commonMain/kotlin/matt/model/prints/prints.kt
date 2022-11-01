package matt.model.prints

import matt.model.report.Reporter

interface Prints: Reporter {
  override fun local(prefix: String): Prints
  fun println(a: Any)
  fun print(a: Any)
}