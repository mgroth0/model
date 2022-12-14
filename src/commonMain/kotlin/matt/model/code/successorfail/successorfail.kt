package matt.model.code.successorfail


inline fun <R> mightFail(op: FailableDSL.()->R): FailableReturn<R> {
  return FailableDSL.runOrFail(op)
}


object FailableDSL {

  inline fun <R> runOrFail(op: FailableDSL.()->R): FailableReturn<R> {
	return try {
	  val r = op.invoke(this)
	  SuccessfulReturn(r)
	} catch (f: FailException) {
	  f.failure.cast()
	}
  }

  fun fail(message: String) {
	val f = FailedReturn<Any?>(message)
	throw FailException(f)
  }

  fun fail(failure: FailedReturn<*>) {
	val f = failure.cast<Any?>()
	throw FailException(f)
  }

  @PublishedApi
  internal class FailException(val failure: FailedReturn<*>): Exception()

}

sealed interface FailableReturn<T>

fun <T> FailableReturn<T>.requireSuccess() = (this as SuccessfulReturn<T>).value

inline fun <T> FailableReturn<T>.resultOr(op: (FailedReturn<T>)->Unit): T {
  when (this) {
	is SuccessfulReturn -> return this.value
	is FailedReturn     -> {
	  op(this)
	  error("was supposed to return above")
	}
  }
}


class SuccessfulReturn<T>(val value: T): FailableReturn<T>
class FailedReturn<T>(val message: String): FailableReturn<T> {
  fun <R> cast() = FailedReturn<R>(message = message)
}


sealed interface SuccessOrFail {
  val message: String
}

object Success: SuccessOrFail {
  override val message = ""
}

class Fail(override val message: String): SuccessOrFail