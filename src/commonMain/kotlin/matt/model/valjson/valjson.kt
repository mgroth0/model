package matt.model.valjson

import matt.lang.delegation.provider
import matt.lang.delegation.valProp

object ValJson {
  const val WAIT_FOR_MS = 1000

  private val ports = mutableMapOf<String, Int>()

  object Port: Map<String, Int> by ports {
	private var num = 65000
	private val aPort
	  get() = provider {
		val i = num++
		ports[it] = i
		valProp {
		  i
		}
	  }
	val task by aPort
	val top by aPort
	val notify by aPort
	val launch by aPort
	val slidespace by aPort
	val brainstorm by aPort
	val kjg by aPort
	val pdf by aPort
	val ide by aPort
	val graphviz by aPort
	val ktor by aPort
	val spotify by aPort
	val deephysWeb by aPort
  }
}