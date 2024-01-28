package matt.model.code.sharedmem

import matt.lang.convert.BiConverter
import matt.lang.model.file.types.Json
import matt.model.data.prop.ConvertedSuspendProperty
import matt.model.data.prop.SimpleSuspendProperty


interface SharedMemoryDomain {
    val json: kotlinx.serialization.json.Json
    fun str(key: String): SimpleSuspendProperty<String>
}

fun <T : Any> SharedMemoryDomain.converted(
    key: String,
    converter: BiConverter<String, T>
) = ConvertedSuspendProperty(str(key), converter)



