package matt.model.data.proxy.set

import matt.model.data.proxy.collect.ProxyCollection
import matt.model.op.convert.Converter


open class ProxySet<S, T>(
    private val innerSet: Set<S>,
    private val converter: Converter<S, T>
) : ProxyCollection<S, T>(innerSet, converter), Set<T> {
    override fun contains(element: T): Boolean {
        return innerSet.contains(converter.convertToA(element))
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all {
            innerSet.contains(converter.convertToA(it))
        }
    }

    override fun iterator() = object : Iterator<T> {
        private val iter = innerSet.iterator()
        override fun hasNext(): Boolean {
            return iter.hasNext()
        }

        override fun next(): T {
            return converter.convertToB(iter.next())
        }
    }
}

class ProxyMutableSet<S, T>(
    private val innerSet: MutableSet<S>,
    private val converter: Converter<S, T>
) : ProxySet<S, T>(innerSet, converter), MutableSet<T> {
    override fun add(element: T): Boolean {
        return innerSet.add(converter.convertToA(element))
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return innerSet.addAll(elements.map { converter.convertToA(it) })
    }

    override fun clear() {
        innerSet.clear()
    }

    override fun iterator() = object: MutableIterator<T> {
        private val itr = innerSet.iterator()
        override fun hasNext(): Boolean {
            return itr.hasNext()
        }

        override fun next(): T {
            return converter.convertToB(itr.next())
        }

        override fun remove() {
            itr.remove()
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return innerSet.retainAll(elements.map { converter.convertToA(it) })
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return innerSet.removeAll(elements.map { converter.convertToA(it) })
    }

    override fun remove(element: T): Boolean {
        return innerSet.remove(converter.convertToA(element))
    }

}
