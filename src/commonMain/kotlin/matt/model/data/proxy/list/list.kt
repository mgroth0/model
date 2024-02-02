package matt.model.data.proxy.list

import matt.lang.anno.Open
import matt.lang.convert.BiConverter
import matt.model.data.proxy.collect.ProxyCollection

fun <S, T> List<S>.proxy(converter: BiConverter<S, T>) = ImmutableProxyList(this, converter)
fun <S, T> MutableList<S>.proxy(converter: BiConverter<S, T>) = ProxyList(this, converter)

open class ImmutableProxyList<S, T>(
    private val innerList: List<S>,
    private val converter: BiConverter<S, T>
) : ProxyCollection<S, T>(innerList, converter), List<T> {

    protected fun S.toT() = converter.convertToB(this)
    protected fun T.toS() = converter.convertToA(this)


    final override fun get(index: Int): T = innerList[index].toT()


    @Open
    override fun iterator() = listIterator()

    @Open
    override fun listIterator() = listIterator(0)

    @Open
    override fun listIterator(index: Int) = object : ListIterator<T> {

        private val itr = innerList.listIterator(index)


        override fun hasNext(): Boolean = itr.hasNext()

        override fun hasPrevious(): Boolean = itr.hasPrevious()

        override fun next(): T = itr.next().toT()

        override fun nextIndex(): Int = itr.nextIndex()

        override fun previous(): T = itr.previous().toT()

        override fun previousIndex(): Int = itr.previousIndex()


    }
    @Open
    override fun subList(
        fromIndex: Int,
        toIndex: Int
    ): MutableList<T> {
        TODO()
    }

    final override fun lastIndexOf(element: T): Int = innerList.lastIndexOf(element.toS())

    final override fun indexOf(element: T): Int = innerList.indexOf(element.toS())

    final override fun containsAll(elements: Collection<T>): Boolean = innerList.containsAll(elements.map { it.toS() })

    final override fun contains(element: T): Boolean = innerList.contains(element.toS())

}

class ProxyList<S, T>(
    private val innerList: MutableList<S>,
    converter: BiConverter<S, T>
) : ImmutableProxyList<S, T>(innerList, converter), MutableList<T> {


    override fun clear() {
        innerList.clear()
    }

    override fun addAll(elements: Collection<T>): Boolean = innerList.addAll(elements.map { it.toS() })

    override fun addAll(
        index: Int,
        elements: Collection<T>
    ): Boolean = innerList.addAll(index, elements.map { it.toS() })

    override fun add(
        index: Int,
        element: T
    ) = innerList.add(index, element.toS())

    override fun add(element: T): Boolean = innerList.add(element.toS())

    override fun iterator() = listIterator()

    override fun listIterator() = listIterator(0)

    override fun listIterator(index: Int) = object : MutableListIterator<T> {

        private val itr = innerList.listIterator(index)

        override fun add(element: T) {
            itr.add(element.toS())
        }

        override fun hasNext(): Boolean = itr.hasNext()

        override fun hasPrevious(): Boolean = itr.hasPrevious()

        override fun next(): T = itr.next().toT()

        override fun nextIndex(): Int = itr.nextIndex()

        override fun previous(): T = itr.previous().toT()

        override fun previousIndex(): Int = itr.previousIndex()

        override fun remove() = itr.remove()

        override fun set(element: T) = itr.set(element.toS())

    }

    override fun removeAt(index: Int): T = innerList.removeAt(index).toT()

    override fun subList(
        fromIndex: Int,
        toIndex: Int
    ): MutableList<T> {
        TODO()
    }

    override fun set(
        index: Int,
        element: T
    ): T = innerList.set(index, element.toS()).toT()

    override fun retainAll(elements: Collection<T>): Boolean = innerList.retainAll(elements.map { it.toS() })

    override fun removeAll(elements: Collection<T>): Boolean = innerList.removeAll(elements.map { it.toS() })

    override fun remove(element: T): Boolean = innerList.remove(element.toS())

}
