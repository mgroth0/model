package matt.model.code.successorfail

import matt.model.code.idea.FailableIdea


inline fun <R> mightFail(op: FailableDSL.() -> R): FailableReturn<R> {
    return FailableDSL.runOrFail(op)
}


object FailableDSL {

    inline fun <R> runOrFail(op: FailableDSL.() -> R): FailableReturn<R> {
        return try {
            val r = op.invoke(this)
            SuccessfulReturn(r)
        } catch (f: FailException) {
            FailedReturn(f)
        }
    }

    fun fail(message: String) {
        val exception = FailException(message = message)
        /*val f = FailedReturn<Any?>(exception)*/
        throw exception
    }

    fun fail(failure: FailedReturn) {
        /*val f = failure.cast<Any?>()
        throw FailException(f)*/
        throw failure.exception
    }

    @PublishedApi
    internal class FailException(message: String) : Exception(message)

}

sealed interface FailableReturn<out T> : FailableIdea

fun <T> FailableReturn<T>.requireSuccess() = (this as SuccessfulReturn<T>).value

inline fun <T> FailableReturn<T>.resultOr(op: (FailedReturn) -> Unit): T {
    when (this) {
        is SuccessfulReturn -> return this.value
        is FailedReturn     -> {
            op(this)
            error("was supposed to return above")
        }
    }
}


class SuccessfulReturn<T>(val value: T) : FailableReturn<T>
class FailedReturn(val exception: Exception) : FailableReturn<Nothing>


sealed interface SuccessOrFail : FailableIdea {
    val message: String
}

sealed interface SucceedOrFailWithException : SuccessOrFail

object Success : SucceedOrFailWithException {
    override val message = ""
}

sealed interface Failure : SuccessOrFail

class Fail(override val message: String) : Failure {
    override fun toString() = "Fail[message=\"$message\"]"
}

class FailWithException(val exception: Exception) : Failure, SucceedOrFailWithException {
    override val message = exception.message ?: "no exception message"
}