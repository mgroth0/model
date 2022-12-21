package matt.model.data.dir

import kotlinx.serialization.Serializable

enum class Direction() {
  BI(),
  FORWARD(),
  BACKWARD()
}


enum class LeftOrRight(val textSymbol: String, val moreSymbol: String) {
  LEFT("←", "<<"), RIGHT("→", ">>")
}

@Serializable
enum class OffOrOn {
  OFF, ON
}

enum class YesOrNo {
  YES, NO
}