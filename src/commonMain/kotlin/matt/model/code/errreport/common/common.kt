package matt.model.code.errreport.common

import matt.lang.classname.mostInformativeClassName
import matt.lang.common.go
import matt.model.code.errreport.createThrowReport
import matt.prim.str.mybuild.api.string

inline fun reportAndReThrowErrorsBetter(op: () -> Unit) {
    try {
        op()
    } catch (throwable: Throwable) {
        createThrowReport(throwable).print()
        throw throwable
    }
}

fun Throwable.printReport() = createThrowReport(this).print()
interface CommonThrowReport {
    val text: String
    fun print()
}

abstract class Report: CommonThrowReport {
    abstract override val text: String
    final override fun print() = println(text)
    final override fun toString(): String = text
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

fun Throwable.infoString(
    depth: Int = 0
): String =
    string {
        if (depth >= 10) {
            +"REACHED MAX DEPTH FOR INFO STRING. WITHOUT THIS CHECK, STACK OVERFLOWS CAN AND DO HAPPEN OCCASIONALLY"
            return@string
        }
        val throwable = this@infoString
        lineDelimited {
            +"Throwable=$throwable"
            +"Class=${throwable::class.mostInformativeClassName}"
            +"String=$throwable"
            +"Message=$message"
            +"STACK TRACE:"
            stackTraceToString().let {
                +(
                    if (it.length > 100_000) it.take(
                        100_000
                    ) + "... THROTTLED STACK TRACE STRING TO EASE STACK OVERFLOW ERROR DETECTION"
                    else it
                )
            }
            +"CAUSE: ${cause?.infoString(depth = depth + 1)}"
            if (suppressedExceptions.isEmpty()) {
                +"SUPPRESSED EXCEPTIONS: NONE"
            } else {
                +"SUPPRESSED EXCEPTIONS:"
                +suppressedExceptions.joinToString("\n\n") { it.infoString(depth = depth + 1) }
            }
        }
    }
