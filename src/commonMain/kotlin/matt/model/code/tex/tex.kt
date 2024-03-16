package matt.model.code.tex

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.internal.FormatLanguage
import matt.model.code.SimpleFormatCode
import kotlin.jvm.JvmInline


@JvmInline
@Serializable
value class TexCode(
    @FormatLanguage("TeX", "", "") override val code: String
) : SimpleFormatCode<TexCode> {
    override fun formatted() = this
}

