package matt.model.code.jvm

import kotlinx.serialization.Serializable
import matt.lang.If
import matt.lang.anno.SeeURL
import matt.lang.ifTrue
import matt.lang.opt
import matt.lang.optArray
import matt.lang.require.requireEmpty
import matt.lang.require.requireEquals
import matt.lang.sysprop.props.JmxRemoteAuthenticate
import matt.lang.sysprop.props.JmxRemotePort
import matt.lang.sysprop.props.JmxRemoteSsl
import matt.lang.sysprop.props.KotlinXCoroutinesDebug
import matt.lang.sysprop.props.PrismMaxVram
import matt.model.code.jvm.agentpath.AgentPathArg
import matt.model.code.jvm.agentpath.fullArg
import matt.model.code.jvm.args.JvmArg
import matt.model.code.jvm.args.gc.GarbageCollector
import matt.model.code.jvm.bytearg.Xms
import matt.model.code.jvm.bytearg.Xmx
import matt.model.code.jvm.bytearg.Xss
import matt.model.data.byte.ByteSize
import kotlin.jvm.JvmInline

val ACCEPTABLE_AUTO_INSERTED_ARGS = setOf(
    "-Dfile.encoding=UTF-8",
    "-Duser.country=US",
    "-Duser.language=en",
    "-Duser.variant" /*idk why it is added. But seems harmless.*/
)

const val HEROKU_FORWARDED_PORT = 9090


interface CommonJvmArgs {
    @SeeURL("https://stackoverflow.com/a/42541096/6596010")
    val addExports: List<String>?
    val addOpens: List<String>?
    fun getExportArgs(exports: List<String>?) = optArray(addExports) {
        map { "--add-exports=$it" }.toTypedArray()
    }

    fun getOpenArgs(opens: List<String>?) = optArray(addOpens) {
        map { "--add-opens=$it" }.toTypedArray()
    }
}

const val miscModule = "java.base/jdk.internal.misc=ALL-UNNAMED"

