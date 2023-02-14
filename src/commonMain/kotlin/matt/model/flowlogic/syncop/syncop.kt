package matt.model.flowlogic.syncop

import matt.lang.YesIUseLang
import matt.lang.anno.NullToReduceObjects
import matt.lang.anno.OnlySynchronizedOnJvm
import matt.lang.function.Op
import matt.lang.sync.inSync


class AntiDeadlockSynchronizer {
  companion object {
	val yesIUseLang = YesIUseLang
  }

  private var currentWorkerCount = 0


  @NullToReduceObjects
  private var opQueue: MutableList<Op>? = null

  fun useInternalData(op: Op) {
	inSync(this) {
	  currentWorkerCount += 1
	}
	op()
	inSync(this) {
	  currentWorkerCount -= 1
	  if (currentWorkerCount == 0) {
		opQueue?.forEach { it() }
		opQueue = null
	  }
	}
  }

  @OnlySynchronizedOnJvm fun operateOnInternalDataNowOrLater(op: Op) {
	if (currentWorkerCount > 0) {
	  (opQueue ?: mutableListOf<Op>().also {
		opQueue = it
	  }) += op
	} else op()
  }

}