package matt.model.stream

import java.io.InputStream

interface Streamable {
  fun inputStream(): InputStream
}