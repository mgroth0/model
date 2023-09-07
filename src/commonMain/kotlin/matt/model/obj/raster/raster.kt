package matt.model.obj.raster

import matt.image.ImmutableRaster
import matt.image.Png
import matt.model.data.rect.IntRectSize

interface Rasterizable {
    fun rasterize(size: IntRectSize): ImmutableRaster
}

interface PngRasterizable: Rasterizable {
    override fun rasterize(size: IntRectSize): Png
}