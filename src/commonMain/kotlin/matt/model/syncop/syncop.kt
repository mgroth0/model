package matt.model.syncop

import matt.lang.YesIUseLang
import matt.lang.sync.inSync
import kotlin.jvm.Synchronized

class AntiDeadlockSynchronizer {
  companion object {
	val yesIUseLang = YesIUseLang
  }

  private var currentWorkerCount = 0


  private val opQueue by lazy { mutableListOf<()->Unit>() }

  fun useInternalData(op: ()->Unit) {
	inSync(this) {
	  currentWorkerCount += 1
	}
	op()
	inSync(this) {
	  currentWorkerCount -= 1
	  if (currentWorkerCount == 0) {
		opQueue.forEach { it() }
		opQueue.clear()
	  }
	}
  }

  @Synchronized fun operateOnInternalDataNowOrLater(op: ()->Unit) {
	if (currentWorkerCount > 0) opQueue += op
	else op()
  }

}