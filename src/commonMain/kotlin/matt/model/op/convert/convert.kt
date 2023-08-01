package matt.model.op.convert

import matt.model.data.byte.ByteSize
import matt.prim.byte.toInt
import matt.prim.int.toByteArray
import matt.prim.str.elementsToString
import matt.prim.str.takeIfNotBlank
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class NotAConverter<T> : Converter<T, T> {
    override fun convertToB(a: T): T {
        return a
    }

    override fun convertToA(b: T): T {
        return b
    }
}
typealias IdentityConverter<T> = NotAConverter<T>

fun <T> toStringConverter(op: (T) -> String) = object : StringConverter<T> {
    override fun toString(t: T): String {
        return op(t)
    }

    override fun fromString(s: String): T {
        TODO("Not yet implemented")
    }

}

fun <T> fromStringConverter(op: (String) -> T) = object : StringConverter<T> {
    override fun toString(t: T): String {
        TODO("Not yet implemented")
    }

    override fun fromString(s: String): T {
        return op(s)
    }

}


object NullToBlankStringConverter : StringConverter<String?> {
    override fun toString(t: String?): String {
        return t ?: ""
    }

    override fun fromString(s: String): String {
        return s
    }

}

interface Converter<A, B> {

    companion object {
        /*TODO: should be WeakMap*/
        val invertedConverters = mutableMapOf<Converter<*, *>, Converter<*, *>>()
    }

    fun convertToB(a: A): B
    fun A.toB() = convertToB(this)
    fun convertToA(b: B): A
    fun B.toA() = convertToA(this)
    fun invert(): Converter<B, A> {
        val i = invertedConverters[this]
        @Suppress("UNCHECKED_CAST")
        if (i != null) return i as Converter<B, A>
        val outer = this
        val inv = object : Converter<B, A> {

            override fun convertToB(a: B): A {
                return outer.convertToA(a)
            }

            override fun convertToA(b: A): B {
                return outer.convertToB(b)
            }

        }
        invertedConverters[this] = inv
        return inv
    }

    fun nullable(): Converter<A?, B?> = object : Converter<A?, B?> {
        override fun convertToB(a: A?): B? {
            if (a == null) return null
            return this@Converter.convertToB(a)
        }

        override fun convertToA(b: B?): A? {
            if (b == null) return null
            return this@Converter.convertToA(b)
        }


    }

    /*cannot have invoke both ways. Its some sort of jvm clash*/
    operator fun invoke(a: A) = a.toB()
}


fun <A, B : Any> Converter<A, B>.asFailable() = object : FailableConverter<A, B> {
    override fun convertToB(a: A): B {
        return this@asFailable.convertToB(a)
    }

    override fun convertToA(b: B): A {
        return this@asFailable.convertToA(b)
    }

}

interface FailableConverter<A, B : Any> {
    fun convertToB(a: A): B?
    fun A.toB() = convertToB(this)
    fun convertToA(b: B): A
    fun B.toA() = convertToA(this)
}

interface StringConverter<T> : Converter<String, T> {
    fun toString(t: T): String
    fun fromString(s: String): T
    override fun convertToA(b: T): String = toString(b)
    override fun convertToB(a: String) = fromString(a)

    fun asSingularStringListConverter() = StringListConverter.fromStringConverterAsSingular(this)

    fun nullAsBlank() = object : Converter<String, T?> {
        override fun convertToB(a: String): T? {
            return a.takeIfNotBlank()?.toB()
        }

        override fun convertToA(b: T?): String {
            return b?.toA() ?: ""
        }


    }

}



typealias StringList = List<String>


interface StringListConverter<T> : Converter<StringList, T> {
    companion object {
        fun <T> fromStringConverterAsList(
            stringConverter: StringConverter<T>
        ) = StringListByElementConverter(stringConverter)

        fun <T> fromStringConverterAsSingular(
            stringConverter: StringConverter<T>
        ) = StringListBySingleElementConverter(stringConverter)

        fun <T> fromStringConverterAsSingularOrEmpty(
            stringConverter: StringConverter<T>
        ) = StringListBySingleElementOrNullConverter(stringConverter)
    }

    fun toStringList(t: T): StringList
    fun fromStringList(s: StringList): T
    override fun convertToA(b: T): StringList = toStringList(b)
    override fun convertToB(a: StringList) = fromStringList(a)

    fun emptyIsNull(): StringListConverter<T?> = object : StringListConverter<T?> {
        override fun toStringList(t: T?): StringList {
            return if (t == null) emptyList() else this@StringListConverter.toStringList(t)
        }

        override fun fromStringList(s: StringList): T? {
            return if (s.isEmpty()) null else this@StringListConverter.fromStringList(s)
        }
    }
}

class StringListByElementConverter<T>(private val elementConverter: StringConverter<T>) : StringListConverter<List<T>> {
    override fun toStringList(t: List<T>): StringList {
        return t.map { elementConverter.toString(it) }
    }

