package matt.model.successorfail

sealed interface SuccessOrFail

object Success: SuccessOrFail
class Fail(val message: String): SuccessOrFail