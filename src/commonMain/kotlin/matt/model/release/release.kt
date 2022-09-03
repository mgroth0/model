package matt.model.release

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@Serializable
data class Release(
  val name: String,
  val `zipball_url`: String,
  val `tarball_url`: String,
  val commit: Commit,
  val `node_id`: String
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
	  .takeIf { it != 0 } ?: ((second.compareTo(other.second)).takeIf { it != 0 } ?: (third.compareTo(other.third)))
  }

  override fun toString(): String {
	return listOf(first, second, third).joinToString(".")
  }
}
