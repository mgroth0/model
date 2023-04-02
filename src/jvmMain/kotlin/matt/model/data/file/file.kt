@file:JavaIoFileIsOk

package matt.model.data.file

import matt.model.code.ok.JavaIoFileIsOk
import java.io.File

/*provides a case-insensitive file (lowercase path) on case-insensitive file systems and case-sensitive files on case-sensitive systens*/
interface IDFile: FilePath {
  val idFile: File
}

interface IDFolder: IDFile, FolderPath