package matt.model.obj.raster

import matt.image.common.ImmutableRaster
import matt.image.common.Png
import matt.model.data.rect.IntRectSize

interface Rasterizable {
    fun rasterize(size: IntRectSize): ImmutableRaster
}

interface PngRasterizable: Rasterizable {
    override fun rasterize(size: IntRectSize): Png
}
