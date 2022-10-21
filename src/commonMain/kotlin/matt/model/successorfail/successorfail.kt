package matt.model.successorfail

sealed interface SuccessOrFail {
  val message: String
}

object Success: SuccessOrFail {
  override val message = ""
}

class Fail(override val message: String): SuccessOrFail