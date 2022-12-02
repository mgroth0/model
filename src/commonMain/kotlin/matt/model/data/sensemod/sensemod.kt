package matt.model.data.sensemod

import kotlinx.serialization.Serializable

@Serializable enum class SensoryModality {
  Visual, Audio
}


@Serializable enum class WaveForm {
  Square, Sin
}


@Serializable
data class Phase(val degrees: Degrees) {
  companion object {
	val ZERO = Phase(Degrees.ZERO)
	val HALF_CYCLE = Phase(Degrees(180))
  }

  override fun toString() = degrees.toString()
}

@Serializable
data class Degrees(val value: Int) {
  companion object {
	const val SYMBOL = "°"
	val ZERO = Degrees(0)
  }

  override fun toString() = "$value$SYMBOL"
}