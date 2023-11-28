package matt.model.data.mathable.mathext

import matt.model.data.mathable.NumberWrapper


fun <M : NumberWrapper<M>> List<M>.minOr(seed: Int): M = minOrNull() ?: first().of(seed)
fun <M : NumberWrapper<M>> List<M>.minOrZero() = minOr(0)

fun <M : NumberWrapper<M>> List<M>.maxOr(seed: Int): M = maxOrNull() ?: first().of(seed)
fun <M : NumberWrapper<M>> List<M>.maxOrZero() = maxOr(0)