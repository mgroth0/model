package matt.model.data.proxy.map

import matt.lang.err
import matt.model.op.convert.Converter
import kotlin.collections.Map.Entry
import kotlin.collections.MutableMap.MutableEntry

fun <SK : Any, SV : Any, TK : Any, TV : Any> Map<SK, SV>.proxy(
    keyConverter: Converter<SK, TK>,
    valueConverter: Converter<SV, TV>
) = ImmutableProxyMap(
    this,
    keyConverter,
    valueConverter
)

fun <SK : Any, SV : Any, TK : Any, TV : Any> MutableMap<SK, SV>.proxy(
    keyConverter: Converter<SK, TK>,
    valueConverter: Converter<SV, TV>
) = ProxyMap(
    this,
    keyConverter,
    valueConverter
)

fun <K, V> Entry<K, V>.toFakeMutableEntry() = FakeMutableEntry(this)
class FakeMutableEntry<K, V>(entry: Entry<K, V>) : MutableEntry<K, V> {
    override val key = entry.key
    override val value = entry.value

    override fun setValue(newValue: V): V {
        error("FakeMutableEntry is FAKE")
    }
}

open class ImmutableProxyMap<SK : Any, SV : Any, TK : Any, TV : Any>(
    private val innerMap: Map<SK, SV>,
    private val keyConverter: Converter<SK, TK>,
    private val valueConverter: Converter<SV, TV>
) : Map<TK, TV> {
    protected fun SK.toTK() = keyConverter.convertToB(this)
    protected fun SV.toTV() = valueConverter.convertToB(this)
    protected fun TK.toSK() = keyConverter.convertToA(this)
    protected fun TV.toSV() = valueConverter.convertToA(this)


    override val entries: Set<Entry<TK, TV>>
        get() = run {
            val theSet = innerMap.entries.map { e ->
                object : Entry<TK, TV> {
                    override val key = e.key.toTK()
                    override val value = e.value.toTV()
                }
            }.toSet()

            theSet

//            object : Set<Entry<TK, TV>> {
//
//                override fun containsAll(elements: Collection<MutableEntry<TK, TV>>): Boolean {
//                    TODO("Not yet implemented")
//                }
//
//                override fun contains(element: MutableEntry<TK, TV>): Boolean {
//                    TODO("Not yet implemented")
//                }
//
//                override fun iterator(): MutableIterator<MutableEntry<TK, TV>> {
//                    val itr = theSet.iterator()
//                    return object : MutableIterator<MutableEntry<TK, TV>> {
//                        override fun hasNext(): Boolean {
//                            return itr.hasNext()
//                        }
//
//                        override fun next(): MutableEntry<TK, TV> {
//                            return itr.next()
//                        }
//
//                        override fun remove() {
//                            TODO("Not yet implemented")
//                        }
//
//                    }
//                }
//
//
//            }
        }


    override val keys: Set<TK>
        get() = TODO("Not yet implemented")
    override val size: Int
        get() = innerMap.size
    override val values: Collection<TV>
        get() = TODO("Not yet implemented")

    override fun isEmpty() = innerMap.isEmpty()


    override fun get(key: TK): TV? {
        return innerMap.get(key.toSK())?.toTV()
    }

    override fun containsValue(value: TV): Boolean {
        return innerMap.containsValue(value.toSV())
    }

    override fun containsKey(key: TK): Boolean {
        return innerMap.containsKey(key.toSK())
    }


}

open class FakeMutableIterator<E>(val itr: Iterator<E>) : Iterator<E> by itr, MutableIterator<E> {

    init {
        println("WARNING: there are two FakeMutableIterator classes. UGH!")
    }

    override fun remove() {
        err("tried remove in ${FakeMutableIterator::class.simpleName}")
    }

}


fun <E> Set<E>.toFakeMutableSet() = FakeMutableSet(this)

class FakeMutableSet<E>(val set: Collection<E>) : MutableSet<E> {


    init {
        println("WARNING: there are two FakeMutableSet classes. UGH!")
    }

    override fun add(element: E): Boolean {
        err("tried to add in ${FakeMutableSet::class.simpleName}")
    }

    override fun addAll(elements: Collection<E>): Boolean {
        err("tried to addAll in ${FakeMutableSet::class.simpleName}")
    }

    override fun clear() {
        err("tried to clear in ${FakeMutableSet::class.simpleName}")
    }

    override fun iterator(): MutableIterator<E> {
        return FakeMutableIterator(set.iterator())
    }

    override fun remove(element: E): Boolean {
        err("tried to remove in ${FakeMutableSet::class.simpleName}")
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        err("tried to removeAll in ${FakeMutableSet::class.simpleName}")
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        err("tried to retainAll in ${FakeMutableSet::class.simpleName}")
    }

    override val size: Int
        get() = set.size

    override fun contains(element: E): Boolean {
        return set.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return set.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return set.isEmpty()
    }

}

class ProxyMap<SK : Any, SV : Any, TK : Any, TV : Any>(
    private val innerMap: MutableMap<SK, SV>,
    private val keyConverter: Converter<SK, TK>,
    private val valueConverter: Converter<SV, TV>
) : ImmutableProxyMap<SK, SV, TK, TV>(
    innerMap,
    keyConverter,
    valueConverter
), MutableMap<TK, TV> {

    init {
        println("TODO: figure out where fake mutable set and fake mutable entry should be")
    }


    override val entries: MutableSet<MutableEntry<TK, TV>>
        get() = super.entries.map { it.toFakeMutableEntry() }.toSet().toFakeMutableSet()

    override val keys: MutableSet<TK>
        get() = TODO("Not yet implemented")

    override val values: MutableCollection<TV>
        get() = TODO("Not yet implemented")

    override fun clear() = innerMap.clear()


    override fun remove(key: TK) = innerMap.remove(key.toSK())?.toTV()

    override fun putAll(from: Map<out TK, TV>) {
        innerMap.putAll(from.mapKeys { it.key.toSK() }.mapValues { it.value.toSV() })
    }

    override fun put(
        key: TK,
        value: TV
    ): TV? {
        return innerMap.put(
            key.toSK(),
            value.toSV()
        )?.toTV()
    }


}




