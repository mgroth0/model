package matt.model.flowlogic.promise.j

import matt.model.flowlogic.latch.j.SimpleThreadLatch
import matt.model.flowlogic.promise.AwaitableCommitment
import matt.model.flowlogic.promise.CommitmentMaker
import matt.model.flowlogic.promise.MadeAwaitableCommitment

fun commit(make: AwaitableCommitmentMakerImpl.() -> Unit): AwaitableCommitment {
    val commitment = AwaitableCommitmentMakerImpl()
    commitment.make()
    return MadeAwaitableCommitment(commitment)
}

class AwaitableCommitmentMakerImpl() : CommitmentMaker(), AwaitableCommitment {
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
