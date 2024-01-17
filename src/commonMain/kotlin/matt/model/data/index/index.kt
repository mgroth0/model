package matt.model.data.index

import kotlinx.serialization.Serializable
import matt.lang.convert.BiConverter
import matt.model.data.mathable.IntWrapper
import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue

fun <T> MyIndexedValue<T, Index>.toKotlinIndexedValue() = IndexedValue(index = index.i, value = element)

infix fun <T> T.withIndex(i: Int) = MyIndexedValue(index = Index(i), element = this)
infix fun <T, I : AbstractIndex> T.withIndex(i: I): MyIndexedValue<T, I> =
    MyIndexedValue<T, I>(index = i, element = this)

class MyIndexedValue<T, I : AbstractIndex>(
    val element: T,
    val index: I
) {
    override fun toString(): String {
        return "MyIndexedValue[e=$element,i=$index]"
    }
}

sealed interface AbstractIndex
sealed interface AdditionIndex : AbstractIndex
sealed interface RemovalIndex : AbstractIndex
data object Start : AdditionIndex
data object End : AdditionIndex
data object First : RemovalIndex
data object All : RemovalIndex

val Int.asIndex get() = Index(this)

interface IndexLike {
    val redisArgument: Int
}

val IndexLike.pythonArgument get() = redisArgument /*they are the same*/

/*should be a value class*/
@Serializable
data class Index(val i: Int) : IntWrapper<Index>, RemovalIndex, AdditionIndex, IndexLike {

    companion object {
        val FIRST = Index(0)
    }

    override fun fromInt(d: Int): Index = Index(d)

    override val asInt: Int get() = i

    override val abs: Index get() = Index(i.absoluteValue)

    override val redisArgument get() = i
}


object IndexWrapperConverter : BiConverter<Index, Double> {
    override fun convertToB(a: Index): Double {
        return a.asInt.toDouble()
    }

    override fun convertToA(b: Double): Index {
        return Index(b.toInt())
    }

}

object IndexWrapperIntConverter : BiConverter<Index, Int> {
    override fun convertToB(a: Index): Int {
        return a.asInt
    }

    override fun convertToA(b: Int): Index {
        return Index(b)
    }

}


@JvmInline
value class ReverseIndex(private val i: Int) : IndexLike {
    companion object {
        val LAST = ReverseIndex(-1)
        fun nthLast(n: Int): ReverseIndex {
            require(n > 0) {
                "n here should be 1 if you mean last, 2 if you mean second to last, etc"
            }
            return ReverseIndex(-n)
        }
    }

    init {
        require(i < 0) {
            "To comply with other languages like python, -1 should mean last (and -2 means second to last, and so on)"
        }
    }

    override val redisArgument get() = i
}