    override fun fromStringList(s: StringList): List<T> {
        return s.map { elementConverter.fromString(it) }
    }
}

class StringListBySingleElementConverter<T>(private val elementConverter: StringConverter<T>) : StringListConverter<T> {
    override fun toStringList(t: T): StringList {
        return listOf(elementConverter.toString(t))
    }

    override fun fromStringList(s: StringList): T {
        return elementConverter.fromString(
            s.singleOrNull() ?: error("only 1 element allowed, but got ${s.size}: ${s.elementsToString()}")
        )
    }

}

class StringListBySingleElementOrNullConverter<T>(private val elementConverter: StringConverter<T>) :
    StringListConverter<T?> {
    override fun toStringList(t: T?): StringList {
        if (t == null) return emptyList()
        return listOf(elementConverter.toString(t))
    }

    override fun fromStringList(s: StringList): T? {
        if (s.isEmpty()) return null
        return elementConverter.fromString(
            s.singleOrNull() ?: error("only 0-1 elements allowed, but got ${s.size}: ${s.elementsToString()}")
        )
    }

}


object StringListStringListConverter : StringListConverter<StringList> {
    override fun toStringList(t: StringList) = t
    override fun fromStringList(s: StringList) = s
}

interface BytesConverter<T> : Converter<ByteArray, T> {
    fun toBytes(t: T): ByteArray
    fun fromBytes(s: ByteArray): T
    override fun convertToA(b: T): ByteArray = toBytes(b)
    override fun convertToB(a: ByteArray) = fromBytes(a)
}

fun <X, Y, Z> Converter<X, Y>.then(converter: Converter<Y, Z>) = object : Converter<X, Z> {
    override fun convertToB(a: X): Z {
        return converter.convertToB(this@then.convertToB(a))

    }

    override fun convertToA(b: Z): X {
        return this@then.convertToA(converter.convertToA(b))
    }
}


//fun <X, Y, Z> Converter<X, Y?>.thenIfNotNull(converter: Converter<Y, Z>) = object : Converter<X, Z?> {
//    override fun convertToB(a: X): Z {
//        return converter.convertToB(this@then.convertToB(a))
//
//    }
//
//    override fun convertToA(b: Z): X {
//        return this@then.convertToA(converter.convertToA(b))
//    }
//}


interface FailableStringConverter<T : Any> : FailableConverter<String, T> {
    fun toString(t: T): String
    fun fromString(s: String): T?
    override fun convertToA(b: T): String = toString(b)
    override fun convertToB(a: String) = fromString(a)
}


object ByteArrayStringConverter : StringConverter<ByteArray> {
    override fun toString(t: ByteArray) = t.decodeToString()
    override fun fromString(s: String) = s.encodeToByteArray()
}


object StringStringConverter : StringConverter<String> {
    override fun toString(t: String) = t
    override fun fromString(s: String) = s
}

object BooleanStringConverter : StringConverter<Boolean> {
    override fun toString(t: Boolean) = t.toString()
    override fun fromString(s: String) = s.toBoolean()
}

object IntStringConverter : FailableStringConverter<Int> {
    override fun toString(t: Int) = t.toString()
    override fun fromString(s: String) = s.toIntOrNull()
}

object DefiniteIntStringConverter : StringConverter<Int> {
    override fun toString(t: Int) = t.toString()
    override fun fromString(s: String) = s.toInt()
}

object IntBytesConverter : BytesConverter<Int> {
    override fun toBytes(t: Int): ByteArray {
        return t.toByteArray()
    }

    override fun fromBytes(s: ByteArray): Int {
        return s.toInt()
    }

}

object MyNumberStringConverter : StringConverter<Number> {
    override fun toString(t: Number): String {
        return t.toString()
    }

    override fun fromString(s: String): Number {
        return s.toDouble()
    }
}


object LongMillisConverter : Converter<Long, Duration> {
    override fun convertToB(a: Long): Duration {
        return a.milliseconds
    }

    override fun convertToA(b: Duration): Long {
        return b.inWholeMilliseconds
    }
}

object DoubleLongConverter : Converter<Double, Long> {
    override fun convertToB(a: Double): Long {
        return a.toLong()
    }

    override fun convertToA(b: Long): Double {
        return b.toDouble()
    }

}


val BYTE_SIZE_FORMATTER = object : StringConverter<Number> {
    override fun toString(t: Number): String {
        return ByteSize(t.toLong()).toString()
    }

    override fun fromString(s: String) = TODO()
}


val RATIO_TO_PERCENT_FORMATTER_NO_DECIMAL = object : StringConverter<Number> {
    override fun toString(t: Number): String {
        return (t.toDouble() * 100).toInt().toString() + "%"
    }

    override fun fromString(s: String) = TODO()
}




