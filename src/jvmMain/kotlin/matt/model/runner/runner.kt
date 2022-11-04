package matt.model.runner

import matt.lang.function.On
import matt.lang.function.Produce
import matt.lang.model.value.Value
import matt.model.latch.asyncloaded.LoadedValueSlot
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

  override fun join(op: On<R>) {
	thread!!.join()
	op(result!!.value)
  }
}

class ResultRun<R>(private val result: LoadedValueSlot<R>): Run<R> {
  /*todo: structure this better to prevent external usages to edit result. There should be more clear separation between the function that manages the Run and the one that responds to it. It's to easy to accidentally return a ResultRun instead of a Run*/
  override fun whenFinished(op: On<R>) {
	thread {
	  op(result.await())
	}
  }

  override fun join(op: On<R>) {
	op(result.await())
  }
}

