@file:JvmName("MessageJvmKt")

package matt.model.data.message

import kotlinx.serialization.ExperimentalSerializationApi
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
import kotlin.jvm.JvmName

/*only using this as a workaround for an internal kotlinx.serialization bug*/
object SFileDebugSerializer : KSerializer<SFile> {
    override val descriptor = serialDescriptor<String>()

    override fun deserialize(decoder: Decoder): SFile {
        return SFile(decoder.decodeString())
    }

    override fun serialize(
        encoder: Encoder,
        value: SFile
    ) {
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

typealias SafeFile = AbsCaseInsensitiveFile

@Serializable(with = AbsCaseInsensitiveFile.Companion::class)
class AbsCaseInsensitiveFile(path: String) : FilePath {

    companion object : KSerializer<AbsCaseInsensitiveFile> {
        override val descriptor by lazy { serialDescriptor<String>() }

        override fun deserialize(decoder: Decoder): AbsCaseInsensitiveFile {
            return AbsCaseInsensitiveFile(decoder.decodeString())
        }

        override fun serialize(
            encoder: Encoder,
            value: AbsCaseInsensitiveFile
        ) {
            encoder.encodeString(value.idFile)
        }
    }

    private val idFile = path.lowercase().let {
        if (it.startsWith("/")) it
        else "/$it"
    }

    override fun equals(other: Any?): Boolean {
        return other is AbsCaseInsensitiveFile && other.idFile == idFile
    }

    override fun hashCode(): Int {
        return idFile.hashCode()
    }

    override val fName: String
        get() = when {
            "/" in idFile  -> idFile.substringAfterLast("/")
            "\\" in idFile -> idFile.substringAfterLast("\\")
            else           -> idFile
        }


    override val filePath: String
        get() = idFile
    override val partSep: String
        get() = TODO("Not yet implemented")

    override fun isDir(): Boolean {
        TODO("Not yet implemented")
    }

    override fun toString() = idFile

}

/*only using this as a workaround for an internal kotlinx.serialization bug*/
@OptIn(ExperimentalSerializationApi::class)
object SFileDebugFileListSerializer : KSerializer<FileList> {
    override val descriptor = listSerialDescriptor<String>()

    private val innerSerializer by lazy { ListSerializer(serializer<String>()) }

    override fun deserialize(decoder: Decoder): FileList {
        return FileList(decoder.decodeSerializableValue(innerSerializer).map { SFile(it) })
    }

    override fun serialize(
        encoder: Encoder,
        value: FileList
    ) {
        encoder.encodeSerializableValue(innerSerializer, value.map { it.path })
    }

}

@Serializable(with = SFileDebugFileListSerializer::class)
data class FileList(val files: List<SFile>) : List<SFile> by files