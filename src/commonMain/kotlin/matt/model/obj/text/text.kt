package matt.model.obj.text

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

interface MightExist {
    fun exists(): Boolean
    val doesNotExist get() = !exists()
}

fun <T : MightExist> T.takeIfExists() = takeIf { exists() }

interface MightExistAndWritableText : MightExist, WritableText
interface MightExistAndWritableBytes : MightExist, WritableBytes