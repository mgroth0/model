package matt.model.data.proxy.map

import matt.model.op.convert.Converter
import kotlin.collections.Map.Entry
import kotlin.collections.MutableMap.MutableEntry

fun <SK: Any, SV: Any, TK: Any, TV: Any> MutableMap<SK, SV>.proxy(
  keyConverter: Converter<SK, TK>,
  valueConverter: Converter<SV, TV>
) = ProxyMap(this, keyConverter, valueConverter)

fun <K, V> Entry<K, V>.toFakeMutableEntry() = FakeMutableEntry(this)
class FakeMutableEntry<K, V>(entry: Entry<K, V>): MutableEntry<K, V> {
  override val key = entry.key
  override val value = entry.value

  override fun setValue(newValue: V): V {
	error("FakeMutableEntry is FAKE")
  }
}

class ProxyMap<SK: Any, SV: Any, TK: Any, TV: Any>(
  private val innerMap: MutableMap<SK, SV>,
  private val keyConverter: Converter<SK, TK>,
  private val valueConverter: Converter<SV, TV>
): MutableMap<TK, TV> {

  init {
	println("TODO: figure out where fake mutable set and fake mutable entry should be")
  }

  private fun SK.toTK() = keyConverter.convertToB(this)
  private fun SV.toTV() = valueConverter.convertToB(this)
  private fun TK.toSK() = keyConverter.convertToA(this)
  private fun TV.toSV() = valueConverter.convertToA(this)

  override val entries: MutableSet<MutableEntry<TK, TV>>
	get() = run {
	 val theSet = innerMap.entries.map { e ->
		object: Entry<TK, TV> {
		  override val key = e.key.toTK()
		  override val value = e.value.toTV()
		}.toFakeMutableEntry()
	  }.toSet()

	  object: MutableSet<MutableEntry<TK, TV>> {
		override fun add(element: MutableEntry<TK, TV>): Boolean {
		  TODO("Not yet implemented")
		}

		override fun addAll(elements: Collection<MutableEntry<TK, TV>>): Boolean {
		  TODO("Not yet implemented")
		}

		override val size: Int
		  get() = TODO("Not yet implemented")

		override fun clear() {
		  TODO("Not yet implemented")
		}

		override fun isEmpty(): Boolean {
		  TODO("Not yet implemented")
		}

		override fun containsAll(elements: Collection<MutableEntry<TK, TV>>): Boolean {
		  TODO("Not yet implemented")
		}

		override fun contains(element: MutableEntry<TK, TV>): Boolean {
		  TODO("Not yet implemented")
		}

		override fun iterator(): MutableIterator<MutableEntry<TK, TV>> {
		  val itr = theSet.iterator()
		  return object: MutableIterator<MutableEntry<TK, TV>> {
			override fun hasNext(): Boolean {
			  return itr.hasNext()
			}

			override fun next(): MutableEntry<TK, TV> {
			  return itr.next()
			}

			override fun remove() {
			  TODO("Not yet implemented")
			}

		  }
		}

		override fun retainAll(elements: Collection<MutableEntry<TK, TV>>): Boolean {
		  TODO("Not yet implemented")
		}

		override fun removeAll(elements: Collection<MutableEntry<TK, TV>>): Boolean {
		  TODO("Not yet implemented")
		}

		override fun remove(element: MutableEntry<TK, TV>): Boolean {
		  TODO("Not yet implemented")
		}

	  }
	}


  override val keys: MutableSet<TK>
	get() = TODO("Not yet implemented")
  override val size: Int
	get() = innerMap.size
  override val values: MutableCollection<TV>
	get() = TODO("Not yet implemented")

  override fun clear() = innerMap.clear()

  override fun isEmpty() = innerMap.isEmpty()

  override fun remove(key: TK) = innerMap.remove(key.toSK())?.toTV()

  override fun putAll(from: Map<out TK, TV>) {
	innerMap.putAll(from.mapKeys { it.key.toSK() }.mapValues { it.value.toSV() })
  }

  override fun put(key: TK, value: TV): TV? {
	return innerMap.put(key.toSK(), value.toSV())?.toTV()
  }

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




