package matt.model.code.errreport

import matt.model.code.errreport.common.CommonThrowReport


expect fun createThrowReport(e: Throwable): CommonThrowReport
