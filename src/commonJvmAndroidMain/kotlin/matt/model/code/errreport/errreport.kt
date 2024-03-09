
package matt.model.code.errreport

import matt.model.code.errreport.common.CommonThrowReport
import matt.model.code.errreport.j.ThrowReport




actual fun createThrowReport(e: Throwable): CommonThrowReport = ThrowReport(e)

