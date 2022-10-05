package matt.model.file



interface FilePath {
  val filePath: String
  val partSep: String
  fun isDir(): Boolean
}

interface FolderPath: FilePath