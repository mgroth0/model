package matt.model.data.release

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import matt.model.data.release.UpdateLevel.FEATURE
import matt.model.data.release.UpdateLevel.PATCH
import matt.model.data.release.UpdateLevel.PUBLISH
import matt.prim.str.joinWithPeriods


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

@Serializable
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

    override fun toString(): String {
        return listOf(first, second, third).joinToString(".")
    }

    fun increment(level: UpdateLevel): Version {
        return when (level) {
            PUBLISH -> Version(first + 1, 0, 0)
            FEATURE -> Version(first, second + 1, 0)
            PATCH   -> Version(first, second, third + 1)
        }
    }

}

@Serializable
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

    internal companion object : KSerializer<FourLevelVersion> {

        override val descriptor by lazy { serialDescriptor<String>() }

        override fun deserialize(decoder: Decoder): FourLevelVersion {
            return FourLevelVersion(decoder.decodeString())
        }


        override fun serialize(
            encoder: Encoder,
            value: FourLevelVersion
        ) {
            encoder.encodeString(value.version)
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