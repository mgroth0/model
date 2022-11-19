@file:JvmName("ClsJvmKt")

package matt.model.code.cls

import matt.model.code.cls.AbstractInnerClass
import matt.model.code.cls.AbstractOuterClass
import matt.model.code.cls.DataClass
import matt.model.code.cls.FinalInnerClass
import matt.model.code.cls.FinalOuterClass
import matt.model.code.cls.FunInterface
import matt.model.code.cls.ObjectType
import matt.model.code.cls.OpenInnerClass
import matt.model.code.cls.OpenOuterClass
import matt.model.code.cls.RegularInterface
import matt.model.code.cls.SealedInterface
import matt.model.code.cls.SealedOuterClass
import matt.model.code.cls.ValueClass
import kotlin.reflect.KClass


/*13*/
fun KClass<*>.typeType() = when {
  objectInstance != null -> ObjectType
  isFun                  -> FunInterface
  java.isInterface       -> when {
	isSealed -> SealedInterface
	else     -> RegularInterface
  }

  isData                 -> DataClass
  isValue                -> ValueClass
  isInner                -> when {
	isAbstract -> AbstractInnerClass
	isOpen     -> OpenInnerClass
	else       -> FinalInnerClass
  }

  isSealed               -> SealedOuterClass
  isAbstract             -> AbstractOuterClass
  isOpen                 -> OpenOuterClass
  else                   -> FinalOuterClass
}

//fun KClass<*>.requireIs(typeType: TypeType) {
//  require(modifiers().all { it in mods }) {
//	val fail = modifiers().first { it !in mods }
//	"${this@requireIsOnly} is $fail, which is not in [${mods.joinToString()}]"
//  }
//}
//
//fun KClass<*>.requireIsNone(vararg mods: ClassType) {
//  require(modifiers().none { it in mods })
//}
//
//fun KClass<*>.requireIsAll(vararg mods: ClassType) {
//  require(mods.all { it in modifiers() })
//}
//
//fun KClass<*>.requireIsAny(vararg mods: ClassType) {
//  require(mods.any { it in modifiers() })
//}
