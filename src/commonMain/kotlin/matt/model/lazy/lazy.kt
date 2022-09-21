package matt.model.lazy

import kotlinx.datetime.Clock
import matt.lang.go
import kotlin.contracts.ExperimentalContracts
import kotlin.jvm.Synchronized

private object EMPTY

@OptIn(ExperimentalContracts::class)
class DependentValue<V>(op: ()->V) {

  var stopwatch: String? = null
  private var lastCalculated: Any? = EMPTY
  private var valid: Boolean = false

  var op = op
	set(value) {
	  field = value
	  markInvalid()
	}

  @Synchronized
  fun markInvalid() {
	valid = false
  }

  @Synchronized
  fun calc() {
	val t = if (stopwatch != null) Clock.System.now() else null
	lastCalculated = op()
	valid = true
	t?.go {
	  println("stopwatch\t${(Clock.System.now() - t)}\t$stopwatch")
	}
  }


  @Synchronized
  @Suppress("UNCHECKED_CAST")
  fun get() = if (valid) lastCalculated as V else {
	calc()
	lastCalculated as V
  }
}