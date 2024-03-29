package matt.model.code.jvm.agentpath

import kotlinx.serialization.Serializable
import matt.lang.anno.SeeURL
import matt.model.data.message.AbsLinuxFile
import matt.model.data.message.AbsMacFile


@Serializable
sealed interface AgentPathArg {
    fun argValue(): String
}

fun AgentPathArg.fullArg() = "-agentpath:${argValue()}"

const val MAC_LIBJPROFILERTI_PATH = "/Applications/JProfiler.app/Contents/Resources/app/bin/macos/libjprofilerti.jnilib"

@SeeURL("https://www.ej-technologies.com/resources/jprofiler/help/doc/main/profiling.html")
@Serializable
data class JProfilerAgentPathArg(
    val libjprofilertiPath: String = MAC_LIBJPROFILERTI_PATH,
    val offline: Boolean = false,
    val port: Int? = 33809,
    val config: AbsLinuxFile? = null,
    val sessionId: Int? = null
) : AgentPathArg {

    override fun argValue(): String {
        val args = mutableListOf<String>()
        if (offline) {
            args += "offline"
        }
        if (port != null) {
            args += "port=$port"
        }
        if (config != null) {
            args += "config=${config.path}"
        }
        if (sessionId != null) {
            args += "id=$sessionId"
        }
        val maybeEquals = if (args.isNotEmpty()) "=" else ""
        return "$libjprofilertiPath$maybeEquals${
            args.joinToString(
                separator = ","
            )
        }"
    }

    override fun toString() = argValue()
}


const val DEFAULT_SAMPLE_IN_YOURKIT = false

@SeeURL("https://www.yourkit.com/forum/viewtopic.php?t=43414")
/*
/Applications/YourKit-Java-Profiler-2022.9.app/Contents/Resources/bin/mac/libyjpagent.dylib=profiler_dir=/Applications/YourKit-Java-Profiler-2022.9.app/Contents/Resources



"-agentpath:=exceptions=disable,delay=10000,listen=all"


"=exceptions=disable,listen=all"


port=${V==alJson.Port.profileAgent}*/
@Serializable
@SeeURL("https://www.yourkit.com/docs/java/help/agent.jsp")
data class YourKitAgentPathArg(
    val yourKitAppFolder: AbsMacFile,
    val samplingMode: Boolean = DEFAULT_SAMPLE_IN_YOURKIT
) : AgentPathArg {
    override fun argValue(): String {
        val resFolder = yourKitAppFolder.path.removeSuffix("/") + "/Contents/Resources"
        var r = resFolder + "/bin/mac/libyjpagent.dylib" + "=profiler_dir=$resFolder"
        if (samplingMode) {
            r += ",sampling"
        }
        return r
    }

    override fun toString() = argValue()
}
