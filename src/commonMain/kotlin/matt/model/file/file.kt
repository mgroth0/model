package matt.model.file

interface FilePath {
  val filePath: String
  val partSep: String
}

interface FolderPath: FilePath