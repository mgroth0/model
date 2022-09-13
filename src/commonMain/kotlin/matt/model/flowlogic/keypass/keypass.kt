package matt.model.flowlogic.keypass

import kotlin.jvm.Synchronized

class KeyPass {

  var isHeld = false
	@Synchronized private set
	@Synchronized get

  @Synchronized
  fun <R> with(op: ()->R): R {
	isHeld = true
	val r = op()
	isHeld = false
	return r
  }

  @Synchronized
  fun hold() {
	require(!isHeld)
	isHeld = true
  }

  @Synchronized
  fun release() {
	require(isHeld)
	isHeld = false
  }
}