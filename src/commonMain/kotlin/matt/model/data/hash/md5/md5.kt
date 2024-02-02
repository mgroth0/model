package matt.model.data.hash.md5

import kotlinx.serialization.Serializable
import matt.prim.converters.StringConverter
import kotlin.jvm.JvmInline


@Serializable
@JvmInline
value class MD5(val value: String) {
    override fun toString() = value
}

object MD5Converter : StringConverter<MD5> {
    override fun toString(t: MD5): String = t.value

    override fun fromString(s: String): MD5 = MD5(s)

}


