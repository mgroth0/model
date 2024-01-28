package matt.model.flowlogic.promise

import matt.lang.assertions.require.requireNot
import matt.lang.function.Op
import matt.lang.function.Produce
import matt.lang.idea.ProceedingIdea
import matt.lang.sync.ReferenceMonitor
import matt.lang.sync.inSync
import matt.model.flowlogic.await.ThreadAwaitable

/*Loosely based on the general computer science construct Promise and its javascript implementation and inspired by its name. For me, the name has literal significance too. Used in a case when an abstract function is supposed to do something asynchronously. But since there is sometimes nothing to return, there is less enforcement that the subclass actually implements the asynchronous function. The Commitment is a way for the subclass to "commit" that it is doing the async operation that it is supposed to be doing*/

/*why don't I just call these Promises...*/


interface AwaitableCommitment : Commitment, ThreadAwaitable<Unit>

interface Commitment : ProceedingIdea {
    val isFulfilled: Boolean
    fun then(op: Op): Commitment
    fun thenAsync(op: Produce<Commitment>): Commitment
    val debugID: Int
}

class MadeCommitment(private val maker: CommitmentMaker) : Commitment by maker
class MadeAwaitableCommitment(private val maker: AwaitableCommitment) : AwaitableCommitment by maker

open class CommitmentMaker : Commitment, ReferenceMonitor {

    companion object {
        var nextID = 0
    }

    final override val debugID = nextID++

    final override var isFulfilled = false
        private set

    fun fulfilled() = inSync {
        requireNot(isFulfilled) {
            "fulfilled twice"
        }
        isFulfilled = true
        thens.forEach {
            it()
        }
    }

    private val thens = mutableListOf<Op>()

    final override fun then(op: Op): Commitment = inSync {
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


    final override fun thenAsync(op: Produce<Commitment>): Commitment = inSync {
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