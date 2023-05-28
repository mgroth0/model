package matt.model.code.version

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class JavaVersion(val version: String)

@Serializable
@JvmInline
value class GradleVersion(val version: String)

@Serializable
@JvmInline
value class PythonVersion(val version: String)