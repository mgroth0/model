package matt.model.obsmod.proceeding.err

import matt.log.profile.err.ExceptionHandler
import matt.log.profile.err.ExceptionResponse.IGNORE
import matt.log.profile.err.ExceptionResponse.THROW
import matt.log.report.BugReport
import matt.model.code.successorfail.Fail
import matt.model.code.successorfail.SuccessOrFail
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.system.exitProcess

fun reportButIgnore(vararg clses: KClass<out Exception>): ExceptionHandler = { e, r ->
  r.print()
  val response = when {
	clses.any { e::class.isSubclassOf(it) } -> IGNORE
	else                                    -> THROW
  }
  response
}

fun ExceptionHandler.with(vararg ignore: KClass<out java.lang.Exception>, op: ()->Unit) = withResult(*ignore) {
  op()
  matt.model.code.successorfail.Success
}

fun ExceptionHandler.withResult(vararg ignore: KClass<out java.lang.Exception>, op: ()->SuccessOrFail): SuccessOrFail {
  return try {
	op()
  } catch (e: Exception) {
	when (this(e, BugReport(java.lang.Thread.currentThread(), e))) {
	  matt.log.profile.err.ExceptionResponse.EXIT   -> when {
		ignore.any { e::class.isSubclassOf(it) } -> Fail("${e::class.simpleName}")
		else                                     -> {
		  thread(isDaemon = true) {
			/*needs to be in thread to avoid *circular blockage of threads waiting for other threads to end in shutdown process*/
			exitProcess(1)
		  }
		  Fail("${e::class.simpleName}")
		}
	  }

	  matt.log.profile.err.ExceptionResponse.IGNORE -> Fail("${e::class.simpleName}")
	  matt.log.profile.err.ExceptionResponse.THROW  -> throw e
	}
  }
}