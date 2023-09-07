package matt.model.flowlogic.promise

import matt.model.flowlogic.latch.SimpleThreadLatch

fun commit(make: AwaitableCommitmentMakerImpl.()->Unit): AwaitableCommitment {
  val commitment = AwaitableCommitmentMakerImpl()
  commitment.make()
  return MadeAwaitableCommitment(commitment)
}


class AwaitableCommitmentMakerImpl(): CommitmentMaker(), AwaitableCommitment {

  private val latch by lazy {
	SimpleThreadLatch().also {
	  this@AwaitableCommitmentMakerImpl.then {
		it.open()
	  }
	}
  }

  override fun await() {
	latch.await()
  }

}