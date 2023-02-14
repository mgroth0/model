package matt.model.flowlogic.keypass

import matt.lang.anno.OnlySynchronizedOnJvm

class KeyPass {

  var isHeld = false
	@OnlySynchronizedOnJvm private set
	@OnlySynchronizedOnJvm get

  val isNotHeld get() = !isHeld

  @OnlySynchronizedOnJvm
  fun <R> with(op: ()->R): R {
	isHeld = true
	val r = op()
	isHeld = false
	return r
  }

  @OnlySynchronizedOnJvm
  fun hold() {
	require(!isHeld)
	isHeld = true
  }

  @OnlySynchronizedOnJvm
  fun release() {
	require(isHeld)
	isHeld = false
  }
}