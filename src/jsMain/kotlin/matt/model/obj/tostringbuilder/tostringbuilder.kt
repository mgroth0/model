package matt.model.obj.tostringbuilder

import kotlin.reflect.KClass

actual fun KClass<*>.firstSimpleName() = this.simpleName!!