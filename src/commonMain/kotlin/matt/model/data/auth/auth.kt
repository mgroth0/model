package matt.model.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val username: String,
    val password: String
)

class Auth(
    val key: String,
    val secret: String
)

interface GitHubKeys {
    val readPackagesForever: String
    val pushReleasesForever: String
    val apiKey: String
    val apiOrgKey: String
    val ghaUsageAuditKey: String
}

@Serializable
data class ActualGitHubKeys(
    override val readPackagesForever: String,
    override val pushReleasesForever: String,
    override val apiKey: String,
    override val apiOrgKey: String,
    override val ghaUsageAuditKey: String
) : GitHubKeys


class LazyGitHubKeys(
    private val readPackagesForeverProv: Lazy<String>,
    private val pushReleasesForeverProv: Lazy<String>,
    private val apiKeyProv: Lazy<String>,
    private val apiOrgKeyProv: Lazy<String>,
    private val ghaUsageAuditKeyProv: Lazy<String>
) : GitHubKeys {
    override val readPackagesForever: String get() = readPackagesForeverProv.value
    override val pushReleasesForever: String get() = pushReleasesForeverProv.value
    override val apiKey: String get() = apiKeyProv.value
    override val apiOrgKey: String get() = apiOrgKeyProv.value
    override val ghaUsageAuditKey: String get() = ghaUsageAuditKeyProv.value

    fun actualize() = ActualGitHubKeys(
        readPackagesForever = readPackagesForever,
        pushReleasesForever = pushReleasesForever,
        apiKey = apiKey,
        apiOrgKey = apiOrgKey,
        ghaUsageAuditKey = ghaUsageAuditKey
    )
}

data class EverNoteKeys(
    val key: String,
    val secret: String,
    val authToken: String,
    val changingTokenFilePath: String
)


