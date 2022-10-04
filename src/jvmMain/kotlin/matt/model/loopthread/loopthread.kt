package matt.model.loopthread

import kotlin.time.Duration


class MutableRefreshTimeDaemonLoop(sleep: Duration, op: DaemonLoop.()->Unit): DaemonLoop(sleep, op) {
  public override var sleepMS = super.sleepMS
}

open class DaemonLoop(sleep: Duration, protected val op: DaemonLoop.()->Unit): Thread() {

  protected open val sleepMS = sleep.inWholeMilliseconds

  private var shouldContinue: Boolean = true

  fun signalToStop() {
	shouldContinue = false
  }

  init {
	isDaemon = true
  }

  final override fun run() {
	while (shouldContinue) {
	  op()
	  try {
		sleep(sleepMS)
	  } catch (e: InterruptedException) {
		if (shouldContinue) throw e
	  }
	}
  }
}