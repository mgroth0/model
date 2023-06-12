package matt.model.rest

import matt.lang.idea.Idea


interface EndpointIdea : Idea

interface OneEndpoint : EndpointIdea

interface MResource<D : Any> : EndpointIdea {
    val path: String
}

inline fun <reified D : Any> MResource<D>.typeInfo() = io.ktor.util.reflect.typeInfo<D>()