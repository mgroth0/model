package matt.model.code.errreport

import matt.lang.classname.mostInformativeClassName
import matt.lang.go
import matt.prim.str.mybuild.string

interface CommonThrowReport

abstract class Report {
    abstract val text: String
    fun print() = println(text)
    override fun toString(): String {
        return text
    }
}

fun Throwable.thisAndAllCauses(): List<Throwable> {
    val r = mutableListOf(this)
    var n = this
    do {
        n.cause?.go {
            r += it
            n = it
        }
    } while (n.cause != null)
    return r.toList()
}


fun Throwable.infoString(): String = string {
    val throwable = this@infoString
    lineDelimited {
        +"Throwable=$throwable"
        +"Class=${throwable::class.mostInformativeClassName}"
        +"String=$throwable"
        +"Message=${message}"
        +"STACK TRACE:"
        stackTraceToString().let {
            +(if (it.length > 100_000) it.take(
                100_000
            ) + "... THROTTLED STACK TRACE STRING TO EASE STACK OVERFLOW ERROR DETECTION"
            else it)
        }
        +"CAUSE: ${cause?.infoString()}"
        if (suppressedExceptions.isEmpty()) {
            +"SUPPRESSED EXCEPTIONS: NONE"
        } else {
            +"SUPPRESSED EXCEPTIONS:"
            +suppressedExceptions.joinToString("\n\n") { it.infoString() }
        }
    }
}


expect fun throwReport(e: Throwable): CommonThrowReport