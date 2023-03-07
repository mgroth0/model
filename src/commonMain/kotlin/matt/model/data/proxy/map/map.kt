package matt.model.data.proxy.map

import matt.model.op.convert.Converter
import kotlin.collections.MutableMap.MutableEntry

fun <SK: Any, SV: Any, TK: Any, TV: Any> MutableMap<SK, SV>.proxy(
  keyConverter: Converter<SK, TK>,
  valueConverter: Converter<SV, TV>
) = ProxyMap(this, keyConverter, valueConverter)

class ProxyMap<SK: Any, SV: Any, TK: Any, TV: Any>(
  private val innerMap: MutableMap<SK, SV>,
  private val keyConverter: Converter<SK, TK>,
  private val valueConverter: Converter<SV, TV>
): MutableMap<TK, TV> {

  private fun SK.toTK() = keyConverter.convertToB(this)
  private fun SV.toTV() = valueConverter.convertToB(this)
  private fun TK.toSK() = keyConverter.convertToA(this)
  private fun TV.toSV() = valueConverter.convertToA(this)

  override val entries: MutableSet<MutableEntry<TK, TV>>
	get() = TODO("Not yet implemented")
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