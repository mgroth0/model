package matt.model.flowlogic.loader

import matt.lang.function.Consume

// interface ReUsable< T> {
//    fun reUse(): T
// }

interface ObjectLoader<T, S> {
    suspend fun reUse(t: T): T

    suspend fun generateFrom(
        s: S,
        onLoad: Consume<T>,
        onErr: Consume<String>,
        onPartProgress: Consume<Double>,
    )
}

// interface PostLoader<out T, S> {
//    suspend fun generateFrom(
//        s: S,
//        onLoad: Consume<T>,
//        onErr: Op,
//        onPartProgress: Consume<Double>
//    )
// }
