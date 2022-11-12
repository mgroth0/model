package matt.model.im

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun BufferedImage.bytes() = run {
  val stream = ByteArrayOutputStream()
  ImageIO.write(this@bytes, "png", stream)
  stream.toByteArray()
}