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
  constructor(e: Throwable): this(Thread.currentThread(), e)

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
  ${
  stackTraceToString().let {
	if (it.length > 10_000) it.take(
	  10_000
	) + "... THROTTLED STACK TRACE STRING TO EASE STACK OVERFLOW ERROR DETECTION"
  }
}
  
  CAUSE: ${cause?.infoString()}
  
  SUPPRESSED EXCEPTIONS:
  ${suppressedExceptions.joinToString("\n\n") { infoString() }}
""".trimIndent()


