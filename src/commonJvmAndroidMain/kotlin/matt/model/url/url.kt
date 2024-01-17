package matt.model.url

import java.net.URI
import java.net.URL


interface URLLike {
    fun toJavaURL(): URL
    fun toJavaURI(): URI
}