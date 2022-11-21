package matt.model.data.sensemod

import kotlinx.serialization.Serializable

@Serializable enum class SensoryModality {
  Visual, Audio
}


@Serializable enum class WaveForm {
  Square, Sin
}

@Serializable
class WaveConfig(val form: WaveForm, val phase: Phase)

@Serializable
class Phase(val degrees: Degrees)

@Serializable
class Degrees(val value: Int)