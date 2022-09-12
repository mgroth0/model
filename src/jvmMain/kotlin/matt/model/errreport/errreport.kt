package matt.model.errreport


/*can later collect machine and context info, etc*/
class ThrowReport(private val t: Thread?, private val e: Throwable?) {
  override fun toString() = """
    ERROR REPORT
    Thread: $t
    Throwable: ${e?.infoString()}
    MemReport: 
  """.trimIndent()

  fun print() = println(toString())
}


fun Throwable.infoString(): String = """
  Throwable=${this}
  Class=${this::class.qualifiedName}
  String=${toString()}
  Message=${message}
  
  STACK TRACE:
  ${stackTraceToString()}
  
  CAUSE: ${cause?.infoString()}
  
  SUPPRESSED EXCEPTIONS:
  ${suppressedExceptions.joinToString("\n\n") { infoString() }}
""".trimIndent()