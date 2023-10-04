package matt.model.code.successorfail

import matt.lang.idea.FailableIdea
import matt.model.code.successorfail.FailableDSL.CodeFailException


inline fun <R> mightFail(op: FailableDSL.() -> R): FailableReturn<R> {
    return FailableDSL.runOrFail(op)
}


object FailableDSL {

    inline fun <R> runOrFail(op: FailableDSL.() -> R): FailableReturn<R> {
        return try {
            val r = op.invoke(this)
            SuccessfulReturn(r)
        } catch (f: FailException) {
            when (f) {
                is CodeFailException -> CodeFailedReturn(f)
                is UserFailException -> f.userFailure
            }
        }
    }

    fun codeFail(message: String) {
        throw CodeFailException(message = message)
    }

    fun userFail(message: String) {
        throw UserFailException(message = message)
    }

    fun fail(failure: FailedReturn) {
        when (failure) {
            is CodeFailedReturn -> throw failure.exception
            is UserFailedReturn -> throw UserFailException(failure)
        }
    }


    sealed class FailException(
        message: String,
        cause: Throwable? = null
    ) : Exception(message, cause) {

    }

    class UserFailException(val userFailure: UserFailedReturn) : FailException(userFailure.message) {
        constructor(message: String) : this(UserFailedReturn(message))
    }

    class CodeFailException(
        message: String,
        cause: Throwable? = null
    ) : FailException(message, cause) {
        constructor(
            message: String,
        ) : this(message = message, cause = null)
    }

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

sealed interface FailedReturn : FailableReturn<Nothing>
class CodeFailedReturn(val exception: Exception) : FailedReturn {
    constructor(message: String) : this(CodeFailException(message))
}

class UserFailedReturn(val message: String) : FailedReturn


sealed interface SuccessOrFail : FailableIdea {
    val message: String
}

sealed interface SucceedOrFailWithException : SuccessOrFail

data object Success : SucceedOrFailWithException {
    override val message = ""
}

sealed interface Failure : SuccessOrFail

class Fail(override val message: String) : Failure {
    override fun toString() = "Fail[message=\"$message\"]"
}

class FailWithException(val exception: Exception) : Failure, SucceedOrFailWithException {
    override val message = exception.message ?: "no exception message"
}