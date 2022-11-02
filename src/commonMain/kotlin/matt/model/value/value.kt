package matt.model.value

interface ValueWrapper<T> {
  val value: T
}

/*perfect for wen null is not the same thing as empty*/
/*should be value class*/
class Value<T>(override val value: T): ValueWrapper<T> {
  companion object {
	init {
	  if (KotlinVersion.CURRENT.isAtLeast(1, 8)) {
		println("this should be a generic value class")
	  }
	}
  }
}