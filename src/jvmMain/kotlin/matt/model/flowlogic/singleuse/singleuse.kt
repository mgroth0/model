package matt.model.flowlogic.singleuse

import matt.lang.assertions.require.requireNot

class SingleUse() {
    private var used = false

    @Synchronized
    fun wasNotUsed() = !used

    @Synchronized
    fun wasUsed() = used

    @Synchronized
    fun requireSingleUse() {
        requireNot(used) {
            "SingleUse was used more than once"
        }
        used = true
    }
}
