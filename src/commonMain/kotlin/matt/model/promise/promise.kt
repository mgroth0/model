package matt.model.promise

import matt.model.idea.ProceedingIdea

/*Loosely based on the general computer science construct Promise and its javascript implementation and inspired by its name. For me, the name has literal significance too. Used in a case when an abstract function is supposed to do something asynchronously. But since there is sometimes nothing to return, there is less enforcement that the subclass actually implements the asynchronous function. The Commitment is a way for the subclass to "commit" that it is doing the async operation that it is supposed to be doing*/

fun commit(make: CommitmentMaker.()->Unit): Commitment {
  val commitment = CommitmentMaker()
  commitment.make()
  return MadeCommitment(commitment)
}

interface Commitment: ProceedingIdea {
  val isFulfilled: Boolean
}

class MadeCommitment(private val maker: CommitmentMaker): Commitment by maker

class CommitmentMaker: Commitment {
  override var isFulfilled = false
	private set

  fun fulfilled() {
	isFulfilled = true
  }
}