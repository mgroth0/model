package matt.model.code.errreport

import matt.prim.str.mybuild.api.string

actual fun throwReport(e: Throwable): CommonThrowReport = JsThrowReport(e)

/*can later collect machine and context info, etc*/
class JsThrowReport(
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
