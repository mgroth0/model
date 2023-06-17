package matt.model.code.valjson

import matt.lang.delegation.provider
import matt.lang.delegation.valProp


object PortRegistry {
    private var num = 65000 /*max port possible is 65_535*/
    private val aPort
        get() = provider {
            val i = num++
            valProp {
                i
            }
        }

    private fun portRange(size: Int) = provider {
        val startInclusive = num
        num += size
        val endExclusive = num
        val range = startInclusive until endExclusive
        valProp {
            range
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

    val spotify by aPort
    val profileAgent by aPort

    val multiDesktop by aPort

    val seleniumDaemon by aPort
    val seleniumService by aPort

    val localKtorServers by portRange(10)


    val omniFxGui by aPort

    val unRegisteredPortPool = num..65_500 /*max port possible is 65_535*/
}