package matt.model.data.release

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import matt.lang.anno.SeeURL
import matt.model.data.release.UpdateLevel.FEATURE
import matt.model.data.release.UpdateLevel.PATCH
import matt.model.data.release.UpdateLevel.PUBLISH
import matt.prim.str.joinWithPeriods
import kotlin.jvm.JvmInline


@JvmInline
value class GeneralVersion(val version: String) : Comparable<GeneralVersion> {
    val parts get() = version.split(".").map { it.toInt() }

    init {
        check(version.isNotEmpty())
        check(parts.isNotEmpty())
    }

    override fun compareTo(other: GeneralVersion): Int {
        val parts1 = parts
        val parts2 = other.parts
        check(parts1.size == parts2.size)
        parts1.zip(parts2).forEach { (one, two) ->
            val r = one.compareTo(2)
            if (r != 0) return r
        }
        return 0
    }
}


@Serializable
data class Release(
    val name: String,
    val zipball_url: String,
    val tarball_url: String,
    val commit: Commit,
    val node_id: String
) {
    val version by lazy { Version(name) }
    override fun toString() = "a ${Release::class.simpleName} with version $version"
}

@Serializable
class Commit(
    /*sha must be private because it is not correctly retrieved from authenticated github client*/
    private val sha: String,
    /*url must be private because I DONT KNOW if it is correctly retrieved from authenticated github client*/
    private val url: String
)

@Serializable
data class VersionInfo(
    val version: Version,
    val downloadURL: String
)

@Serializer(forClass = Version::class)
object DefaultVersionSerializer

@Serializer(forClass = FourLevelVersion::class)
object DefaultVersionFourSerializer

@Serializable(with = Version.Companion::class)
data class Version(
    val first: Int,
    val second: Int,
    val third: Int
) : Comparable<Version> {


    constructor(versionString: String) : this(
        versionString.split(".")[0].toInt(), versionString.split(".")[1].toInt(), versionString.split(".")[2].toInt()
    )


    override operator fun compareTo(other: Version): Int {
        return (first.compareTo(other.first))
            .takeIf { it != 0 } ?: ((second.compareTo(other.second)).takeIf { it != 0 }
            ?: (third.compareTo(other.third)))
    }

    private val asString by lazy {
        listOf(first, second, third).joinToString(".")
    }

    override fun toString(): String {
        return asString
    }

    fun increment(level: UpdateLevel): Version {
        return when (level) {
            PUBLISH -> Version(first + 1, 0, 0)
            FEATURE -> Version(first, second + 1, 0)
            PATCH   -> Version(first, second, third + 1)
        }
    }

    operator fun inc() = increment(PATCH)


    internal companion object : KSerializer<Version> {

        override val descriptor by lazy { serialDescriptor<String>() }

        @SeeURL("https://github.com/Kotlin/kotlinx.serialization/issues/1512")
        private val secondaryJson = Json {
            useAlternativeNames = false
        }

        override fun deserialize(decoder: Decoder): Version {
            val e = (decoder as JsonDecoder).decodeJsonElement()
            if (e is JsonObject) {
                @SeeURL("https://github.com/Kotlin/kotlinx.serialization/issues/1512")
                return secondaryJson.decodeFromJsonElement(DefaultVersionSerializer, e)
            }
            check((e as JsonPrimitive).isString)
            return Version(e.content)
        }


        override fun serialize(
            encoder: Encoder,
            value: Version
        ) {
            encoder.encodeSerializableValue(DefaultVersionSerializer, value)
//            encoder.encodeString(value.asString)
        }

    }

}

enum class UpdateLevel {
    PUBLISH, FEATURE, PATCH;

    val firstVersion get() = Version(0, 0, 0).increment(this)
}


@Serializable(with = FourLevelVersion.Companion::class)
class FourLevelVersion(val version: String) : Comparable<FourLevelVersion> {
    constructor(
        part1: Int,
        part2: Int,
        part3: Int,
        part4: Int
    ) : this(listOf(part1, part2, part3, part4).joinWithPeriods())

    override fun toString(): String {
        return version
    }


    internal companion object : KSerializer<FourLevelVersion> {

        override val descriptor by lazy { serialDescriptor<String>() }

        @SeeURL("https://github.com/Kotlin/kotlinx.serialization/issues/1512")
        private val secondaryJson = Json {
            useAlternativeNames = false
        }

        override fun deserialize(decoder: Decoder): FourLevelVersion {
            val e = (decoder as JsonDecoder).decodeJsonElement()
            if (e is JsonObject) {
                @SeeURL("https://github.com/Kotlin/kotlinx.serialization/issues/1512")
                return secondaryJson.decodeFromJsonElement(DefaultVersionFourSerializer, e)
            }
            check((e as JsonPrimitive).isString)
            return FourLevelVersion(e.content)
        }


        override fun serialize(
            encoder: Encoder,
            value: FourLevelVersion
        ) {
            encoder.encodeSerializableValue(DefaultVersionFourSerializer, value)
//            encoder.encodeString(value.version)
        }

        private val COMPARATOR = Comparator<FourLevelVersion> { a, b -> a[0].compareTo(b[0]) }
            .then { a, b -> a[1].compareTo(b[1]) }
            .then { a, b -> a[2].compareTo(b[2]) }
            .then { a, b -> a[3].compareTo(b[3]) }
    }

    init {
        check(version.count { it == '.' } == 3)
        check(version.all { it == '.' || it.isDigit() })
    }

    private val theSplit = version.split(".")

    init {
        check(theSplit.size == 4)
    }

    private val theSplitInts = theSplit.map { it.toInt() }

    operator fun get(index: Int): Int {
        return theSplitInts[index]
    }

    override fun compareTo(other: FourLevelVersion) = COMPARATOR.compare(this, other)


}


interface OnlineVersionsLoader {
    suspend fun loadOnlineVersions(): List<Version>?
}