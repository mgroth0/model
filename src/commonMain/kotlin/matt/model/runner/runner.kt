package matt.model.runner

import matt.lang.function.On
import matt.lang.function.Produce


interface Runner {
  fun <R> run(op: Produce<R>): Run<R>
}

interface Run<R> {
  fun whenFinished(op: On<R>)
  fun join(op: On<R>)
}



object InPlaceRunner: Runner {
  override fun <R> run(op: Produce<R>): Run<R> {
	val r = op()
	return AlreadyFinishedRun(r)
  }
}

class AlreadyFinishedRun<R> internal constructor(private val r: R): Run<R> {
  override fun whenFinished(op: On<R>) {
	op(r)
  }

  override fun join(op: On<R>) {
	op(r)
  }
}