package matt.model.op.prints

import matt.model.code.report.Reporter

interface Prints : Reporter {
    override fun local(prefix: String): Prints
    fun println(a: Any)
    fun print(a: Any)
}