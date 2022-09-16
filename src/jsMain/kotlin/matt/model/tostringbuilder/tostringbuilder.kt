package matt.model.tostringbuilder

import kotlin.reflect.KClass

actual fun KClass<*>.firstSimpleName() = this.simpleName!!