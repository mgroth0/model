package matt.model.data.dir

import kotlinx.serialization.Serializable
import matt.lang.function.Op

enum class Direction() {
  BI(),
  FORWARD(),
  BACKWARD()
}


enum class BeforeOrAfter {
  BEFORE, AFTER
}

enum class LeftOrRight(val textSymbol: String, val moreSymbol: String) {
  LEFT("←", "<<"), RIGHT("→", ">>")
}

@Serializable
enum class OffOrOn {
  OFF, ON
}

enum class YesOrNo {
  YES, NO;

  fun ifYes(op: Op) = if (this == YES) op() else Unit
}

enum class EnableOrDisable {
  ENABLE, DISABLE
}