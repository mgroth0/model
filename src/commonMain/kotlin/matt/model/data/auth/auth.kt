package matt.model.data.auth

class Auth(
  val key: String,
  val secret: String
)


data class GitHubKeys(
  val readPackagesForever: String,
  val pushReleasesForever: String,
  val apiKey: String,
  val apiOrgKey: String,
  val ghaUsageAuditKey: String
)


data class EverNoteKeys(
  val key: String,
  val secret: String,
  val authToken: String,
  val changingTokenFilePath: String
)