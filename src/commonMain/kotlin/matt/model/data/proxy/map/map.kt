package matt.model.data.proxy.map

import matt.lang.anno.Open
import matt.lang.convert.BiConverter
import matt.model.data.proxy.set.ProxyMutableSet
import kotlin.collections.Map.Entry
import kotlin.collections.MutableMap.MutableEntry

fun <SK : Any, SV : Any, TK : Any, TV : Any> Map<SK, SV>.proxy(
    keyConverter: BiConverter<SK, TK>,
    valueConverter: BiConverter<SV, TV>
) = ImmutableProxyMap(
    this,
    keyConverter,
    valueConverter
)

fun <SK : Any, SV : Any, TK : Any, TV : Any> MutableMap<SK, SV>.proxy(
    keyConverter: BiConverter<SK, TK>,
    valueConverter: BiConverter<SV, TV>
) = ProxyMap(
    this,
    keyConverter,
    valueConverter
)

class EntryImpl<K, V>(
    override val key: K,
    override val value: V
) : Entry<K, V>

fun <K, V> Entry<K, V>.toFakeMutableEntry() = FakeMutableEntry(this)
class FakeMutableEntry<K, V>(entry: Entry<K, V>) : MutableEntry<K, V> {
    constructor(
        key: K,
        value: V
    ) : this(EntryImpl(key, value))

    override val key = entry.key
    override val value = entry.value

    override fun setValue(newValue: V): V {
        error("FakeMutableEntry is FAKE")
    }
}

open class ImmutableProxyMap<SK : Any, SV : Any, TK : Any, TV : Any>(
    private val innerMap: Map<SK, SV>,
    private val keyConverter: BiConverter<SK, TK>,
    private val valueConverter: BiConverter<SV, TV>
) : Map<TK, TV> {
    protected fun SK.toTK() = keyConverter.convertToB(this)
    protected fun SV.toTV() = valueConverter.convertToB(this)
    protected fun TK.toSK() = keyConverter.convertToA(this)
    protected fun TV.toSV() = valueConverter.convertToA(this)

    @Open
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
//                    TODO()
//                }
//
//                override fun contains(element: MutableEntry<TK, TV>): Boolean {
//                    TODO()
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
//                            TODO()
//                        }
//
//                    }
//                }
//
//
//            }
        }


    @Open
    override val keys: Set<TK>
        get() = TODO()

    final override val size: Int
        get() = innerMap.size

    @Open
    override val values: Collection<TV>
        get() = TODO()

    final override fun isEmpty() = innerMap.isEmpty()


    final override fun get(key: TK): TV? {
        return innerMap.get(key.toSK())?.toTV()
    }

    final override fun containsValue(value: TV): Boolean {
        return innerMap.containsValue(value.toSV())
    }

    final override fun containsKey(key: TK): Boolean {
        return innerMap.containsKey(key.toSK())
    }


}


class ProxyMap<SK : Any, SV : Any, TK : Any, TV : Any>(
    private val innerMap: MutableMap<SK, SV>,
    private val keyConverter: BiConverter<SK, TK>,
    private val valueConverter: BiConverter<SV, TV>
) : ImmutableProxyMap<SK, SV, TK, TV>(
    innerMap,
    keyConverter,
    valueConverter
), MutableMap<TK, TV> {

    override val entries: MutableSet<MutableEntry<TK, TV>>
        get() = ProxyMutableSet(
            innerMap.entries,
            object : BiConverter<MutableEntry<SK, SV>, MutableEntry<TK, TV>> {

                override fun convertToB(a: MutableEntry<SK, SV>): MutableEntry<TK, TV> {
                    return FakeMutableEntry(
                        EntryImpl(
                            keyConverter.convertToB(a.key),
                            valueConverter.convertToB(a.value)
                        )
                    )
                }

                override fun convertToA(b: MutableEntry<TK, TV>): MutableEntry<SK, SV> {
                    return FakeMutableEntry(
                        EntryImpl(
                            keyConverter.convertToA(b.key),
                            valueConverter.convertToA(b.value)
                        )
                    )
                }

            }
        )

    /*.map { it.toFakeMutableEntry() }.toSet().toFakeMutableSet()*/

    override val keys: MutableSet<TK>
        get() = TODO()

    override val values: MutableCollection<TV>
        get() = TODO()

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




