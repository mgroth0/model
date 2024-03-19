package matt.model.code.tex

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.internal.FormatLanguage
import matt.model.code.SimpleFormatCode
import kotlin.jvm.JvmInline


@JvmInline
@Serializable
value class TexCode @OptIn(InternalSerializationApi::class) constructor(
    @FormatLanguage("TeX", "", "") override val code: String
) : SimpleFormatCode<TexCode> {
    override fun formatted() = this
}

