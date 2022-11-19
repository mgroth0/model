package matt.model.code.successorfail

sealed interface SuccessOrFail {
  val message: String
}

object Success: SuccessOrFail {
  override val message = ""
}

class Fail(override val message: String): SuccessOrFail