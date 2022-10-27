package matt.model.valjson

object ValJson {
  const val WAIT_FOR_MS = 1000

  object Port {
	private var num = 65000
	val task = num++
	val top = num++
	val notify = num++
	val launch = num++
	val slidespace = num++
	val brainstorm = num++
	val kjg = num++
	val pdf = num++
	val ide = num++
	val graphviz = num++
	val ktor = num++
	val spotify = num++
	val deephysWeb = num++
  }
}