@Serializable
data class JavaExecArgs(
    @SeeURL("https://stackoverflow.com/questions/32855984/does-java-xmx1g-mean-109-or-230-bytes") val xmx: ByteSize?,
    @SeeURL("https://stackoverflow.com/questions/32855984/does-java-xmx1g-mean-109-or-230-bytes") val xms: ByteSize? = null,
    val stackTraceInFastThrow: Boolean = true,
    val diagnosticVMOptions: Boolean = false,
    @SeeURL("https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/topics/debugging.md#stacktrace-recovery") val enableAssertionsAndCoroutinesDebugMode: Boolean = true,
    val maxStackTraceDepth: Int? = null,
    /*JVM will fail to start if less than 208K*/
    /*nvm, I just programmatically print both the top and bottom of the stack now. Much better.*/
    val maxStackSize: ByteSize? = null,

    val prism: Boolean = true,

    val unlockDiagnosticVmOptions: Boolean = true,
    val showHiddenFrames: Boolean = true,

    val gc: GarbageCollector? = null,

    val kotlinxCoroutinesDebug: Boolean = true,

    val agentPath: AgentPathArg? = null,


    val useContainerSupport: Boolean = false,

    val printGarbageCollectorDetails: Boolean = false,
    val printGarbageDateStamps: Boolean = false,
    val printTenuringDistribution: Boolean = false,

    val printGarbageCollectorLogs: Boolean = false,

    val compilerThreadCount: Int? = null,

    val jmx: Boolean = false,


    override val addExports: List<String>? = null,
    override val addOpens: List<String>? = null,


    val otherArgs: List<JvmArg> = listOf(),


    ) : CommonJvmArgs {

    companion object {
        val EMPTY by lazy {
            JavaExecArgs(
                xmx = null,
                stackTraceInFastThrow = false,
                enableAssertionsAndCoroutinesDebugMode = false,
                prism = false,
                unlockDiagnosticVmOptions = false,
                showHiddenFrames = false,
                kotlinxCoroutinesDebug = false
            ).also {
                requireEmpty(it.args) {
                    "EMPTY should be empty but it has ${it.args.size} values: ${it.args.joinToString { it }}"
                }
            }
        }
    }

    init {
        if (showHiddenFrames && !unlockDiagnosticVmOptions) {
            println("WARNING: I think showHiddenFrames requires unlockDiagnosticVmOptions")
        }
        if (printGarbageDateStamps) {
            error("PrintGCDateStamps seems to be removed from java 1.9, see https://openjdk.org/jeps/271")
        }
        if (printTenuringDistribution) {
            error("I'm guessing this was removed from java 1.9 too, but not sure. I should spend my energy learning the new logging framework anyway. see https://openjdk.org/jeps/271 ")
        }
    }


    private val systemProps by lazy {

        mapOf(
            /*https://stackoverflow.com/questions/65565209/nullpointers-in-javafx-when-using-a-large-canvas*/
            *If(prism).then(PrismMaxVram to "2G"),


            /*
       "Overhead of this feature is negligible and it can be safely turned on by default to simplify logging and diagnostic." - kotlinx.coroutines

       gives better stack traces with coroutines. Given the message above, I should have this enabled always.

       I tested it, and this definitely works! And the correct format is in fact "-Dkotlinx.coroutines.debug". "-Dkotlinx.coroutines.debug=true" may work, I have not tested it. But  "-Dkotlinx.coroutines.debug" definitely works.
       * */

            *If(kotlinxCoroutinesDebug).then(
                @SeeURL("https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/topics/debugging.md#stacktrace-recovery")
                KotlinXCoroutinesDebug to ""
            ),


            *If(jmx).then(
                *listOf(
                    JmxRemotePort to "$HEROKU_FORWARDED_PORT",
                    JmxRemoteSsl to "false",
                    JmxRemoteAuthenticate to "false"
                ).toTypedArray()
            )

        )
//
//        arrayOf(
//
//            *If(prism).then("prism.maxvram=2G"),
//
//            /*
//       "Overhead of this feature is negligible and it can be safely turned on by default to simplify logging and diagnostic." - kotlinx.coroutines
//
//       gives better stack traces with coroutines. Given the message above, I should have this enabled always.
//
//       I tested it, and this definitely works! And the correct format is in fact "-Dkotlinx.coroutines.debug". "-Dkotlinx.coroutines.debug=true" may work, I have not tested it. But  "-Dkotlinx.coroutines.debug" definitely works.
//       * */
//
//            *If(kotlinxCoroutinesDebug).then(
//                @SeeURL("https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/topics/debugging.md#stacktrace-recovery") "kotlinx.coroutines.debug"
//            ),
//
//
//            *If(jmx).then(
//                *listOf(
//                    @SeeURL("https://kubos.cz/2016/01/13/visualvm-connecting-through-ssh.html")
//                    "port=$HEROKU_FORWARDED_PORT", /*just the default for heroku port forwarding I think*/
//                    "ssl=false",
//                    "authenticate=false"
//                ).map {
//                    "com.sun.management.jmxremote.$it"
//                }.toTypedArray()
//            )
//        )
    }

    private val jvmArgs by lazy {
        arrayOf(
            /*OmitStackTraceInFastThrow: makes sure that exceptions get a full stack trace always.*/
            /*https://stackoverflow.com/questions/2411487/nullpointerexception-in-java-with-no-stacktrace*/
            *ifTrue(stackTraceInFastThrow) { "-OmitStackTraceInFastThrow" },
            /*VM option 'ShowHiddenFrames' is diagnostic and must be enabled via -XX:+UnlockDiagnosticVMOptions.*/
            *If(unlockDiagnosticVmOptions).then("+UnlockDiagnosticVMOptions"),
            *If(showHiddenFrames).then("+ShowHiddenFrames"),
            *ifTrue(diagnosticVMOptions) { "+UnlockDiagnosticVMOptions" },
            *opt(gc) { "+Use${this.name}GC" },
            *opt(maxStackTraceDepth) { "MaxJavaStackTraceDepth=$this" },


            *If(useContainerSupport).then(
                @SeeURL("https://devcenter.heroku.com/articles/java-memory-issues") "+UseContainerSupport"
            ),

            *If(printGarbageCollectorDetails).then(
                @SeeURL("https://devcenter.heroku.com/articles/java-support#monitoring-resource-usage") "+PrintGCDetails"
            ),
            *If(printGarbageDateStamps).then(
                @SeeURL("https://devcenter.heroku.com/articles/java-support#monitoring-resource-usage") "+PrintGCDateStamps"
            ),
            *If(printTenuringDistribution).then(
                @SeeURL("https://devcenter.heroku.com/articles/java-support#monitoring-resource-usage") "+PrintTenuringDistribution"
            ),
            *opt(compilerThreadCount) { "CICompilerCount=$this" },
        )
    }

    val args by lazy {
        arrayOf(

            *systemProps.map {
                if (it.value.isBlank()) {
                    "-D${it.key.key}" /*flag prop*/
                } else {
                    "-D${it.key.key}=${it.value}"
                }

            }.toTypedArray(),
            *opt(xms) { Xms(this).toRawArg() },
            *opt(xmx) { Xmx(this).toRawArg() },
            *ifTrue(enableAssertionsAndCoroutinesDebugMode) { "-enableassertions" },

            *jvmArgs.map { "-XX:$it" }.toTypedArray(),


            *opt(maxStackSize) { Xss(this).toRawArg() },
            *opt(agentPath) { fullArg() },


            *If(printGarbageCollectorLogs).then(
                @SeeURL("https://devcenter.heroku.com/articles/java-support#monitoring-resource-usage") @SeeURL("https://openjdk.org/jeps/158") "-Xlog:gc"
            ),

            *getExportArgs(addExports),
            *getOpenArgs(addOpens),


            *otherArgs.map { it.toRawArg() }.toTypedArray(),

            )
    }
    val argsList by lazy {
        JvmArgsList(args)
    }
}


@JvmInline
value class JvmArgsList(val args: List<String>) : List<String> by args {
    constructor(args: Array<String>) : this(args.toList())

    init {
        requireEquals(args.toSet().size, args.size)
    }
}