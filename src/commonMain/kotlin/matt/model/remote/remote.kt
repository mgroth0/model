package matt.model.remote



interface HostAndUser {
    val host: String
    val user: String
}

data class SimpleHostAndUser(
    override val host: String,
    override val user: String
) : HostAndUser
