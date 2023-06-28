package matt.model.code.jvm

import kotlinx.serialization.Serializable
import matt.lang.If
import matt.lang.anno.SeeURL
import matt.lang.ifTrue
import matt.lang.opt
import matt.model.code.jvm.agentpath.AgentPathArg
import matt.model.code.jvm.agentpath.fullArg
import matt.model.data.byte.ByteSize
import kotlin.jvm.JvmInline


@Serializable
data class JvmArgs(
    @SeeURL("https://stackoverflow.com/questions/32855984/does-java-xmx1g-mean-109-or-230-bytes")
    val xmx: ByteSize?,
    @SeeURL("https://stackoverflow.com/questions/32855984/does-java-xmx1g-mean-109-or-230-bytes")
    val xms: ByteSize? = null,
    val stackTraceInFastThrow: Boolean = true,
    val diagnosticVMOptions: Boolean = false,
    @SeeURL("https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/topics/debugging.md#stacktrace-recovery")
    val enableAssertionsAndCoroutinesDebugMode: Boolean = true,
    val maxStackTraceDepth: Int? = null,
    /*JVM will fail to start if less than 208K*/
    /*nvm, I just programmatically print both the top and bottom of the stack now. Much better.*/
    val maxStackSize: String? = null,

    val prism: Boolean = true,

    val unlockDiagnosticVmOptions: Boolean = true,
    val showHiddenFrames: Boolean = true,

    val useParallelGC: Boolean = false,

    val kotlinxCoroutinesDebug: Boolean = true,

    val agentPath: AgentPathArg? = null,

    val otherArgs: List<JvmArg> = listOf(),

    ) {

    companion object {
        val EMPTY by lazy {
            JvmArgs(
                xmx = null,
                stackTraceInFastThrow = false,
                enableAssertionsAndCoroutinesDebugMode = false,
                prism = false,
                unlockDiagnosticVmOptions = false,
                showHiddenFrames = false,
                kotlinxCoroutinesDebug = false
            ).also {
                require(it.args.isEmpty()) {
                    "EMPTY should be empty but it has ${it.args.size} values: ${it.args.joinToString { it }}"
                }
            }
        }
    }

    init {
        if (showHiddenFrames && !unlockDiagnosticVmOptions) {
            println("WARNING: I think showHiddenFrames requires unlockDiagnosticVmOptions")
        }
    }

    val args by lazy {
        arrayOf(
            /*https://stackoverflow.com/questions/65565209/nullpointers-in-javafx-when-using-a-large-canvas*/
            *If(prism).then("-Dprism.maxvram=2G"),

            *opt(xmx) { "-Xms$formattedBinaryNoSpaceNoDecimalsAndSingleLetterUnit" },
            *opt(xmx) { Xmx(this).toRawArg() },
            *ifTrue(enableAssertionsAndCoroutinesDebugMode) { "-enableassertions" },

            /*OmitStackTraceInFastThrow: makes sure that exceptions get a full stack trace always.*/
            /*https://stackoverflow.com/questions/2411487/nullpointerexception-in-java-with-no-stacktrace*/
            *ifTrue(stackTraceInFastThrow) { "-XX:-OmitStackTraceInFastThrow" },
            /*VM option 'ShowHiddenFrames' is diagnostic and must be enabled via -XX:+UnlockDiagnosticVMOptions.*/
            *If(unlockDiagnosticVmOptions).then("-XX:+UnlockDiagnosticVMOptions"),
            *If(showHiddenFrames).then("-XX:+ShowHiddenFrames"),
            *ifTrue(diagnosticVMOptions) { "-XX:+UnlockDiagnosticVMOptions" },

            *If(useParallelGC).then("-XX:+UseParallelGC"),

            *opt(maxStackTraceDepth) { "-XX:MaxJavaStackTraceDepth=$this" },
            *opt(maxStackSize) { "-Xss$this" },
            *opt(agentPath) { fullArg() },

            /*
            "Overhead of this feature is negligible and it can be safely turned on by default to simplify logging and diagnostic." - kotlinx.coroutines

            gives better stack traces with coroutines. Given the message above, I should have this enabled always.

            I tested it, and this definitely works! And the correct format is in fact "-Dkotlinx.coroutines.debug". "-Dkotlinx.coroutines.debug=true" may work, I have not tested it. But  "-Dkotlinx.coroutines.debug" definitely works.
            * */

            *If(kotlinxCoroutinesDebug).then(
                @SeeURL("https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/topics/debugging.md#stacktrace-recovery")
                "-Dkotlinx.coroutines.debug"
            ),

            *otherArgs.map { it.toRawArg() }.toTypedArray(),

            )
    }
    val argsList by lazy {
        JvmArgsList(args)
    }
}



@Serializable
@JvmInline
value class Xmx(private val size: ByteSize) : JvmArg {
    override fun toRawArg(): String {
        return "-Xmx${size.formattedBinaryNoSpaceNoDecimalsAndSingleLetterUnit}"
    }
}

interface JvmArg {
    fun toRawArg(): String
}

@JvmInline
value class JvmArgsList(val args: List<String>) : List<String> by args {
    constructor(args: Array<String>) : this(args.toList())

    init {
        require(args.toSet().size == args.size)
    }
}