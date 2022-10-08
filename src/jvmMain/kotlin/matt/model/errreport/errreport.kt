package matt.model.errreport


abstract class Report {
  abstract val text: String
  fun print() = println(text)
  override fun toString(): String {
	return text
  }
}

/*can later collect machine and context info, etc*/
class ThrowReport(private val t: Thread?, private val e: Throwable?): Report() {
  override val text by lazy {
	"""
    ERROR REPORT
    Thread: $t
    Throwable: ${e?.infoString()}
  """.trimIndent()
  }


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


