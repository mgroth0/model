package matt.model.op.prints

import matt.lang.anno.Open
import matt.model.code.report.Reporter

interface Prints : Reporter {
    override fun local(prefix: String): Prints
    fun print(a: Any)

    @Open  /*this needs to be defined here and not in extension, otherwise I will use the default kotlin println while in this scope unintentionally*/
    fun println(a: Any) = print(a.toString() + "\n")
}


fun Prints.tab(s: Any?) = println("\t$s")
operator fun Prints.plusAssign(s: Any) = println(s.toString())
