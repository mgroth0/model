@file:JvmName("ClsJvmKt")

package matt.model.cls

import matt.lang.enums.filter
import matt.model.cls.ClassModifier.ABSTRACT
import matt.model.cls.ClassModifier.COMPANION
import matt.model.cls.ClassModifier.DATA
import matt.model.cls.ClassModifier.FINAL
import matt.model.cls.ClassModifier.FUN_INTERFACE
import matt.model.cls.ClassModifier.INNER
import matt.model.cls.ClassModifier.INTERFACE
import matt.model.cls.ClassModifier.OBJECT
import matt.model.cls.ClassModifier.OPEN
import matt.model.cls.ClassModifier.SEALED
import matt.model.cls.ClassModifier.VALUE
import kotlin.reflect.KClass


fun KClass<*>.modifiers() = filter<ClassModifier> {
  when (it) {
	SEALED        -> isSealed
	ABSTRACT      -> isAbstract && !isSealed
	OPEN          -> isOpen && !isAbstract && !isSealed
	OBJECT        -> objectInstance != null
	DATA          -> isData
	VALUE         -> isValue
	COMPANION     -> isCompanion
	INTERFACE     -> java.isInterface
	FUN_INTERFACE -> isFun
	INNER         -> isInner
	FINAL         -> isFinal
  }
}.also {
  require(it.isNotEmpty())
}

fun KClass<*>.requireIsOnly(vararg mods: ClassModifier) {
  require(modifiers().all { it in mods })
}

fun KClass<*>.requireIsNone(vararg mods: ClassModifier) {
  require(modifiers().none { it in mods })
}

fun KClass<*>.requireIsAll(vararg mods: ClassModifier) {
  require(mods.all { it in modifiers() })
}

fun KClass<*>.requireIsAny(vararg mods: ClassModifier) {
  require(mods.any { it in modifiers() })
}
