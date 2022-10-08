@file: JvmName("RunJvmKt")

package matt.model.obsmod.run

import matt.lang.go
import matt.lang.scope
import matt.log.profile.ExceptionHandler
import matt.log.profile.ExceptionResponse
import matt.log.profile.StructuredExceptionHandler
import matt.log.profile.defaultExceptionHandler
import matt.model.errreport.Report
import matt.model.flowlogic.controlflowstatement.ControlFlow
import matt.model.flowlogic.controlflowstatement.ControlFlow.CONTINUE
import matt.model.successorfail.SuccessOrFail
import matt.model.successorfail.SuccessOrFail.FAIL
import matt.model.successorfail.SuccessOrFail.SUCCESS
import matt.obs.bindings.bool.ObsB
import matt.obs.prop.VarProp
import matt.sys.loopthread.MutableRefreshTimeDaemonLoop
import kotlin.concurrent.thread
import kotlin.time.Duration

abstract class ProceedingImpl: Proceeding {
  protected val runningProp = VarProp(false)
  override val running: ObsB = runningProp
}


abstract class ManualProceeding: ProceedingImpl() {
  protected abstract fun startup(): SuccessOrFail

  @Synchronized override fun startIfNotRunning() {
	require(canStart.value)
	val result = if (!runningProp.value) startup() else SUCCESS
	runningProp v when (result) {
	  SUCCESS -> true
	  FAIL    -> false
	}
  }

  override val canStart: ObsB = VarProp(true)

}

abstract class StoppableManualProceeding: ManualProceeding(), StoppableProceeding {
  abstract fun stop()
  override fun stopAndJoin() {
	stop()
	runningProp v false
  }

  override val canStop: ObsB = VarProp(true)
}

class BasicThreadedProceeding(
  private val op: ()->Unit, private val exceptionHandler: ExceptionHandler = defaultExceptionHandler
): ManualProceeding() {
  @Synchronized override fun startup(): SuccessOrFail {
	val t = thread(start = false) {
	  op()
	  runningProp v false
	}
	t.uncaughtExceptionHandler = object: StructuredExceptionHandler() {
	  override fun handleException(t: Thread, e: Throwable, report: Report): ExceptionResponse {
		val r = exceptionHandler(e, report)
		runningProp v false
		return r
	  }
	}
	t.start()
	return SUCCESS
  }
}

fun daemonLoopSpawnerProceeding(
  canStop: ObsB = VarProp(true),
  sleepInterval: Duration,
  op: ()->Unit,
  finalize: ()->Unit = {},
  exceptionHandler: ExceptionHandler = defaultExceptionHandler
) = DaemonLoopSpawnerProceeding(
  canStop = canStop,
  sleepInterval = sleepInterval,
  op = {
	op()
	CONTINUE
  },
  finalize = finalize,
  exceptionHandler = exceptionHandler
)

class DaemonLoopSpawnerProceeding(
  override val canStop: ObsB = VarProp(true),
  sleepInterval: Duration,
  private val op: ()->ControlFlow,
  private val finalize: ()->Unit = {},
  private val exceptionHandler: ExceptionHandler = defaultExceptionHandler
): ManualProceeding(), AsyncStoppableProceeding {


  var spawnDaemons = true

  private var loop: MutableRefreshTimeDaemonLoop? = null

  val sleepIntervalProp = VarProp(sleepInterval).apply {
	onChange {
	  synchronized(this@DaemonLoopSpawnerProceeding) {
		loop?.sleepInterval = value
	  }
	}
  }
  var sleepInterval by sleepIntervalProp

  private var hadException: Boolean = false

  @Synchronized override fun startup(): SuccessOrFail {
	require(!hadException)
	loop?.go {
	  require(!it.wasSignaledToStop) {
		"tried to start MutableRefreshTimeDaemonLoop while it was stopping. How to handle this? Doesn't make sense to start another before the previous stops. Also doesn't make sense to do nothing. Main possibilities are blocking until last stop completed or explicitly skipping if last stop didn't finish or a combination of the two with a timeout."
	  }
	}
	require(loop == null)
	if (loop == null) {
	  loop = MutableRefreshTimeDaemonLoop(sleepInterval, op = {
		op()
	  }, finalize = {
		finalize()
		synchronized(this@DaemonLoopSpawnerProceeding) {
		  loop = null
		  runningProp v false
		}
	  }, uncaughtExceptionHandler = object: StructuredExceptionHandler() {
		override fun handleException(t: Thread, e: Throwable, report: Report): ExceptionResponse {
		  hadException = true
		  val r = exceptionHandler(e, report)
		  runningProp v false
		  loop = null
		  return r
		}
	  }).also {
		it.isDaemon = spawnDaemons
		it.start()
	  }
	}
	return SUCCESS
  }

  @Synchronized override fun stopAndJoin() {
	loop?.scope {
	  signalToStop()
	  join()
	}
  }

  @Synchronized override fun sendStopSignal() {
	loop?.scope {
	  signalToStop()
	}
  }

}