@file:JvmName("MessageJvmKt")

package matt.model.data.message

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import matt.lang.anno.Duplicated
import matt.lang.model.file.CaseInSensitiveFilePath
import matt.lang.model.file.CaseSensitiveFilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFile
import matt.lang.model.file.FsFilePath
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.casefix.fixFilePath
import matt.lang.model.file.constructFilePath
import matt.lang.model.file.exts.contains
import matt.model.code.sys.LinuxFileSystem
import matt.model.ser.EncodedAsStringKSerializer
import matt.prim.str.ensureSuffix
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName

/*only using this as a workaround for an internal kotlinx.serialization bug*/
internal object MacFileDebugSerializer : EncodedAsStringKSerializer<MacFile>() {

    override fun String.decode(): MacFile {
        return MacFile(this)
    }

    override fun MacFile.encodeToString(): String {
        return path
    }


}

@JvmInline
@Serializable(with = MacFileDebugSerializer::class)
value class MacFile(private val fPath: String) : FsFile {
    override fun withinFileSystem(newFileSystem: FileSystem): FsFile {
        TODO("Not yet implemented")
    }

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

    companion object : EncodedAsStringKSerializer<AbsMacFile>() {

        override fun String.decode(): AbsMacFile {
            return AbsMacFile(this)
        }

        override fun AbsMacFile.encodeToString(): String {
            return idFile
        }
    }

    private val idFile = fixFilePath(path.lowercase().let {
        if (it.startsWith("/")) it
        else "/$it"
    })

    override fun withinFileSystem(newFileSystem: FileSystem): FsFile {
        TODO("Not yet implemented")
    }

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


@Serializable(with = RelLinuxFile.Companion::class)
class RelLinuxFile(path: String) : FsFile {

    init {
        check(!path.startsWith("/"))
    }

    override val isAbsolute = false

    companion object : EncodedAsStringKSerializer<RelLinuxFile>() {

        override fun String.decode(): RelLinuxFile {
            return RelLinuxFile(this)
        }

        override fun RelLinuxFile.encodeToString(): String {
            return path
        }
    }

    val idPath = fixFilePath(path)


    override fun withinFileSystem(newFileSystem: FileSystem): FsFile {
        TODO("Not yet implemented")
    }

    override val fsFilePath: FsFilePath get() = CaseSensitiveFilePath(idPath, LinuxFileSystem)

    override fun equals(other: Any?): Boolean {
        return other is RelLinuxFile && other.idPath == idPath
    }

    override fun hashCode(): Int {
        return idPath.hashCode()
    }

    override val partSep: String
        get() = LinuxFileSystem.separatorChar.toString()

    override fun get(item: String): FsFile {
        require(!item.startsWith(partSep))
        return RelLinuxFile(fsFilePath.path.ensureSuffix(partSep) + item)
    }

    override fun toString() = filePath
    override val fileSystem: FileSystem
        get() = LinuxFileSystem

    override val parent: FsFile
        get() {
            val parentPath = path.substringBeforeLast(partSep)
            check(parentPath.isNotEmpty())
            check(parentPath != path)
            return /*if (isRoot) null else*/ RelLinuxFile(parentPath)
        }
    override val isRoot: Boolean
        get() = TODO("not sure this property makes sense in a relative file")

    @Duplicated
    override fun relativeTo(other: FsFile): FsFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        @Suppress("UNUSED_VARIABLE")
        val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        TODO()
        /*return MacFile(path.path)*/
    }
}


@Serializable(with = AbsLinuxFile.Companion::class)
class AbsLinuxFile(path: String) : FsFile {

    init {
        check(path.startsWith("/"))
    }

    override val isAbsolute = true

    companion object : EncodedAsStringKSerializer<AbsLinuxFile>() {

        override fun String.decode(): AbsLinuxFile {
            return AbsLinuxFile(this)
        }

        override fun AbsLinuxFile.encodeToString(): String {
            return path
        }
    }

    val idPath = fixFilePath(path)


    override fun withinFileSystem(newFileSystem: FileSystem): FsFile {
        TODO("Not yet implemented")
    }

    override val fsFilePath: FsFilePath get() = CaseSensitiveFilePath(idPath, LinuxFileSystem)

    override fun equals(other: Any?): Boolean {
        return other is AbsLinuxFile && other.idPath == idPath
    }

    override fun hashCode(): Int {
        return idPath.hashCode()
    }

    override val partSep: String
        get() = LinuxFileSystem.separatorChar.toString()

    override fun get(item: String): FsFile {
        require(!item.startsWith(partSep))
        return AbsLinuxFile(fsFilePath.path.ensureSuffix(partSep) + item)
    }

    override fun toString() = filePath
    override val fileSystem: FileSystem
        get() = LinuxFileSystem

    override val parent: FsFile?
        get() = if (isRoot) null else AbsLinuxFile(path.substringBeforeLast(partSep))
    override val isRoot: Boolean
        get() = fsFilePath.path == partSep

    @Duplicated
    override fun relativeTo(other: FsFile): FsFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        @Suppress("UNUSED_VARIABLE")
        val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        TODO()
        /*return MacFile(path.path)*/
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


/*
@Serializable
data class AbsSfsFile(
    val path: String,

) : FsFile {
    init {

    }
}*/






