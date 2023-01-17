package matt.model.data.index

import kotlinx.serialization.Serializable
import matt.model.data.mathable.IntWrapper
import matt.model.op.convert.Converter
import kotlin.math.absoluteValue

fun <T> MyIndexedValue<T, Index>.toKotlinIndexedValue() = IndexedValue(index = index.i, value = element)

infix fun <T> T.withIndex(i: Int) = MyIndexedValue(index = Index(i), element = this)
infix fun <T, I: AbstractIndex> T.withIndex(i: I): MyIndexedValue<T, I> =
  MyIndexedValue<T, I>(index = i, element = this)

class MyIndexedValue<T, I: AbstractIndex>(val element: T, val index: I) {
  override fun toString(): String {
	return "MyIndexedValue[e=$element,i=$index]"
  }
}

sealed interface AbstractIndex
sealed interface AdditionIndex: AbstractIndex
sealed interface RemovalIndex: AbstractIndex
object Start: AdditionIndex
object End: AdditionIndex
object First: RemovalIndex
object All: RemovalIndex

/*should be a value class*/
@Serializable data class Index(val i: Int): IntWrapper<Index>, RemovalIndex, AdditionIndex {
  override fun fromInt(d: Int): Index {
	return Index(i)
  }

  override val asInt: Int
	get() = i

  override val abs: Index
	get() = Index(i.absoluteValue)
}

object IndexWrapperConverter: Converter<Index, Double> {
  override fun convertToB(a: Index): Double {
	return a.asInt.toDouble()
  }

  override fun convertToA(b: Double): Index {
	return Index(b.toInt())
  }

}

object IndexWrapperIntConverter: Converter<Index, Int> {
  override fun convertToB(a: Index): Int {
	return a.asInt
  }

  override fun convertToA(b: Int): Index {
	return Index(b)
  }

}