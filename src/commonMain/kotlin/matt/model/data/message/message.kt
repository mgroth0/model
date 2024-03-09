
package matt.model.data.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import matt.lang.anno.Duplicated
import matt.lang.model.file.CaseInSensitiveFilePath
import matt.lang.model.file.CaseSensitiveFilePath
import matt.lang.model.file.CaseSensitivityAwareFilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFileBase
import matt.lang.model.file.MacDefaultFileSystem
import matt.lang.model.file.casefix.fixFilePath
import matt.lang.model.file.constructFilePath
import matt.lang.model.file.exts.inDir
import matt.model.code.sys.LinuxFileSystem
import matt.model.ser.EncodedAsStringSerializer


class ActionResult(
    val message: InterAppMessage?
) {
    companion object {
        fun fail(message: String) = ActionResult(Failure(message))
        fun success() = ACTION_SUCCESS
    }
}

val NOTHING_TO_SEND = ActionResult(message = null)

val YES_ACTION = ActionResult(message = YES)

val ACTION_SUCCESS = ActionResult(message = SUCCESS)

val ACTION_EDIT_INVALIDED = ActionResult(message = EditInvalidated)

/*CONSIDER SENDING BACK THE NEW CONTENT AND ALLOWING THE SENDER TO USE THAT INSTEAD OF HAVING TO READ THE FILE AGAIN. IT CAN BE OPTIONAL.*/
@Serializable
data object EditInvalidated : InterAppMessage

@Serializable
class IntMessage(val i: Int) : InterAppMessage

@Serializable
class DoubleMessage(val d: Double) : InterAppMessage

@Serializable
class LongMessage(val l: Long) : InterAppMessage


@Serializable
sealed interface InterAppMessage

sealed class InterAppResult : InterAppMessage

@Serializable
sealed interface Result : InterAppMessage

@Serializable
data object SUCCESS : Result



@Serializable
class Failure(val message: String) : Result

@Serializable
class Text(val text: String) : InterAppMessage

@Serializable
data object PONG : InterAppMessage

@Serializable
data object YES : InterAppMessage

@Serializable
data object MalformedMessage: InterAppMessage


@Serializable
class FileMessage(val file: AbsMacFile) : InterAppMessage

@Serializable
class FilesMessage(val files: List<AbsMacFile>) : InterAppMessage

@Serializable
sealed interface InterAppAction : InterAppMessage

@Serializable
data object ACTIVATE : InterAppAction

@Serializable
data object EXIT : InterAppAction

@Serializable
data object PING : InterAppAction

@Serializable
data object GetActiveFile : InterAppAction

@Serializable
data object GetAllOpenFiles : InterAppAction


@Serializable
data object RunIdeaInspections : InterAppAction

@Serializable
class IdeaInspectionResults(
    val findings: Set<InspectionFinding>
) : InterAppMessage


@Serializable
class Go(val id: String) : InterAppAction

@Serializable
class Open(val thing: String) : InterAppAction

@Serializable
data object CommitAction : InterAppAction

@Serializable
data object CLOSE : InterAppAction

@Serializable
class OpenRelative(val thing: String) : InterAppAction

@Serializable
sealed interface OpenSpecificInter : InterAppAction

@Serializable
sealed interface OpenSpecificPsiInter : OpenSpecificInter

@Serializable
sealed interface OpenSpecificByIdentifier : OpenSpecificPsiInter {
    val qualifiedName: String
}

@Serializable
data class OpenMyBookmark(
    val bookmark: String
) : OpenSpecificPsiInter

@Serializable
data class OpenSpecific(
    override val qualifiedName: String
) : OpenSpecificByIdentifier

@Serializable
sealed interface OpenFileLoc {
    val fileName: String
    val lineIndex: Int
}

@Serializable
data class OpenVerySpecific(
    override val qualifiedName: String,
    override val fileName: String,
    override val lineIndex: Int
) : OpenSpecificByIdentifier, OpenFileLoc

@Serializable
data class OpenFileLocation(
    val filePath: AbsMacFile,
    override val lineIndex: Int
) : OpenSpecificInter, OpenFileLoc {
    override val fileName = filePath.name
}

@Serializable
data object OpenNearestGradleBuildscript : InterAppAction

@Serializable
data object OpenNearestBuildJson : InterAppAction

@Serializable
data object OpenNearestKotlinDescendant : InterAppAction

@Serializable
data class WriteFile(
    val file: AbsMacFile,
    val oldText: String /*THIS IS A PERFECT PLACE TO USE A HASH. I SHOULD USE A HASH HERE.*/,
    val newText: String
) : InterAppAction


@Serializable
class HarvardAuthor(val thing: String) : InterAppAction

@Serializable
class HarvardAuthorMeta(val thing: String) : InterAppAction

@Serializable
class KJGNav(val thing: String) : InterAppAction

@Serializable
class GoPage(val pageIndex: Int) : InterAppAction

@Serializable
data object GetPageIndex : InterAppAction

