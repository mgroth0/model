package matt.model.file


interface FilePath {
  val fName: String
  val filePath: String
  val partSep: String
  fun isDir(): Boolean
}

interface FolderPath: FilePath