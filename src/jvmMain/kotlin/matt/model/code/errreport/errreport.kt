@file:JvmName("ErrReportJvmKt")

package matt.model.code.errreport

import matt.prim.str.mybuild.string


fun Throwable.printReport() = ThrowReport(this).print()

actual fun throwReport(e: Throwable): CommonThrowReport = ThrowReport(e)

/*can later collect machine and context info, etc*/
class ThrowReport(
    private val t: Thread?,
    private val e: Throwable?
) : Report(), CommonThrowReport {
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


inline fun reportAndReThrowErrorsBetter(op: () -> Unit) {
    try {
        op()
    } catch (throwable: Throwable) {
        ThrowReport(throwable).print()
        throw throwable
    }
}

