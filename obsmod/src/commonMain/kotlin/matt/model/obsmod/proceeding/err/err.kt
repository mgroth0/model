package matt.model.obsmod.proceeding.err

import matt.async.thread.namedThread
import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.ExceptionResponse
import matt.log.profile.err.ExceptionResponse.IGNORE
import matt.log.profile.err.ExceptionResponse.THROW
import matt.log.report.desktop.BugReport
import matt.model.code.successorfail.Fail
import matt.model.code.successorfail.SuccessOrFail
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.system.exitProcess

fun reportButIgnore(vararg clses: KClass<out Exception>): ExceptionHandler = { e, r ->
    r.print()
    val response =
        when {
            clses.any { e::class.isSubclassOf(it) } -> IGNORE
            else                                    -> THROW
        }
    response
}

fun ExceptionHandler.with(vararg ignore: KClass<out java.lang.Exception>, op: () -> Unit) =
    withResult(*ignore) {
        op()
        matt.model.code.successorfail.Success
    }

fun ExceptionHandler.withResult(vararg ignore: KClass<out java.lang.Exception>, op: () -> SuccessOrFail): SuccessOrFail =
    try {
        op()
    } catch (e: Exception) {
        when (this(e, BugReport(Thread.currentThread(), e))) {
            ExceptionResponse.EXIT   ->
                when {
                    ignore.any { e::class.isSubclassOf(it) } -> Fail("${e::class.simpleName}")
                    else                                     -> {
                        namedThread(isDaemon = true, name = "withResult Thread") {
                            /*needs to be in thread to avoid *circular blockage of threads waiting for other threads to end in shutdown process*/
                            exitProcess(1)
                        }
                        Fail("${e::class.simpleName}")
                    }
                }

            ExceptionResponse.IGNORE                     -> Fail("${e::class.simpleName}")
            ExceptionResponse.THROW -> throw e
        }
    }
