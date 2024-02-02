package matt.model.code.version

import kotlinx.serialization.Serializable
import matt.model.data.release.GeneralVersion
import kotlin.jvm.JvmInline

interface JavaVersion {
    val major: Int
}

@Serializable
@JvmInline
value class JavaMajorVersion(override val major: Int) : JavaVersion {
    override fun toString(): String = major.toString()
}

@Serializable
data class JavaPatchedVersion(
    override val major: Int,
    val minor: Int,
    val patch: Int
) : JavaVersion {
    constructor(input: String) : this(
        major = input.substringBefore(".").toInt(),
        minor = input.substringAfter(".").substringBefore(".").toInt(),
        patch = input.substringAfterLast(".").toInt()
    )

    override fun toString(): String = "$major.$minor.$patch"
}

@Serializable
@JvmInline
value class GradleVersion(val version: String) : Comparable<GradleVersion> {
    private val asGeneralVersion get() = GeneralVersion(version)

    init {
        asGeneralVersion /*do general version checks*/
    }

    override fun compareTo(other: GradleVersion): Int = asGeneralVersion.compareTo(other.asGeneralVersion)
}

@Serializable
@JvmInline
value class PythonVersion(val version: String)


interface TomlVersionsInter {
    val python: PythonVersion
    fun tomlVersion(name: String): String
}