@Serializable
data object GetFile : InterAppAction

@Serializable
data class PDFFileMessage(val file: String) : InterAppMessage

@Serializable
data class PDFPageMessage(val pageNum: Int) : InterAppMessage

@Serializable
class ObjMessage<T>(val obj: T) : InterAppMessage

@Serializable
class JsonMessage(val json: JsonElement) : InterAppMessage

@Serializable
class Freecomp(val path: String) : InterAppMessage


sealed interface FormatJob {
    val text: String
}

class FormatJsonJob(
    override val text: String
): FormatJob



@Serializable
sealed interface FormatRequest: FormatJob



@Serializable
class FormatKotlinRequest(
    override val text: String,
    val script: Boolean
): FormatRequest

@Serializable
class FormatYamlRequest(
    override val text: String
): FormatRequest

@Serializable
class FormatHoconRequest(
    override val text: String
): FormatRequest




@Serializable
sealed interface FormatResult

@Serializable
sealed interface FormatSuccess: FormatResult

@Serializable
class FormatChange(
    val text: String
): FormatSuccess

@Serializable
data object NoChange: FormatSuccess



@Serializable
class FormatFailure(
    val message: String,
    val reportToPrintAndThrow: String?
): FormatResult

@Serializable
data object FormatConnectionFailure: FormatResult










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

    final override val isAbs = true

    final override val isRoot: Boolean
        get() = fsFilePath.path == partSep
}

sealed class RelativeSerializableFileBase(inputPath: String) : SerializableFile() {

    init {
        check(!inputPath.startsWith("/"))
    }

    final override val isAbs = false

    final override val isRoot: Boolean
        get() = TODO("not sure if the property isRoot makes sense in a relative file")
}


internal abstract class SerializableFileSerializer<T : SerializableFile>() : EncodedAsStringSerializer<T>() {
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

    override val fsFilePath: CaseSensitivityAwareFilePath get() = CaseInSensitiveFilePath(fPath, MacDefaultFileSystem)

    override val partSep: String
        get() = MacDefaultFileSystem.separatorChar.toString()


    override val myFileSystem = MacDefaultFileSystem

    override val parent: SerializableFile?
        get() = if (isRoot) null else RelMacFile(fPath.substringBeforeLast(partSep))


    @Duplicated
    override fun relativeTo(other: SerializableFile): SerializableFile {
        require(this inDir other) {
            "$this must be in $other in order to get the relative path"
        }
        val path =
            myFileSystem.constructFilePath(
                path.removePrefix(other.path).removePrefix(partSep)
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

    private val idFile =
        fixFilePath(
            path.lowercase().let {
                if (it.startsWith("/")) it
                else "/$it"
            }
        )

    override fun withinFileSystem(newFileSystem: FileSystem): SerializableFile {
        TODO()
    }

    override val fsFilePath: CaseSensitivityAwareFilePath get() = CaseInSensitiveFilePath(idFile, MacDefaultFileSystem)

    override val partSep = MacDefaultFileSystem.separatorChar.toString()


    override val myFileSystem: FileSystem
        get() = MacDefaultFileSystem

    override val parent: SerializableFile?
        get() = if (isRoot) null else AbsMacFile(path.substringBeforeLast(partSep))

    @Duplicated
    override fun relativeTo(other: SerializableFile): SerializableFile {
        require(this inDir other) {
            "$this must be in $other in order to get the relative path"
        }
        val path =
            myFileSystem.constructFilePath(
                path.removePrefix(other.path).removePrefix(partSep)
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


    override val myFileSystem = LinuxFileSystem

    override val parent: SerializableFile
        get() {
            val parentPath = path.substringBeforeLast(partSep)
            check(parentPath.isNotEmpty())
            check(parentPath != path)
            return /*if (isRoot) null else*/ RelLinuxFile(parentPath)
        }

    @Duplicated
    override fun relativeTo(other: SerializableFile): SerializableFile {
        require(this inDir other) {
            "$this must be in $other in order to get the relative path"
        }
        val path =
            myFileSystem.constructFilePath(
                path.removePrefix(other.path).removePrefix(partSep)
            )
        return RelMacFile(path.path)
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


    override val myFileSystem = LinuxFileSystem

    override val parent: AbsLinuxFile?
        get() = if (isRoot) null else AbsLinuxFile(path.substringBeforeLast(partSep))

    @Duplicated
    override fun relativeTo(other: SerializableFile): SerializableFile {
        require(this inDir other) {
            "$this must be in $other in order to get the relative path"
        }
        val path =
            myFileSystem.constructFilePath(
                path.removePrefix(other.path).removePrefix(partSep)
            )
        return RelLinuxFile(path.path)
    }
}






@Serializable
class InspectionFinding(
    val filePath: String,
    val lineNumber: Int, /*redundant?*/
    val problemName: String,
    val startOffset: Int,
    val endOffset: Int,
    val startCol: Int,
    val endCol: Int
)
