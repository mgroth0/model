package matt.model.syncop

import matt.lang.NullToReduceObjects
import matt.lang.Op
import matt.lang.YesIUseLang
import matt.lang.sync.inSync
import kotlin.jvm.Synchronized


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

  @Synchronized fun operateOnInternalDataNowOrLater(op: Op) {
	if (currentWorkerCount > 0) {
	  (opQueue ?: mutableListOf<Op>().also {
		opQueue = it
	  }) += op
	} else op()
  }

}