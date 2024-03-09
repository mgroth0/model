package matt.model.data.id

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface CodesignIdentity {
    val arg: String
}
/*

significantly restricted!

I used to use this when I was not sharing a program, but I just needed it to work with Instruments.

This might come in handy at some point again. Unsure.

*/
@Serializable
data object AdHoc: CodesignIdentity  {
    override val arg = "-"
}
@Serializable
data class DeveloperIdApplication(
    val name: String,
    val teamId: String
): CodesignIdentity {
    init {
        setOf(
            name,
            teamId
        ).forEach {
            check(DEV_ID_APP !in it)
            check(name.isNotEmpty())
            check('(' !in it)
            check(')' !in it)
            check(':' !in it)
            check(it.first().isLetterOrDigit())
            check(it.last().isLetterOrDigit())
        }
    }
    @Transient
    val fullId = "$DEV_ID_APP: $name ($teamId)"
    @Transient
    override val arg = fullId
    companion object {
        private const val DEV_ID_APP = "Developer ID Application"
    }
}
