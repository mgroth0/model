package matt.model.code.errreport

import matt.lang.go
import matt.prim.str.mybuild.string


abstract class Report {
    abstract val text: String
    fun print() = println(text)
    override fun toString(): String {
        return text
    }
}

fun Throwable.printReport() = ThrowReport(this).print()

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

/*can later collect machine and context info, etc*/
class ThrowReport(private val t: Thread?, private val e: Throwable?) : Report() {
    constructor(e: Throwable) : this(Thread.currentThread(), e)

    override val text by lazy {
        string {
            lineDelimited {
                +"ERROR REPORT"
                +"Thread: $t"
                +"Throwable: ${e?.infoString()}"
            }
        }
    }


}


fun Throwable.infoString(): String = string {
    val throwable = this@infoString
    lineDelimited {
        +"Throwable=$throwable"
        +"Class=${throwable::class.qualifiedName}"
        +"String=$throwable"
        +"Message=${message}"
        +"STACK TRACE:"
        stackTraceToString().let {
            +(if (it.length > 10_000) it.take(
                10_000
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

