package matt.model.code.mod

import kotlinx.serialization.Serializable
import matt.lang.require.requireContains
import matt.lang.require.requireStartsWith
import matt.model.code.idea.ModIdea
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
    companion object {

        private const val REQUIRED_PREFIX = ":k:"

        fun fromUniqueCamelCaseName(camelCaseID: String): GradleKSubProjectPath {
            val parts = mutableListOf("")
            camelCaseID.forEach {
                if (it.isUpperCase()) {
                    parts += ""
                }
                parts[parts.size - 1] = parts.last() + it.lowercase()
            }
            return GradleKSubProjectPath(":k:${parts.joinToString(separator = ":")}")
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

val RelativeToKMod.gradlePath get() = ":k:${relToKNames.joinToString(":")}"
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


@Serializable
@JvmInline
value class GradleTaskPath(val path: String) {
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
}