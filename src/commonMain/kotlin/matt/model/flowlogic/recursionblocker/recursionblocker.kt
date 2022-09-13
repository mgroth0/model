package matt.model.flowlogic.recursionblocker

class RecursionBlocker() {
  private var inBlock = false
  fun with(op: ()->Unit) {
	if (inBlock) return
	inBlock = true
	op()
	inBlock = false
  }
}