package matt.model.data.proxy.collect

import matt.lang.convert.Converter


abstract class ProxyCollection<S, T>(
    private val innerCollection: Collection<S>,
    private val converter: Converter<S, T>
) : Collection<T> {
    final override val size: Int
        get() = innerCollection.size

    final override fun isEmpty(): Boolean {
        return innerCollection.isEmpty()
    }

}


/*
abstract class MutableProxyCollection<S, T>(
    private val innerCollection: MutableCollection<S>,
    private val converter: Converter<S, T>
) : ProxyCollection<S,T>(innerCollection, converter), MutableCollection<T>*/
