package matt.model.data.uuid

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class UUID(val value: String)
