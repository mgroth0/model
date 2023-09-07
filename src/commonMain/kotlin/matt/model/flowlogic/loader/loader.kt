package matt.model.flowlogic.loader

import matt.lang.function.Consume
import matt.lang.function.Op

interface ObjectLoader<out T, S> {
    suspend fun generateFrom(
        s: S,
        onLoad: Consume<T>,
        onErr: Op,
        onPartProgress: Consume<Double>
    )
}


//interface PostLoader<out T, S> {
//    suspend fun generateFrom(
//        s: S,
//        onLoad: Consume<T>,
//        onErr: Op,
//        onPartProgress: Consume<Double>
//    )
//}
