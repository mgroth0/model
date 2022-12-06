package matt.model.data.dir

enum class Direction() {
  BI(),
  FORWARD(),
  BACKWARD()
}


enum class LeftOrRight(val textSymbol: String) {
  LEFT("←"), RIGHT("→")
}