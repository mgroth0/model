package matt.model.flowlogic.singleuse

class SingleUse() {

  private var used = false

  @Synchronized fun wasNotUsed() = !used
  @Synchronized fun wasUsed() = used

  @Synchronized
  fun requireSingleUse() {
	require(!used) {
	  "SingleUse was used more than once"
	}
	used = true
  }

}