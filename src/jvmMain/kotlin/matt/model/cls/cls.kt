@file:JvmName("ClsJvmKt")

package matt.model.cls

import matt.lang.enums.filter
import matt.model.cls.ClassModifier.ABSTRACT_NOT_SEALED
import matt.model.cls.ClassModifier.COMPANION
import matt.model.cls.ClassModifier.DATA
import matt.model.cls.ClassModifier.FINAL_NOT_OBJECT
import matt.model.cls.ClassModifier.FUN_INTERFACE
import matt.model.cls.ClassModifier.INNER
import matt.model.cls.ClassModifier.INTERFACE
import matt.model.cls.ClassModifier.OBJECT
import matt.model.cls.ClassModifier.OPEN_NOT_ABSTRACT_NOT_SEALED
import matt.model.cls.ClassModifier.SEALED
import matt.model.cls.ClassModifier.VALUE
import kotlin.reflect.KClass


fun KClass<*>.modifiers() = filter<ClassModifier> {
  when (it) {
	SEALED                       -> isSealed
	ABSTRACT_NOT_SEALED          -> isAbstract && !isSealed
	OPEN_NOT_ABSTRACT_NOT_SEALED -> isOpen && !isAbstract && !isSealed
	OBJECT                       -> objectInstance != null
	DATA                         -> isData
	VALUE                        -> isValue
	COMPANION                    -> isCompanion
	INTERFACE                    -> java.isInterface
	FUN_INTERFACE                -> isFun
	INNER                        -> isInner
	FINAL_NOT_OBJECT             -> isFinal && objectInstance == null
  }
}.also {
  require(it.isNotEmpty())
}

fun KClass<*>.requireIsOnly(vararg mods: ClassModifier) {
  require(modifiers().all { it in mods }) {
	val fail = modifiers().first { it !in mods }
	"${this@requireIsOnly} is $fail, which is not in [${mods.joinToString()}]"
  }
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
