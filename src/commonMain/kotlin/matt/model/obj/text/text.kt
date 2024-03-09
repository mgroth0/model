package matt.model.obj.text

import matt.lang.anno.Open
import matt.lang.idea.ReferenceToSomethingExternal
import matt.lang.model.file.NewFsFile
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
    @Open
    val doesNotExist get() = !exists()
}

fun <T : MightExist> T.takeIfExists() = takeIf { exists() }

interface MightExistAndWritableText : MightExist, WritableText
interface MightExistAndWritableBytes : MightExist, WritableBytes

interface ReadableFile<F: ReadableFile<F>> : HasBytes, HasText, MightExist, NewFsFile<F> {
    @Open
    override fun exists(): Boolean
    fun listFilesAsList(): List<F>?

    fun isDir(): Boolean
    fun size(): ByteSize
    @Open fun readText() = text
    @Open
    override val doesNotExist get() = !exists()
}

interface WritableFile<F: WritableFile<F>> : ReadableFile<F>, WritableBytes, WritableText {
    @Open
    override var text: String
    @Open
    override var bytes: ByteArray
    fun mkdirs(): Boolean
    fun mkdir()
    fun deleteIfExists()
    @Open fun writeText(s: String) {
        text = s
    }
}


