package matt.model.op.tech.md

import matt.lang.common.substringAfterSingular
import matt.lang.common.substringBeforeSingular

@Deprecated("I should never have to extract data from an markdown file. There should be a more stable alternative, always.")
fun extractMdValue(mdText: String, key: String) =
    mdText.lineSequence().filter { it.startsWith("[//]: #") }
        .firstOrNull {
            it.substringAfterSingular("(").substringBeforeSingular(")").substringBeforeSingular(":") == key
        }?.substringAfterSingular("(")
        ?.substringAfterSingular(":")?.substringBeforeSingular(")")
