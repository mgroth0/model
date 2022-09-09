package matt.model.idlist

import matt.model.obj.MaybeIdentified

/*because if I delete the most recent, the list still needs to remember not to reuse*/
abstract class IdentifiedList<I: Any, O: MaybeIdentified<I>>(): List<O> {
  protected abstract var nextNewID: I

  fun createNewID(): I {
	println("WARNING: MUST SAVE THE IdentifiedList NOW!")
	return nextNewID.also { nextNewID = increment(nextNewID) }
  }

  abstract fun increment(id: I): I

}