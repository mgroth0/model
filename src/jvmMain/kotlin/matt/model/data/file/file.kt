@file:JavaIoFileIsOk

package matt.model.data.file

import matt.lang.anno.ok.JavaIoFileIsOk
import matt.lang.model.file.FilePath
import matt.lang.model.file.FolderPath
import java.io.File

/*provides a case-insensitive file (lowercase path) on case-insensitive file systems and case-sensitive files on case-sensitive systens*/
interface IDFile: FilePath {
  val idFile: File
}

interface IDFolder: IDFile, FolderPath