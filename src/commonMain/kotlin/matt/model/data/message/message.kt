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
import matt.lang.anno.Duplicated
import matt.lang.model.file.CaseInSensitiveFilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFile
import matt.lang.model.file.FsFilePath
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.constructFilePath
import matt.lang.model.file.exts.contains
import matt.lang.model.file.fixFilePath
import matt.prim.str.ensureSuffix
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName

/*only using this as a workaround for an internal kotlinx.serialization bug*/
internal object MacFileDebugSerializer : KSerializer<MacFile> {
    override val descriptor = serialDescriptor<String>()

    override fun deserialize(decoder: Decoder): MacFile {
        return MacFile(decoder.decodeString())
    }

    override fun serialize(
        encoder: Encoder,
        value: MacFile
    ) {
        encoder.encodeString(value.path)
    }

}

@JvmInline
@Serializable(with = MacFileDebugSerializer::class)
value class MacFile(private val fPath: String) : FsFile {

    override val fsFilePath: FsFilePath get() = CaseInSensitiveFilePath(fPath, MacFileSystem)

    override val partSep: String
        get() = MacFileSystem.separatorChar.toString()

    override fun get(item: String): FsFile {
        require(!item.startsWith(partSep))
        return MacFile(fsFilePath.path.ensureSuffix(partSep) + item)
    }

    override fun toString() = filePath

    override val fileSystem: FileSystem
        get() = MacFileSystem

    override val isAbsolute: Boolean
        get() = path.startsWith(partSep)
    override val parent: FsFile?
        get() = if (isRoot) null else MacFile(fPath.substringBeforeLast(partSep))
    override val isRoot: Boolean
        get() = fsFilePath.path == partSep

    @Duplicated
    override fun relativeTo(other: FsFile): FsFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        return MacFile(path.path)
    }
}

@Serializable(with = AbsMacFile.Companion::class)
class AbsMacFile(path: String) : FsFile {

    override val isAbsolute = true

    companion object : KSerializer<AbsMacFile> {
        override val descriptor by lazy { serialDescriptor<String>() }

        override fun deserialize(decoder: Decoder): AbsMacFile {
            return AbsMacFile(decoder.decodeString())
        }

        override fun serialize(
            encoder: Encoder,
            value: AbsMacFile
        ) {
            encoder.encodeString(value.idFile)
        }
    }

    private val idFile = fixFilePath(path.lowercase().let {
        if (it.startsWith("/")) it
        else "/$it"
    })

    override val fsFilePath: FsFilePath get() = CaseInSensitiveFilePath(idFile, MacFileSystem)

    override fun equals(other: Any?): Boolean {
        return other is AbsMacFile && other.filePath == filePath
    }

    override fun hashCode(): Int {
        return filePath.hashCode()
    }

    override val partSep: String
        get() = MacFileSystem.separatorChar.toString()

    override fun get(item: String): FsFile {
        require(!item.startsWith(partSep))
        return AbsMacFile(fsFilePath.path.ensureSuffix(partSep) + item)
    }

    override fun toString() = filePath
    override val fileSystem: FileSystem
        get() = MacFileSystem

    override val parent: FsFile?
        get() = if (isRoot) null else MacFile(path.substringBeforeLast(partSep))
    override val isRoot: Boolean
        get() = fsFilePath.path == partSep

    @Duplicated
    override fun relativeTo(other: FsFile): FsFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        return MacFile(path.path)
    }
}

/*only using this as a workaround for an internal kotlinx.serialization bug*/
@OptIn(ExperimentalSerializationApi::class)
object SFileDebugFileListSerializer : KSerializer<FileList> {
    override val descriptor = listSerialDescriptor<String>()

    private val innerSerializer by lazy { ListSerializer(serializer<String>()) }

    override fun deserialize(decoder: Decoder): FileList {
        return FileList(decoder.decodeSerializableValue(innerSerializer).map { MacFile(it) })
    }

    override fun serialize(
        encoder: Encoder,
        value: FileList
    ) {
        encoder.encodeSerializableValue(innerSerializer, value.map { it.path })
    }

}

@Serializable(with = SFileDebugFileListSerializer::class)
data class FileList(val files: List<MacFile>) : List<MacFile> by files