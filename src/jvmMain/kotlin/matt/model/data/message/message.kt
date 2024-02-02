package matt.model.data.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


class ActionResult(
    val message: InterAppMessage?
)

val NOTHING_TO_SEND = ActionResult(message = null)

val YES_ACTION = ActionResult(message = YES)

val ACTION_SUCCESS = ActionResult(message = SUCCESS)
val ACTION_FAIL = ActionResult(message = FAIL)


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
data object FAIL : Result

@Serializable
class Text(val text: String) : InterAppMessage

@Serializable
data object PONG : InterAppMessage

@Serializable
data object YES : InterAppMessage


@Serializable
class FileMessage(val file: AbsMacFile) : InterAppMessage

@Serializable
class FilesMessage(val files: List<AbsMacFile>) : InterAppMessage {
    //  constructor(files: List<SFile>): this(*files.toTypedArray())
}

@Serializable
sealed interface InterAppAction : InterAppMessage

@Serializable
data object ACTIVATE : InterAppAction

@Serializable
data object EXIT : InterAppAction

@Serializable
data object PING : InterAppAction

@Serializable
data object GET_ACTIVE_FILE : InterAppAction

@Serializable
data object GET_ALL_OPEN_FILES : InterAppAction

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
    val bookmark: String,
) : OpenSpecificPsiInter

@Serializable
data class OpenSpecific(
    override val qualifiedName: String,
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

@Serializable
sealed interface FormatRequest {
    val text: String
}

@Serializable
class FormatKotlinRequest(
    override val text: String,
    val script: Boolean
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
    val reportToPrintAndThrow: String?,
): FormatResult





