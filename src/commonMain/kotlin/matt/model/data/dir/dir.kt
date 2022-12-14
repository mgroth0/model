package matt.model.data.dir

import kotlinx.serialization.Serializable

enum class Direction() {
  BI(),
  FORWARD(),
  BACKWARD()
}


enum class LeftOrRight(val textSymbol: String) {
  LEFT("←"), RIGHT("→")
}

@Serializable
enum class OffOrOn {
  OFF, ON
}