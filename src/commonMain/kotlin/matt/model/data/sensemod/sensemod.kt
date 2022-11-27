package matt.model.data.sensemod

import kotlinx.serialization.Serializable

@Serializable enum class SensoryModality {
  Visual, Audio
}


@Serializable enum class WaveForm {
  Square, Sin
}


@Serializable
class Phase(val degrees: Degrees) {
  override fun toString() = degrees.toString()
}

@Serializable
class Degrees(val value: Int) {
  companion object {
	const val SYMBOL = "°"
  }
  override fun toString() = "$value$SYMBOL"
}