package matt.model.op.debugger

import matt.lang.require.requireNot
import matt.lang.service.ThreadProvider
import matt.model.code.idea.DebuggerIdea
import java.lang.Thread.sleep
import kotlin.time.Duration

class StackTraceDebugger() : DebuggerIdea {
    fun printStackTraces() {
        Thread.getAllStackTraces().filterKeys { it.isAlive }.forEach {
            println(
                """
		==================================================
		Thread: ${it.key}
		
		${it.value.joinToString("\n") { it.toString() }}
		==================================================
	  """.trimMargin()
            )
        }
    }
}

class ReportNothingDoneDebugger(
    private val threadProvider: ThreadProvider,
    private val enable: Boolean = true
) : DebuggerIdea {
    private var lastThingDoneTime = System.currentTimeMillis()
    var lastThingDone = "${ReportNothingDoneDebugger::class.simpleName} created"
        set(value) {
            lastThingDoneTime = System.currentTimeMillis()
            field = value
        }

    @Synchronized
    fun reportIfNothingDoneFor(d: Duration) {
        requireNot(started)
        val refreshRate = d / 10.0
        start(
            refreshMillis = refreshRate.inWholeMilliseconds, thresholdMillis = d.inWholeMilliseconds
        )
    }

    private var started = false
    private var freezeDetected = false
    private fun start(
        refreshMillis: Long,
        thresholdMillis: Long
    ) {
        if (enable) threadProvider.namedThread(isDaemon = true, name = "ReportNothingDoneDebugger start Thread") {
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