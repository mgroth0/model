package matt.model.lazy

import kotlin.jvm.Synchronized

private object EMPTY
class DependentValue<V>(op: ()->V) {
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
	lastCalculated = op()
	valid = true
  }


  @Synchronized
  @Suppress("UNCHECKED_CAST")
  fun get() = if (valid) lastCalculated as V else {
	calc()
	lastCalculated as V
  }
}