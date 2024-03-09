package matt.model.code.successorfail

import kotlinx.serialization.Serializable
import matt.lang.idea.FailableIdea
import matt.model.code.successorfail.FailableDSL.CodeFailException



/*

I should take inspiration from kotlin.Result. However, I can't completely rely on it. kotlin.Result, while idiomatic, is fundamentally based on carrying a reference to an exception. The nice thing about FailableReturn is that the Failure instances need not carry a reference to an exception, making them more lightweight. I should, however, take inspiration from the syntax/semantics/dsl/api of kotlin.Result as it is nice and concise.

*/


inline fun <R> mightFail(op: FailableDSL.() -> R): FailableReturn<R> = FailableDSL.runOrFail(op)


object FailableDSL {

    inline fun <R> runOrFail(op: FailableDSL.() -> R): FailableReturn<R> =
        try {
            val r = op.invoke(this)
            SuccessfulReturn(r)
        } catch (f: FailException) {
            when (f) {
                is CodeFailException -> CodeFailedReturn(f)
                is UserFailException -> f.userFailure
            }
        }

    fun codeFail(message: String): Unit = throw CodeFailException(message = message)

    fun userError(message: String): Nothing = userFail(message)
    fun userFail(message: String): Nothing = throw UserFailException(message = message)

    fun fail(failure: FailedReturn) {
        when (failure) {
            is CodeFailedReturn -> throw failure.throwable
            is UserFailedReturn -> throw UserFailException(failure)
        }
    }


    sealed class FailException(
        message: String,
        cause: Throwable? = null
    ) : Exception(message, cause)

    class UserFailException(val userFailure: UserFailedReturn) : FailException(userFailure.message) {
        constructor(message: String) : this(UserFailedReturn(message))
    }

    class CodeFailException(
        message: String,
        cause: Throwable? = null
    ) : FailException(message, cause) {
        constructor(
            message: String
        ) : this(message = message, cause = null)
    }
}

sealed interface FailableReturn<out T> : FailableIdea

fun <T> FailableReturn<T>.requireSuccess() = (this as SuccessfulReturn<T>).value

inline fun <T> FailableReturn<T>.resultOr(op: (FailedReturn) -> Unit): T {
    when (this) {
        is SuccessfulReturn -> return value
        is FailedReturn     -> {
            op(this)
            error("was supposed to return above")
        }
    }
}


class SuccessfulReturn<T>(val value: T) : FailableReturn<T>

fun <T, R> FailableReturn<T>.mapSuccess(op: (T) -> R) =
    when (this) {
        is FailedReturn     -> this
        is SuccessfulReturn -> SuccessfulReturn(op(value))
    }

sealed interface FailedReturn : FailableReturn<Nothing>
class CodeFailedReturn(val throwable: Throwable) : FailedReturn {
    constructor(message: String) : this(CodeFailException(message))
}

class UserFailedReturn(val message: String) : FailedReturn

/*

... Or you could just use kotlin.Result

*/

sealed interface SuccessOrFail : FailableIdea {
    val message: String
}

sealed interface SucceedOrFailWithException : SuccessOrFail

@Serializable
sealed interface SimpleSuccessOrFail: FailableIdea {
    val message: String
}

@Serializable
data object Success : SucceedOrFailWithException, SimpleSuccessOrFail {
    override val message = ""
}

interface Failure : SuccessOrFail

@Serializable
class Fail(override val message: String) : Failure, SimpleSuccessOrFail {
    override fun toString() = "Fail[message=\"$message\"]"
}


class FailWithException(val exception: Exception) : Failure, SucceedOrFailWithException {
    override val message = exception.message ?: "no exception message"
}


sealed interface FoundOrNot<T : Any>

class Found<T : Any>(val value: T) : FoundOrNot<T> {
    override fun toString() = "Found[value=\"$value\"]"
}

class NotFound(val message: String) : FoundOrNot<Nothing> {
    override fun toString() = "NotFound[message=\"$message\"]"
}



fun <T: Any> T?.failIfNull() = runCatching { this!! }


