package matt.model.promise

import matt.lang.function.Op
import matt.lang.function.Produce
import matt.model.idea.ProceedingIdea
import kotlin.jvm.Synchronized

/*Loosely based on the general computer science construct Promise and its javascript implementation and inspired by its name. For me, the name has literal significance too. Used in a case when an abstract function is supposed to do something asynchronously. But since there is sometimes nothing to return, there is less enforcement that the subclass actually implements the asynchronous function. The Commitment is a way for the subclass to "commit" that it is doing the async operation that it is supposed to be doing*/

/*why don't I just call these Promises...*/

fun commit(make: CommitmentMaker.()->Unit): Commitment {
  val commitment = CommitmentMaker()
  commitment.make()
  return MadeCommitment(commitment)
}

interface Commitment: ProceedingIdea {
  val isFulfilled: Boolean
  fun then(op: Op): Commitment
  fun thenAsync(op: Produce<Commitment>): Commitment
  val debugID: Int
}

class MadeCommitment(private val maker: CommitmentMaker): Commitment by maker

class CommitmentMaker: Commitment {

  companion object {
	var nextID = 0
  }

  override val debugID = nextID++

  override var isFulfilled = false
	private set

  @Synchronized
  fun fulfilled() {
	require(!isFulfilled) {
	  "fulfilled twice"
	}
	isFulfilled = true
	thens.forEach {
	  it()
	}
  }

  private val thens = mutableListOf<Op>()

  @Synchronized
  override fun then(op: Op): Commitment {
	val c = CommitmentMaker()
	if (isFulfilled) {
	  op()
	  c.fulfilled()
	} else {
	  thens += {
		op()
		c.fulfilled()
	  }
	}
	return c
  }


  @Synchronized
  override fun thenAsync(op: Produce<Commitment>): Commitment {
	val c = CommitmentMaker()
	if (isFulfilled) {
	  op().then {
		c.fulfilled()
	  }
	} else {
	  thens += {
		op().then {
		  c.fulfilled()
		}
	  }
	}
	return c
  }
}