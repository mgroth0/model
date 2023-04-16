package matt.model.code.valjson

import matt.lang.delegation.provider
import matt.lang.delegation.valProp

object ValJson {
    const val WAIT_FOR_MS = 1000

    private val ports = mutableMapOf<String, Int>()

    object Port : Map<String, Int> by ports {
        private var num = 65000 /*max port possible is 65_535*/
        private val aPort
            get() = provider {
                val i = num++
                ports[it] = i
                valProp {
                    i
                }
            }

        /*put them higher if I want them to change less often*/
        val ide by aPort

        val task by aPort
        val top by aPort
        val notify by aPort
        val launch by aPort

        /*val slidespace by aPort*/
        val brainstorm by aPort
        val kjg by aPort
        val pdf by aPort

        val graphviz by aPort

        /*val ktor by aPort*/
        val spotify by aPort
        val localKtorTest by aPort
        val profileAgent by aPort
        //	val localKtorTest by aPort

        val pythonTest by aPort

        val multiDesktop by aPort

        val unRegisteredPortPool = num..65_500 /*max port possible is 65_535*/
    }
}