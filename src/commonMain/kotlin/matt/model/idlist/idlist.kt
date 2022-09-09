package matt.model.idlist

import matt.model.obj.MaybeIdentified

/*because if I delete the most recent, the list still needs to remember not to reuse*/
abstract class IdentifiedList(val list: List<MaybeIdentified<Int>>): List<MaybeIdentified<Int>> by list {
  protected abstract var nextNewID: Int

  fun createNewID(): Int {
    println("WARNING: MUST SAVE THE IdentifiedList NOW!")
	return nextNewID.also { nextNewID += 1 }
  }
}