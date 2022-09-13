package matt.model.tech.md

fun extractMdValue(mdText: String, key: String) = mdText.lineSequence().filter { it.startsWith("[//]: #") }
.firstOrNull { it.substringAfter("(").substringBefore(")").substringBefore(":") == key }?.substringAfter("(")
  ?.substringAfter(":")?.substringBefore(")")