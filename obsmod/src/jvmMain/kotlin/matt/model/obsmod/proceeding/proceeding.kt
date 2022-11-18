package matt.model.obsmod.proceeding

import matt.lang.go
import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.ExceptionResponse
import matt.log.profile.err.StructuredExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.log.profile.err.with
import matt.model.errreport.Report
import matt.model.flowlogic.controlflowstatement.ControlFlow
import matt.model.obsmod.proceeding.Proceeding.Status
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.Proceeding.Status.RUNNING
import matt.model.obsmod.proceeding.Proceeding.Status.STARTING
import matt.model.obsmod.proceeding.Proceeding.Status.STOPPING
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
  override val isOff by lazy { status.eq(OFF) }
  val isRunning by lazy { status.eq(RUNNING) }
}

