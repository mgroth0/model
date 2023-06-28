package matt.model.code.valjson

import matt.lang.anno.SeeURL
import matt.lang.delegation.provider
import matt.lang.delegation.valProp

/*I SHOULD START PHASING THIS OUT IN FAVOR OF DYNAMIC PORTS (e.g. 0) FOR MANY REASONS INCLUDING
*
* - https://youtrack.jetbrains.com/issue/KTOR-5996/Configure-server-to-find-first-available-port-matching-predicat
* - Not robustly portable to other machines
* - Issues arise when running multiple applications using different versions of this library
*
* */
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

    val ide by aPort
    val task by aPort
    val top by aPort
    val notify by aPort
    val launch by aPort
    val brainstorm by aPort
    val kjg by aPort
    val pdf by aPort
    val graphviz by aPort
    val spotify by aPort
    val multiDesktop by aPort
    val seleniumDaemon by aPort
    val seleniumService by aPort
    val omniFxGui by aPort
    val localKtorServers by portRange(10)
    val unRegisteredPortPool = num..65_500 /*max port possible is 65_535*/
}

@SeeURL("https://youtrack.jetbrains.com/issue/KTOR-5996/Configure-server-to-find-first-available-port-matching-predicate")
enum class LocalKtorPort {
    Base, Web, Chrome, Python, HerokuRun;

    val port = PortRegistry.localKtorServers.toList()[ordinal]
}