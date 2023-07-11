package matt.model.rest.request

import matt.model.data.hash.md5.MD5

interface Upload
class UploadInfo(
    val md5: MD5?,
    val contentLength: Long
)


object NoArg


