package matt.model.data.proxy.list

import matt.model.op.convert.Converter

fun <S, T> List<S>.proxy(converter: Converter<S, T>) = ImmutableProxyList(this, converter)
fun <S, T> MutableList<S>.proxy(converter: Converter<S, T>) = ProxyList(this, converter)

open class ImmutableProxyList<S, T>(
    private val innerList: List<S>,
    private val converter: Converter<S, T>
) : List<T> {

    protected fun S.toT() = converter.convertToB(this)
    protected fun T.toS() = converter.convertToA(this)

    final override val size: Int
        get() = innerList.size

    final override fun get(index: Int): T {
        return innerList[index].toT()
    }

    final override fun isEmpty(): Boolean {
        return innerList.isEmpty()
    }

    override fun iterator() = listIterator()

    override fun listIterator() = listIterator(0)

    override fun listIterator(index: Int) = object : ListIterator<T> {

        private val itr = innerList.listIterator(index)


        override fun hasNext(): Boolean {
            return itr.hasNext()
        }

        override fun hasPrevious(): Boolean {
            return itr.hasPrevious()
        }

        override fun next(): T {
            return itr.next().toT()
        }

        override fun nextIndex(): Int {
            return itr.nextIndex()
        }

        override fun previous(): T {
            return itr.previous().toT()
        }

        override fun previousIndex(): Int {
            return itr.previousIndex()
        }


    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO()
    }

    final override fun lastIndexOf(element: T): Int {
        return innerList.lastIndexOf(element.toS())
    }

    final override fun indexOf(element: T): Int {
        return innerList.indexOf(element.toS())
    }

    final override fun containsAll(elements: Collection<T>): Boolean {
        return innerList.containsAll(elements.map { it.toS() })
    }

    final override fun contains(element: T): Boolean {
        return innerList.contains(element.toS())
    }

}

class ProxyList<S, T>(
    private val innerList: MutableList<S>,
    converter: Converter<S, T>
) : ImmutableProxyList<S, T>(innerList, converter), MutableList<T> {


    override fun clear() {
        innerList.clear()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return innerList.addAll(elements.map { it.toS() })
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return innerList.addAll(index, elements.map { it.toS() })
    }

    override fun add(index: Int, element: T) {
        return innerList.add(index, element.toS())
    }

    override fun add(element: T): Boolean {
        return innerList.add(element.toS())
    }

    override fun iterator() = listIterator()

    override fun listIterator() = listIterator(0)

    override fun listIterator(index: Int) = object : MutableListIterator<T> {

        private val itr = innerList.listIterator(index)

        override fun add(element: T) {
            itr.add(element.toS())
        }

        override fun hasNext(): Boolean {
            return itr.hasNext()
        }

        override fun hasPrevious(): Boolean {
            return itr.hasPrevious()
        }

        override fun next(): T {
            return itr.next().toT()
        }

        override fun nextIndex(): Int {
            return itr.nextIndex()
        }

        override fun previous(): T {
            return itr.previous().toT()
        }

        override fun previousIndex(): Int {
            return itr.previousIndex()
        }

        override fun remove() {
            return itr.remove()
        }

        override fun set(element: T) {
            return itr.set(element.toS())
        }

    }

    override fun removeAt(index: Int): T {
        return innerList.removeAt(index).toT()
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO()
    }

    override fun set(index: Int, element: T): T {
        return innerList.set(index, element.toS()).toT()
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return innerList.retainAll(elements.map { it.toS() })
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return innerList.removeAll(elements.map { it.toS() })
    }

    override fun remove(element: T): Boolean {
        return innerList.remove(element.toS())
    }

}