package matt.model.synclist

class SynchronousListManager<E>(val list: MutableList<E>) {
  private val toRemove = mutableListOf<E>()
  private val toAdd = mutableListOf<E>()
}