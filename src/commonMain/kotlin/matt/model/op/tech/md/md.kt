package matt.model.op.tech.md

@Deprecated("I should never have to extract data from an markdown file. There should be a more stable alternative, always.")
fun extractMdValue(mdText: String, key: String) = mdText.lineSequence().filter { it.startsWith("[//]: #") }
    .firstOrNull { it.substringAfter("(").substringBefore(")").substringBefore(":") == key }?.substringAfter("(")
    ?.substringAfter(":")?.substringBefore(")")
