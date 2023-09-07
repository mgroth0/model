package matt.model.code.tex

import kotlinx.serialization.Serializable
import matt.image.ImmutableRaster
import matt.model.code.SimpleFormatCode
import matt.model.data.rect.IntRectSize
import matt.model.obj.raster.Rasterizable
import kotlin.jvm.JvmInline


@JvmInline
@Serializable
value class TexCode(override val code: String) : SimpleFormatCode<TexCode>, Rasterizable {
    override fun formatted() = this
    override fun rasterize(size: IntRectSize): ImmutableRaster {
        TODO("Not yet implemented")
    }
}

