package matt.model.code.errreport

import matt.prim.str.mybuild.string


abstract class Report {
    abstract val text: String
    fun print() = println(text)
    override fun toString(): String {
        return text
    }
}

fun Throwable.printReport() = ThrowReport(this).print()

/*can later collect machine and context info, etc*/
class ThrowReport(private val t: Thread?, private val e: Throwable?) : Report() {
    constructor(e: Throwable) : this(Thread.currentThread(), e)

    override val text by lazy {
        """
    ERROR REPORT
    Thread: $t
    Throwable: ${e?.infoString()}
  """.trimIndent()
    }


}


fun Throwable.infoString(): String = string {
    lineDelimited {
        +"Throwable=${this}"
        +"Class=${this::class.qualifiedName}"
        +"String=${toString()}"
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

