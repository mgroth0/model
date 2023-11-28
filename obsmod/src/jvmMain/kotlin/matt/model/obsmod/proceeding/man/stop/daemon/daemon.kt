package matt.model.obsmod.proceeding.man.stop.daemon

import matt.lang.assertions.require.requireNot
import matt.lang.assertions.require.requireNull
import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.ExceptionResponse
import matt.log.profile.err.StructuredExceptionHandler
import matt.log.profile.err.defaultExceptionHandler
import matt.model.code.errreport.Report
import matt.model.flowlogic.controlflowstatement.ControlFlow
import matt.model.obsmod.proceeding.Proceeding.Status.OFF
import matt.model.obsmod.proceeding.man.stop.StoppableManualProceeding
import matt.obs.bindings.bool.ObsB
import matt.obs.prop.VarProp
import matt.sys.loopthread.MutableRefreshTimeDaemonLoop
import kotlin.time.Duration

class DaemonLoopSpawnerProceeding(
    noun: String,
    override val canStart: ObsB = VarProp(true),
    override val canStop: ObsB = VarProp(true),
    sleepInterval: Duration,
    private val op: () -> ControlFlow,
    private val finalize: () -> Unit = {},
    exceptionHandler: ExceptionHandler = defaultExceptionHandler
) : StoppableManualProceeding(noun, exceptionHandler = exceptionHandler) {


    var spawnDaemons = true

    private var loop: MutableRefreshTimeDaemonLoop? = null

    @Suppress("MemberVisibilityCanBePrivate")
    val sleepIntervalProp = VarProp(sleepInterval).apply {
        onChange {
            synchronized(this@DaemonLoopSpawnerProceeding) {
                loop?.sleepInterval = value
            }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    var sleepInterval by sleepIntervalProp

    private var hadException: Boolean = false

    @Synchronized
    override fun Startup.startup() {
        requireNot(hadException)
        requireNull(loop)
        loop = MutableRefreshTimeDaemonLoop(
            sleepInterval = sleepInterval,
            isDaemon = spawnDaemons,
            op = { op() },
            finalize = {
                finalize()
                synchronized(this@DaemonLoopSpawnerProceeding) {
                    loop = null
                    statusProp v OFF
                }
            },
            uncaughtExceptionHandler = object : StructuredExceptionHandler() {
                override fun handleException(
                    t: Thread,
                    e: Throwable,
                    report: Report
                ): ExceptionResponse {
                    hadException = true
                    val r = exceptionHandler(e, report)
                    synchronized(this@DaemonLoopSpawnerProceeding) {
                        loop = null
                        statusProp v OFF
                    }
                    return r
                }
            }
        ).also {
            it.sendStartSignal()
        }
    }

    override fun stop() = loop!!.stopAndJoin()

}
