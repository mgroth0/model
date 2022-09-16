package matt.model.loopthread

import kotlin.time.Duration

class DaemonLoop(sleep: Duration, private val op: DaemonLoop.()->Unit): Thread() {
  private val sleepMS = sleep.inWholeMilliseconds

  init {
	isDaemon = true
  }



  private var shouldContinue: Boolean = false
  fun signalToStop() {
	shouldContinue = false
  }

  override fun run() {
	while (shouldContinue) {
	  op()
	  sleep(sleepMS)
	}
  }
}