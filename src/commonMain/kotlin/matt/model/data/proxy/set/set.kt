package matt.model.data.proxy.set

import matt.lang.anno.JetBrainsYouTrackProject.KT
import matt.lang.anno.Open
import matt.lang.anno.YouTrackIssue
import matt.lang.convert.BiConverter
import matt.model.data.proxy.collect.ProxyCollection

open class ProxySet<S, T>(
    private val innerSet: Set<S>,
    private val converter: BiConverter<S, T>
) : ProxyCollection<S, T>(innerSet, converter), Set<T> {
    final override fun contains(element: T): Boolean = innerSet.contains(converter.convertToA(element))

    final override fun containsAll(elements: Collection<T>): Boolean =
        elements.all {
            innerSet.contains(converter.convertToA(it))
        }

    @Open
    override fun iterator() =
        object : Iterator<T> {
            private val iter = innerSet.iterator()

            override fun hasNext(): Boolean = iter.hasNext()

            override fun next(): T = converter.convertToB(iter.next())
        }
}


@YouTrackIssue(KT, 65555)
object ToUnCommentInK2Beta5
/*

class ProxyMutableSet<S, T>(
    private val innerSet: MutableSet<S>,
    private val converter: BiConverter<S, T>
) : ProxySet<S, T>(innerSet, converter), MutableSet<T> {
    override fun add(element: T): Boolean = innerSet.add(converter.convertToA(element))

    override fun addAll(elements: Collection<T>): Boolean = innerSet.addAll(elements.map { converter.convertToA(it) })

    override fun clear() {
        innerSet.clear()
    }

    override fun iterator() =
        object : MutableIterator<T> {
            private val itr = innerSet.iterator()

            override fun hasNext(): Boolean = itr.hasNext()

            override fun next(): T = converter.convertToB(itr.next())

            override fun remove() {
                itr.remove()
            }
        }

    override fun retainAll(elements: Collection<T>): Boolean = innerSet.retainAll(elements.map { converter.convertToA(it) })

    override fun removeAll(elements: Collection<T>): Boolean = innerSet.removeAll(elements.map { converter.convertToA(it) })

    override fun remove(element: T): Boolean = innerSet.remove(converter.convertToA(element))
}
*/
