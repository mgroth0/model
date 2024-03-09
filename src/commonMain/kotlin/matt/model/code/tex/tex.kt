package matt.model.code.tex

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.internal.FormatLanguage
import matt.image.common.ImmutableRaster
import matt.model.code.SimpleFormatCode
import matt.model.data.rect.IntRectSize
import matt.model.obj.raster.Rasterizable
import kotlin.jvm.JvmInline


@JvmInline
@Serializable
value class TexCode(
    @FormatLanguage("TeX", "", "") override val code: String
) : SimpleFormatCode<TexCode>, Rasterizable {
    override fun formatted() = this
    override fun rasterize(size: IntRectSize): ImmutableRaster {
        TODO()
    }
}

