package matt.model.data.proxy.map

import matt.model.data.proxy.set.ProxyMutableSet
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
        get() = ProxyMutableSet(innerMap.entries, object : Converter<MutableEntry<SK, SV>, MutableEntry<TK, TV>> {

            override fun convertToB(a: MutableEntry<SK, SV>): MutableEntry<TK, TV> {
                return FakeMutableEntry(EntryImpl(keyConverter.convertToB(a.key), valueConverter.convertToB(a.value)))
            }

            override fun convertToA(b: MutableEntry<TK, TV>): MutableEntry<SK, SV> {
                return FakeMutableEntry(EntryImpl(keyConverter.convertToA(b.key), valueConverter.convertToA(b.value)))
            }

        })

            /*.map { it.toFakeMutableEntry() }.toSet().toFakeMutableSet()*/

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




