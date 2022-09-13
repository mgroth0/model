package matt.model.flowlogic.keypass

import kotlin.jvm.Synchronized

class KeyPass {

  var isHeld = false
	@Synchronized private set
	@Synchronized get

  @Synchronized fun with(op: ()->Unit) {
	isHeld = true
	op()
	isHeld = false
  }

  fun hold() {
	require(!isHeld)
	isHeld = true
  }

  fun release() {
	require(isHeld)
	isHeld = false
  }
}