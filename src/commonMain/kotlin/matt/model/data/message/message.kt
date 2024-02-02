@file:JvmName("MessageJvmKt")

package matt.model.data.message

import kotlinx.serialization.Serializable
import matt.lang.anno.Duplicated
import matt.lang.model.file.CaseInSensitiveFilePath
import matt.lang.model.file.CaseSensitiveFilePath
import matt.lang.model.file.CaseSensitivityAwareFilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFileBase
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.casefix.fixFilePath
import matt.lang.model.file.constructFilePath
import matt.lang.model.file.exts.contains
import matt.model.code.sys.LinuxFileSystem
import matt.model.ser.EncodedAsStringKSerializer
import kotlin.jvm.JvmName

sealed class SerializableFile : FsFileBase<SerializableFile>() {
    final override fun toString() = path
}

sealed class AbsoluteSerializableFileBase(
    inputPath: String
) : SerializableFile() {

    init {
        check(inputPath.startsWith("/")) {
            "$inputPath should start with '/'"
        }
    }

    final override val isAbsolute = true

    final override val isRoot: Boolean
        get() = fsFilePath.path == partSep


}

sealed class RelativeSerializableFileBase(inputPath: String) : SerializableFile() {

    init {
        check(!inputPath.startsWith("/"))
    }

    final override val isAbsolute = false

    final override val isRoot: Boolean
        get() = TODO("not sure if the property isRoot makes sense in a relative file")

}


internal abstract class SerializableFileSerializer<T : SerializableFile>() : EncodedAsStringKSerializer<T>() {
    final override fun T.encodeToString(): String = path
}


@Serializable(with = RelMacFile.Serializer::class)
class RelMacFile(private val fPath: String) : RelativeSerializableFileBase(
    inputPath = fPath
) {
    internal companion object Serializer : SerializableFileSerializer<RelMacFile>() {
        override fun String.decode() = RelMacFile(this)
    }

    override fun withinFileSystem(newFileSystem: FileSystem): SerializableFile {
        TODO()
    }

    override fun fileInSameFs(path: String): SerializableFile = RelMacFile(path)

    override val fsFilePath: CaseSensitivityAwareFilePath get() = CaseInSensitiveFilePath(fPath, MacFileSystem)

    override val partSep: String
        get() = MacFileSystem.separatorChar.toString()


    override val fileSystem = MacFileSystem

    override val parent: SerializableFile?
        get() = if (isRoot) null else RelMacFile(fPath.substringBeforeLast(partSep))


    @Duplicated
    override fun relativeTo(other: SerializableFile): SerializableFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        return RelMacFile(path.path)
    }


}


@Serializable(with = AbsMacFile.Serializer::class)
class AbsMacFile(path: String) : AbsoluteSerializableFileBase(
    inputPath = path
) {


    internal companion object Serializer : SerializableFileSerializer<AbsMacFile>() {

        override fun String.decode(): AbsMacFile = AbsMacFile(this)
    }

    override fun fileInSameFs(path: String): SerializableFile = AbsMacFile(path)

    private val idFile = fixFilePath(path.lowercase().let {
        if (it.startsWith("/")) it
        else "/$it"
    })

    override fun withinFileSystem(newFileSystem: FileSystem): SerializableFile {
        TODO()
    }

    override val fsFilePath: CaseSensitivityAwareFilePath get() = CaseInSensitiveFilePath(idFile, MacFileSystem)

    override val partSep = MacFileSystem.separatorChar.toString()


    override val fileSystem: FileSystem
        get() = MacFileSystem

    override val parent: SerializableFile?
        get() = if (isRoot) null else AbsMacFile(path.substringBeforeLast(partSep))

    @Duplicated
    override fun relativeTo(other: SerializableFile): SerializableFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        return RelMacFile(path.path)
    }
}


@Serializable(with = RelLinuxFile.Serializer::class)
class RelLinuxFile(path: String) : RelativeSerializableFileBase(
    inputPath = path
) {


    internal companion object Serializer : SerializableFileSerializer<RelLinuxFile>() {

        override fun String.decode(): RelLinuxFile = RelLinuxFile(this)
    }

    /*bad!!!!*/
    val idPath = fixFilePath(path)

    override fun fileInSameFs(path: String): SerializableFile = RelLinuxFile(path)


    override fun withinFileSystem(newFileSystem: FileSystem): SerializableFile {
        TODO()
    }

    override val fsFilePath: CaseSensitivityAwareFilePath get() = CaseSensitiveFilePath(idPath, LinuxFileSystem)


    override val partSep = LinuxFileSystem.separatorChar.toString()


    override val fileSystem = LinuxFileSystem

    override val parent: SerializableFile
        get() {
            val parentPath = path.substringBeforeLast(partSep)
            check(parentPath.isNotEmpty())
            check(parentPath != path)
            return /*if (isRoot) null else*/ RelLinuxFile(parentPath)
        }

    @Duplicated
    override fun relativeTo(other: SerializableFile): SerializableFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        @Suppress("UNUSED_VARIABLE") val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        TODO()/*return MacFile(path.path)*/
    }
}


@Serializable(with = AbsLinuxFile.Serializer::class)
class AbsLinuxFile(path: String) : AbsoluteSerializableFileBase(
    inputPath = path
) {


    internal companion object Serializer : SerializableFileSerializer<AbsLinuxFile>() {
        override fun String.decode(): AbsLinuxFile = AbsLinuxFile(this)
    }

    override fun fileInSameFs(path: String): SerializableFile = AbsLinuxFile(path)

    /*bad!!!*/
    val idPath = fixFilePath(path)

    override fun withinFileSystem(newFileSystem: FileSystem): SerializableFile {
        TODO()
    }

    override val fsFilePath: CaseSensitivityAwareFilePath get() = CaseSensitiveFilePath(idPath, LinuxFileSystem)

    override val partSep = LinuxFileSystem.separatorChar.toString()


    override val fileSystem = LinuxFileSystem

    override val parent: SerializableFile?
        get() = if (isRoot) null else AbsLinuxFile(path.substringBeforeLast(partSep))

    @Duplicated
    override fun relativeTo(other: SerializableFile): SerializableFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        @Suppress("UNUSED_VARIABLE") val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        TODO()/*return MacFile(path.path)*/
    }
}
