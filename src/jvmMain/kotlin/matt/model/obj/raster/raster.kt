package matt.model.obj.raster

import matt.image.toBufferedImage
import matt.model.data.rect.IntRectSize

fun Rasterizable.toBufferedImage(size: IntRectSize) = rasterize(size).toBufferedImage()
