package matt.model.flowlogic.promise

fun commit(make: Commitment.() -> Unit): Commitment {
    val commitment = CommitmentMaker()
    commitment.make()
    return MadeCommitment(commitment)
}
