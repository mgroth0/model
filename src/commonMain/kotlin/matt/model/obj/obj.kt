package matt.model.obj

import kotlinx.serialization.Transient

/*
Because an IDE warning is not strong enough for me
https://github.com/Kotlin/kotlinx.serialization/issues/470
*/
typealias KtSerializationTransient = Transient

private val dummy = 1.apply {
    if (KotlinVersion.CURRENT.major >= 2) {
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
    override var id: I
) : Identified<I> {
    override fun toString() =
        "${this::class.simpleName} $id: $name"
}


interface DSL


abstract class SimpleData(private val identity: Any) {
    override fun equals(other: Any?): Boolean {
        return other != null && other::class == this::class && (other as SimpleData).identity == identity
    }

    override fun hashCode(): Int {
        return identity.hashCode()
    }
}


interface Searchable {
    val searchSeq: Sequence<String>
}
