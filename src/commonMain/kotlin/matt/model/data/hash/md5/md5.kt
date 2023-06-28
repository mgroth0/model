package matt.model.data.hash.md5

import kotlinx.serialization.Serializable
import matt.model.op.convert.StringConverter
import kotlin.jvm.JvmInline

object MD5Converter : StringConverter<MD5> {
    override fun toString(t: MD5): String {
        return t.toString()
    }

    override fun fromString(s: String): MD5 {
        return MD5(s)
    }

}

@Serializable
@JvmInline
value class MD5(val value: String) {
    override fun toString() = value
}