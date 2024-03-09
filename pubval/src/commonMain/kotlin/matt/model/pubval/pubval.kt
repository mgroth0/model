package matt.model.pubval

import matt.prim.str.removePrefixAndOrSuffix

fun rawGithubURL(
    user: String,
    repo: String,
    branch: String,
    path: String
) = listOf(
    "https://raw.githubusercontent.com",
    user,
    repo,
    branch,
    path
).joinToString(separator = "/") { it.removePrefixAndOrSuffix("/") }
