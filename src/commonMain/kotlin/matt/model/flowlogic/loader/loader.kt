package matt.model.flowlogic.loader

import matt.lang.function.Consume


interface ObjectLoader<T, S> {
    suspend fun reUse(t: T): T

    suspend fun generateFrom(
        s: S,
        onLoad: Consume<T>,
        onErr: Consume<String>,
        onPartProgress: Consume<Double>
    )
}

