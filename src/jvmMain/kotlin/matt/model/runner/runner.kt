package matt.model.runner

import matt.lang.function.On
import matt.lang.function.Produce
import matt.model.value.Value
import kotlin.concurrent.thread

object ThreadRunner: Runner {
  override fun <R> run(op: Produce<R>): Run<R> {
	val run = ThreadRun<R>()
	val t = thread {
	  run.result = Value(op())
	}
	run.thread = t
	return run
  }
}

class ThreadRun<R> internal constructor(): Run<R> {
  internal var result: Value<R>? = null
  var thread: Thread? = null
  override fun whenFinished(op: On<R>) {
	thread {
	  thread!!.join()
	  op(result!!.value)
	}
  }
}