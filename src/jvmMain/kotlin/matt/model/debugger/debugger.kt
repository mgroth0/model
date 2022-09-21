package matt.model.debugger

import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.time.Duration

class Debugger(private val enable: Boolean = true) {
  private var lastThingDoneTime = System.currentTimeMillis()
  var lastThingDone = "${Debugger::class.simpleName} created"
	set(value) {
	  lastThingDoneTime = System.currentTimeMillis()
	  field = value
	}

  @Synchronized fun reportIfNothingDoneFor(d: Duration) {
	require(!started)
	val refreshRate = d/10.0
	start(
	  refreshMillis = refreshRate.inWholeMilliseconds, thresholdMillis = d.inWholeMilliseconds
	)
  }

  private var started = false
  private var freezeDetected = false
  private fun start(
	refreshMillis: Long, thresholdMillis: Long
  ) {
	if (enable) thread(isDaemon = true) {
	  while (true) {
		if (System.currentTimeMillis() - lastThingDoneTime > thresholdMillis) {
		  freezeDetected = true
		  println("${this::class.simpleName} has detected a freeze... lastThingDone=\"$lastThingDone\"")
		} else if (freezeDetected) {
		  println("${this::class.simpleName} has detected an UnFreeze! lastThingDone=\"$lastThingDone\"")
		  freezeDetected = false
		}
		sleep(refreshMillis)
	  }
	}
  }
}