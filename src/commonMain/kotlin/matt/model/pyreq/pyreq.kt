package matt.model.pyreq

import kotlinx.serialization.Serializable
import matt.model.code.mod.GradleKSubProjectPath
import kotlin.jvm.JvmInline


@Serializable
sealed interface PythonRequirement {
    val modName: String
}

@Serializable
class AbsolutePythonRequirement(
    override val modName: String,
    private val absoluteVersion: String? = null
) : PythonRequirement {
    fun resolvedVersion() = absoluteVersion
}

@Serializable
class ProjectPythonRequirement(
    val kSub: GradleKSubProjectPath,
    override val modName: String
) : PythonRequirement



interface CondaEnv {
    val name: String
}
@Serializable
@JvmInline
value class CondaEnvPointer(override val name: String) : CondaEnv


