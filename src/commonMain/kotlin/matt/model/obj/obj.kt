package matt.model.obj


interface Identified<I: Any>: MaybeIdentified<I> {
  override val id: I
}

interface MaybeIdentified<I: Any> {
  val id: I?
}

interface ConstNamed {
  val name: String
}

interface Named {
  var name: String
}

open class Unique<I: Any>(
  open var name: String,
  override var id: I
): Identified<I> {
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