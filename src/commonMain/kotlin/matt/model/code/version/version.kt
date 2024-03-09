package matt.model.code.version

import kotlinx.serialization.Serializable
import matt.lang.common.substringAfterFirst
import matt.lang.common.substringBeforeFirst
import matt.lang.common.substringBeforeSingular
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
        major = input.substringBeforeFirst(".").toInt(),
        minor = input.substringAfterFirst(".").substringBeforeSingular(".").toInt(),
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
