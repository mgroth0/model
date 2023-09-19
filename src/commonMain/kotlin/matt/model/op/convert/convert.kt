package matt.model.op.convert

import matt.lang.convert.BiConverter
import matt.model.data.byte.ByteSize
import matt.prim.byte.toInt
import matt.prim.converters.StringConverter
import matt.prim.int.toByteArray
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class NotAConverter<T> : BiConverter<T, T> {
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



fun <A, B : Any> BiConverter<A, B>.asFailable() = object : FailableConverter<A, B> {
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





interface BytesConverter<T> : BiConverter<ByteArray, T> {
    fun toBytes(t: T): ByteArray
    fun fromBytes(s: ByteArray): T
    override fun convertToA(b: T): ByteArray = toBytes(b)
    override fun convertToB(a: ByteArray) = fromBytes(a)
}

fun <X, Y, Z> BiConverter<X, Y>.then(converter: BiConverter<Y, Z>) = object : BiConverter<X, Z> {
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




object StringStringConverter : StringConverter<String> {
    override fun toString(t: String) = t
    override fun fromString(s: String) = s
}

object BooleanStringConverter : StringConverter<Boolean> {
    override fun toString(t: Boolean) = t.toString()
    override fun fromString(s: String) = s.toBooleanStrict()
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


object LongMillisConverter : BiConverter<Long, Duration> {
    override fun convertToB(a: Long): Duration {
        return a.milliseconds
    }

    override fun convertToA(b: Duration): Long {
        return b.inWholeMilliseconds
    }
}

object DoubleLongConverter : BiConverter<Double, Long> {
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




