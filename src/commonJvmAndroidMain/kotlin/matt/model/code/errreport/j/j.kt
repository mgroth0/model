package matt.model.code.errreport.j

import matt.model.code.errreport.common.CommonThrowReport
import matt.model.code.errreport.common.Report
import matt.model.code.errreport.common.infoString
import matt.prim.str.mybuild.api.string

/*can later collect machine and context info, etc*/
class ThrowReport(
    private val t: Thread?,
    private val e: Throwable?
) : Report(), CommonThrowReport {
    constructor(e: Throwable) : this(Thread.currentThread(), e)

    override val text by lazy {
        try {
            string {
                lineDelimited {
                    +"ERROR REPORT"
                    +"Thread: $t"
                    +"Throwable: ${e?.infoString()}"
                }
            }
        } catch (e: Exception) {
            "EXCEPTION WHILE CREATING THROW REPORT: $e"
        }
    }
}
