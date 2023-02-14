package matt.model.flowlogic.singlerunlambda

import matt.lang.anno.OnlySynchronizedOnJvm

class SingleRunLambda(private val op: ()->Unit) {
  private var ran = false

  @OnlySynchronizedOnJvm
  operator fun invoke() {
	if (!ran) {
	  ran = true
	  op()
	}
  }
}