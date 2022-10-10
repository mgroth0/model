//@file: JvmName("RunJvmKt")

package matt.model.obsmod.run

import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.ExceptionResponse
import matt.log.profile.err.StructuredExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.log.profile.err.with
import matt.model.errreport.Report
import matt.model.flowlogic.controlflowstatement.ControlFlow
import matt.model.obsmod.run.Proceeding.Status
import matt.model.obsmod.run.Proceeding.Status.OFF
import matt.model.obsmod.run.Proceeding.Status.RUNNING
import matt.model.obsmod.run.Proceeding.Status.STARTING
import matt.model.obsmod.run.Proceeding.Status.STOPPING
import matt.model.successorfail.Fail
import matt.model.successorfail.Success
import matt.obs.bindings.bool.ObsB
import matt.obs.bindings.comp.eq
import matt.obs.prop.ObsVal
import matt.obs.prop.VarProp
import matt.sys.loopthread.MutableRefreshTimeDaemonLoop
import kotlin.concurrent.thread
import kotlin.time.Duration


abstract class ProceedingImpl: Proceeding {
  protected val statusProp = VarProp(OFF)
  override val status: ObsVal<Status> = statusProp
  protected val messageProp = VarProp("")
  override val message: ObsVal<String> = messageProp
  val isOff by lazy { status.eq(OFF) }
  val isRunning by lazy { status.eq(RUNNING) }
}


abstract class ManualProceeding(
  override val startButtonLabel: String,
  protected val exceptionHandler: ExceptionHandler = defaultExceptionHandler
): ProceedingImpl() {

  protected abstract fun startup()

  @Synchronized final override fun sendStartSignal() {
	require(canStart.value)
	startSwitch()
  }

  private fun startSwitch(): Thread? {
	return when (status.value) {
	  OFF                         -> {
		statusProp v STARTING
		myMessage = ""
		val t = thread {
		  val result = exceptionHandler.with { startup() }
		  statusProp v when (result) {
			Success -> RUNNING
			is Fail -> {
			  myMessage = result.message
			  OFF
			}
		  }
		}
		t
	  }

	  STARTING, STOPPING, RUNNING -> null
	}
  }

  @Synchronized final override fun startAndJoin() {
	require(canStart.value)
	startSwitch()?.join()
  }

  override val canStart: ObsB = VarProp(true)


  protected var myMessage: String
	get() = message.value
	set(value) {
	  messageProp.value = value
	}

}

abstract class ThreadProceeding(
  startButtonLabel: String,
  exceptionHandler: ExceptionHandler = defaultExceptionHandler
): ManualProceeding(startButtonLabel, exceptionHandler) {
  abstract fun run()
  final override fun startup() {
	thread {
	  val result = exceptionHandler.with {
		run()
	  }
	  require(status.value == RUNNING)
	  val something = when (result) {
		Success -> OFF
		is Fail -> {
		  myMessage = result.message
		  OFF
		}
	  }
	  statusProp.value = something
	}
  }
}

abstract class StoppableManualProceeding(
  noun: String,
  exceptionHandler: ExceptionHandler = defaultExceptionHandler
): ManualProceeding(
  startButtonLabel = "Start $noun",
  exceptionHandler = exceptionHandler
), StoppableProceeding {

  override val name = noun

  override val stopButtonLabel = "Stop $noun"

  abstract fun stop()


  @Synchronized final override fun sendStopSignal() {
	require(canStop.value)
	stopSwitch()
  }

  private fun stopSwitch(): Thread? {
	return when (status.value) {
	  RUNNING                 -> {
		statusProp v STOPPING
		myMessage = ""
		thread {
		  val result = exceptionHandler.with { stop() }
		  statusProp v when (result) {
			Success -> OFF
			is Fail -> {
			  myMessage = result.message
			  RUNNING
			}
		  }
		}
	  }

	  STARTING, STOPPING, OFF -> null
	}
  }

  @Synchronized final override fun stopAndJoin() {
	require(canStop.value)
	stopSwitch()?.join()
  }

  override val canStop: ObsB = VarProp(true)
}


class DaemonLoopSpawnerProceeding(
  noun: String,
  override val canStart: ObsB = VarProp(true),
  override val canStop: ObsB = VarProp(true),
  sleepInterval: Duration,
  private val op: ()->ControlFlow,
  private val finalize: ()->Unit = {},
  exceptionHandler: ExceptionHandler = defaultExceptionHandler
): StoppableManualProceeding(noun, exceptionHandler = exceptionHandler) {


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

  @Synchronized override fun startup() {
	require(!hadException)
	require(loop == null)
	loop = MutableRefreshTimeDaemonLoop(
	  sleepInterval = sleepInterval,
	  op = { op() },
	  finalize = {
		finalize()
		synchronized(this@DaemonLoopSpawnerProceeding) {
		  println("nulling loop 1?")
		  loop = null
		  statusProp v OFF
		}
	  },
	  uncaughtExceptionHandler = object: StructuredExceptionHandler() {
		override fun handleException(t: Thread, e: Throwable, report: Report): ExceptionResponse {
		  hadException = true
		  val r = exceptionHandler(e, report)
		  synchronized(this@DaemonLoopSpawnerProceeding) {
			println("nulling loop 2?")
			loop = null
			statusProp v OFF
		  }
		  return r
		}
	  }
	).also {
	  it.isDaemon = spawnDaemons
	  it.start()
	}
  }

  override fun stop() {
	loop!!.stopAndJoin()
  }

}