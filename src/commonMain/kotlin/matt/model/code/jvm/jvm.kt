package matt.model.code.jvm

import kotlinx.serialization.Serializable
import matt.lang.anno.SeeURL
import matt.lang.ifTrue
import matt.lang.opt
import matt.model.data.byte.ByteSize


@Serializable
data class JvmArgs(
    val xmx: ByteSize?,
    val stackTraceInFastThrow: Boolean = true,
    val diagnosticVMOptions: Boolean = false,
    @SeeURL("https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/topics/debugging.md#stacktrace-recovery")
    val enableAssertionsAndCoroutinesDebugMode: Boolean = true,
    val maxStackTraceDepth: Int? = null,
    /*JVM will fail to start if less than 208K*/
    /*nvm, I just programmatically print both the top and bottom of the stack now. Much better.*/
    val maxStackSize: String? = null,

    val otherArgs: List<JvmArg> = listOf(),

    ) {
    val args by lazy {
        arrayOf(
            /*https://stackoverflow.com/questions/65565209/nullpointers-in-javafx-when-using-a-large-canvas*/
            "-Dprism.maxvram=2G",
            *opt(xmx) { "-Xmx${mb.toInt()}m" },
            *ifTrue(enableAssertionsAndCoroutinesDebugMode) { "-enableassertions" },

            /*OmitStackTraceInFastThrow: makes sure that exceptions get a full stack trace always.*/
            /*https://stackoverflow.com/questions/2411487/nullpointerexception-in-java-with-no-stacktrace*/
            *ifTrue(stackTraceInFastThrow) { "-XX:-OmitStackTraceInFastThrow" },
            /*VM option 'ShowHiddenFrames' is diagnostic and must be enabled via -XX:+UnlockDiagnosticVMOptions.*/
            "-XX:+UnlockDiagnosticVMOptions",
            "-XX:+ShowHiddenFrames",
            *ifTrue(diagnosticVMOptions) { "-XX:+UnlockDiagnosticVMOptions" },


            *opt(maxStackTraceDepth) { "-XX:MaxJavaStackTraceDepth=${this}" },
            *opt(maxStackSize) { "-Xss${this}" },

            /*
            "Overhead of this feature is negligible and it can be safely turned on by default to simplify logging and diagnostic." - kotlinx.coroutines

            gives better stack traces with coroutines. Given the message above, I should have this enabled always.

            I tested it, and this definitely works! And the correct format is in fact "-Dkotlinx.coroutines.debug". "-Dkotlinx.coroutines.debug=true" may work, I have not tested it. But  "-Dkotlinx.coroutines.debug" definitely works.
            * */
            @SeeURL("https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/topics/debugging.md#stacktrace-recovery")
            "-Dkotlinx.coroutines.debug",

            *otherArgs.map { it.toRawArg() }.toTypedArray(),


            )
    }
}


interface JvmArg {
    fun toRawArg(): String
}