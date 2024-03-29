package matt.model.code.vals.portreg

import matt.lang.delegation.provider
import matt.lang.delegation.valProp

/*I SHOULD START PHASING THIS OUT IN FAVOR OF DYNAMIC PORTS (e.g. 0) FOR MANY REASONS INCLUDING
*
* - https://youtrack.jetbrains.com/issue/KTOR-5996/Configure-server-to-find-first-available-port-matching-predicat
* - Not robustly portable to other machines
* - Issues arise when running multiple applications using different versions of this library
*
*


Relocate these to VariablePortRegistry*/
object PortRegistry {
    private var num = 65000 /*max port possible is 65_535*/
    private val aPort
        get() =
            provider {
                val i = num++
                valProp {
                    i
                }
            }

    private fun portRange(size: Int) =
        provider {
            val startInclusive = num
            num += size
            val endExclusive = num
            val range = startInclusive until endExclusive
            valProp {
                range
            }
        }

    val ide by aPort
    val task by aPort
    val top by aPort
    val notify by aPort
    val launch by portRange(3)
    val brainstorm by aPort
    val kjg by aPort
    val pdf by aPort
    val graphviz by aPort
    val spotify by aPort
    val multiDesktop by aPort
    val seleniumDaemon by aPort
    val seleniumService by aPort
    val omniFxGui by aPort
    val localKtorServers by portRange(100)
    val formatd by aPort
    val notify2 by aPort
    val remind by aPort
    val unRegisteredPortPool = num..65_500 /*max port possible is 65_535*/
}

