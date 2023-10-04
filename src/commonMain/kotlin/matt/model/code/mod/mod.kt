package matt.model.code.mod

import kotlinx.serialization.Serializable
import matt.lang.SubRoots
import matt.lang.require.requireContains
import matt.lang.require.requireStartsWith
import matt.model.code.idea.ModIdea
import matt.prim.str.ensurePrefix
import kotlin.jvm.JvmInline


interface ModType : ModIdea

interface RelativeMod : ModType

interface KMod : ModType

/*implemented by generated KSubProject*/
interface RelativeToKMod : RelativeMod, KMod {
    val relToKNames: List<String>
}

val RelativeToKMod.uniqueCamelCaseName
    get() = relToKNames.withIndex().joinToString("") {
        if (it.index == 0) it.value else it.value
            /*.cap(). ugh, I put .cap in prim not lang*/
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }


interface GradlePath {
    val path: String
}


interface GradleProjectPath : GradlePath, ModType

val GradleProjectPath.isRoot get() = path == ":"
val GradleProjectPath.isSubRoot get() = path == ":${SubRoots.k.name}"
fun GradleProjectPath.asKSubPath() = GradleKSubProjectPath(path)

@JvmInline
value class GradleProjectPathImpl(override val path: String) : GradleProjectPath {
    init {
        requireStartsWith(path, ":")
    }
}

fun RelativeToKMod.asGradleKSubProjectPath() = when (this) {
    is GradleKSubProjectPath -> this
    else                     -> GradleKSubProjectPath(gradlePath)
}


@Serializable
@JvmInline
value class GradleKSubProjectPath(override val path: String) : GradleProjectPath, RelativeToKMod {
    constructor(vararg parts: String) : this(
        (listOf(SubRoots.k.name) + parts).joinToString(separator = ":").ensurePrefix(":")
    )

    companion object {

        private val REQUIRED_PREFIX = ":${SubRoots.k.name}:"

        fun fromUniqueCamelCaseName(camelCaseID: String): GradleKSubProjectPath {
            val parts = mutableListOf("")
            camelCaseID.forEach {
                if (it.isUpperCase()) {
                    parts += ""
                }
                parts[parts.size - 1] = parts.last() + it.lowercase()
            }
            return GradleKSubProjectPath(":${SubRoots.k.name}:${parts.joinToString(separator = ":")}")
        }


    }

    init {
        requireStartsWith(path, REQUIRED_PREFIX)
    }

    override val relToKNames get() = path.split(":").filter { it.isNotBlank() }.drop(1)

    override fun toString(): String {
        return path
    }
}

val RelativeToKMod.gradlePath get() = ":${SubRoots.k.name}:${relToKNames.joinToString(":")}"
val RelativeToKMod.jarBaseName get() = relToKNames.joinToString("-")
val RelativeToKMod.jsFileName get() = "$jarBaseName.js"
val RelativeToKMod.jsGzFileName get() = "$jarBaseName.js.gz"


interface AbsoluteMod : RelativeMod {
    val modName: String
    val groupName: String?
}

interface AbsoluteKMod : AbsoluteMod, RelativeToKMod {
    override val modName get() = relToKNames.last()
}

interface GradleTask {
    val taskName: String
}

interface GradleTaskSelector {
    val selectorArgument: String
}

@JvmInline
@Serializable
value class GradleTaskSelectorImpl(val selector: String) : GradleTask, GradleTaskSelector {
    val specifiesAProject get() = ":" in selector
    fun asTaskPath(): GradleTaskPath {
        if (!specifiesAProject) {
            error("GradleTaskSelector can only be a GradleTaskPath if it specifies a project")
        }
        return GradleTaskPath(path = selector)
    }

    override val taskName get() = selector.substringAfter(":")

    override val selectorArgument get() = selector
}


@Serializable
@JvmInline
value class GradleTaskPath(val path: String) : GradleTask, GradleTaskSelector {
    init {
        requireContains(path, ":") {
            "task path \"$path\" should have at least one colon, right? Or do root project tasks not have colons?"
        }
    }

    val gradleProjectPath get() = GradleProjectPathImpl(path.substringBeforeLast(":"))
    override fun toString(): String {
        return path
    }

    fun isTaskOfRootProject() = path.substringBeforeLast(":").isBlank()

    override val taskName get() = path.substringAfterLast(":")

    override val selectorArgument get() = path

    fun asGradleTaskSelectorForAllProjects() = GradleTaskSelectorImpl(selector = path.substringAfter(":"))

}

@Serializable
@JvmInline
value class GradleTaskName(override val taskName: String) : GradleTask {
    init {
        require(":" !in taskName)
    }

    fun asTaskOfRootProject() = GradleTaskPath(":$taskName")
}