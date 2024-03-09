package matt.model.obj.raster.j

import matt.image.desktop.toBufferedImage
import matt.model.data.rect.IntRectSize
import matt.model.obj.raster.Rasterizable
import java.awt.image.BufferedImage

fun Rasterizable.toBufferedImage(size: IntRectSize): BufferedImage = rasterize(size).toBufferedImage()
