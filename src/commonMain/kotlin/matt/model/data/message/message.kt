package matt.model.data.message

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class SFile(val path: String) {
  override fun toString() = path
}

@Serializable
class FileList(val files: List<SFile>): List<SFile> by files