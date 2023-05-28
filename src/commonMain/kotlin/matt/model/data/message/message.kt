package matt.model.data.message

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import matt.model.data.file.FilePath
import kotlin.jvm.JvmInline

/*only using this as a workaround for an internal kotlinx.serialization bug*/
object SFileDebugSerializer : KSerializer<SFile> {
    override val descriptor = serialDescriptor<String>()

    override fun deserialize(decoder: Decoder): SFile {
        return SFile(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: SFile) {
        encoder.encodeString(value.path)
    }

}

@JvmInline
@Serializable(with = SFileDebugSerializer::class)
value class SFile(val path: String) : FilePath {


    override val fName: String
        get() = when {
            "/" in path  -> path.substringAfterLast("/")
            "\\" in path -> path.substringAfterLast("\\")
            else         -> path
        }


    override val filePath: String
        get() = path
    override val partSep: String
        get() = TODO("Not yet implemented")

    override fun isDir(): Boolean {
        TODO("Not yet implemented")
    }

    override fun toString() = path
}

/*only using this as a workaround for an internal kotlinx.serialization bug*/
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
object SFileDebugFileListSerializer : KSerializer<FileList> {
    override val descriptor = listSerialDescriptor<String>()

    private val innerSerializer by lazy { ListSerializer(String::class.serializer()) }

    override fun deserialize(decoder: Decoder): FileList {
        return FileList(decoder.decodeSerializableValue(innerSerializer).map { SFile(it) })
    }

    override fun serialize(encoder: Encoder, value: FileList) {
        encoder.encodeSerializableValue(innerSerializer, value.map { it.path })
    }

}

@Serializable(with = SFileDebugFileListSerializer::class)
class FileList(val files: List<SFile>) : List<SFile> by files