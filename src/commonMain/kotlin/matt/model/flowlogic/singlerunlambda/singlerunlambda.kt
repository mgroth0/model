package matt.model.flowlogic.singlerunlambda

import kotlin.jvm.Synchronized

class SingleRunLambda(private val op: ()->Unit) {
  private var ran = false

  @Synchronized
  operator fun invoke() {
	if (!ran) {
	  ran = true
	  op()
	}
  }
}