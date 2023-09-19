package matt.model.obj.text

import matt.lang.idea.ReferenceToSomethingExternal
import matt.lang.model.file.FsFile
import matt.model.data.byte.ByteSize


interface HasText {
    val text: String
}

interface HasBytes {
    val bytes: ByteArray
}

interface WritableText : HasText {
    override var text: String
}

interface WritableBytes : HasBytes {
    override var bytes: ByteArray
}


interface MightExist : ReferenceToSomethingExternal {
    fun exists(): Boolean
    val doesNotExist get() = !exists()
}

fun <T : MightExist> T.takeIfExists() = takeIf { exists() }

interface MightExistAndWritableText : MightExist, WritableText
interface MightExistAndWritableBytes : MightExist, WritableBytes

interface ReadableFile : HasBytes, HasText, MightExist, FsFile {
    override fun exists(): Boolean
    fun listFilesAsList(): List<ReadableFile>?
    fun isDir(): Boolean
    fun size(): ByteSize
    fun readText() = text
    override val doesNotExist get() = !exists()
}

interface WritableFile : ReadableFile, WritableBytes, WritableText {
    override var text: String
    override var bytes: ByteArray
    fun mkdirs(): Boolean
    fun mkdir()
    fun deleteIfExists()
    fun writeText(s: String) {
        text = s
    }
}
