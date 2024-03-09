package matt.model.pubval.test

import matt.model.pubval.rawGithubURL
import kotlin.test.Test


class PubValTests {
    @Test
    fun canGetRawGithubUrl() {
        rawGithubURL("a", "b", "c", "d")
    }
}
