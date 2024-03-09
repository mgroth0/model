package matt.model.code.errreport

import matt.model.code.errreport.common.CommonThrowReport
import matt.model.code.errreport.common.Report
import matt.model.code.errreport.common.infoString
import matt.prim.str.mybuild.api.string

actual fun createThrowReport(e: Throwable): CommonThrowReport = JsThrowReport(e)

/* can later collect machine and context info, etc */
private class JsThrowReport(
    private val e: Throwable?
) : Report(), CommonThrowReport {
    override val text by lazy {
        string {
            lineDelimited {
                +"JS ERROR REPORT"
                +"Throwable: ${e?.infoString()}"
            }
        }
    }
}
