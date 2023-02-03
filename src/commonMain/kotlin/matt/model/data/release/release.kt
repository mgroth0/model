package matt.model.data.release

import kotlinx.serialization.Serializable
import matt.model.data.release.UpdateLevel.FEATURE
import matt.model.data.release.UpdateLevel.PATCH
import matt.model.data.release.UpdateLevel.PUBLISH


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
  val sha: String,
  val url: String
)

@Serializable
data class Version(val first: Int, val second: Int, val third: Int): Comparable<Version> {
  constructor(versionString: String): this(
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
	  PUBLISH -> Version(first + 1, second, third)
	  FEATURE -> Version(first, second + 1, third)
	  PATCH   -> Version(first, second, third + 1)
	}
  }

}

@Serializable
enum class UpdateLevel {
  PUBLISH, FEATURE, PATCH
}
