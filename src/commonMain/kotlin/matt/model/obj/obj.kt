package matt.model.obj

import kotlinx.serialization.Transient
import matt.lang.ktversion.ifPastInitialK2

/*
Because an IDE warning is not strong enough for me
https://github.com/Kotlin/kotlinx.serialization/issues/470
*/
typealias KtSerializationTransient = Transient

private val dummy =
    1.apply {
        ifPastInitialK2 {
            println(
                """
                        
                        CAN I FINALLY DO THIS CHECK???
                        
                           check(KtSerializationTransient::class.qualifiedName == "kotlinx.serialization.Transient") {
                    "must ensure this doesn't accidentally get mixed with the other Jvm Transient, because otherwise it could become a silent error."
                }
                
                
                AS OF KOTLIN 1.9.20 I cannot because of "Unsupported [This reflection API is not supported yet in JavaScript]"
                        
                """.trimIndent()
            )
        }
    }

interface Identified<I : Any> : MaybeIdentified<I> {
    override val id: I
}

interface MaybeIdentified<I : Any> {
    val id: I?
}

interface ConstNamed {
    val name: String
}

interface Named {
    var name: String
}

open class Unique<I : Any>(
    open var name: String,
    final override var id: I
) : Identified<I> {
    final override fun toString() =
        "${this::class.simpleName} $id: $name"
}


interface Dsl


abstract class SimpleData(private val identity: Any) {
    final override fun equals(
        other: Any?
    ): Boolean = other != null && other::class == this::class && (other as SimpleData).identity == identity

    final override fun hashCode(): Int = identity.hashCode()
}


interface Searchable {
    val searchSeq: Sequence<String>
}

interface Labeled {
    val label: String
}
