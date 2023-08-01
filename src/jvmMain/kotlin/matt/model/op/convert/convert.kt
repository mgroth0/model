@file:JvmName("ConvertJvmKt")
/*if I use classes only, don't need JvmName?*/
package matt.model.op.convert

import matt.prim.base64.decodeFromBase64
import matt.prim.base64.decodeFromURLBase64
import matt.prim.base64.encodeToBase64
import matt.prim.base64.encodeToURLBase64
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible


@Suppress("unused")
val RATIO_TO_PERCENT_FORMATTER = object : StringConverter<Number> {
    override fun toString(t: Number): String {
        return "%.3f".format(t.toDouble() * 100) + "%"
    }

    override fun fromString(s: String) = TODO()
}


object Base64StringConverter : StringConverter<ByteArray> {
    override fun toString(t: ByteArray) = t.encodeToBase64()
    override fun fromString(s: String) = s.decodeFromBase64()
}

object URLBase64StringConverter : StringConverter<ByteArray> {
    override fun toString(t: ByteArray) = t.encodeToURLBase64()
    override fun fromString(s: String) = s.decodeFromURLBase64()
}


class StringValueObjectConverter<T : Any>(private val cls: KClass<T>) : StringConverter<T> {
    init {
        require(cls.isValue)
    }

    private val constructor by lazy {
        cls.primaryConstructor!!
    }
    private val theProp by lazy {
        val constructorParam = constructor.valueParameters.single()
        cls.declaredMemberProperties.first { it.name == constructorParam.name }.also {
            it.isAccessible = true
        }
    }

    override fun toString(t: T): String {
        return theProp.getter.call(t) as String
    }

    override fun fromString(s: String): T {
        return constructor.call(s)
    }

}