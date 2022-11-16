package matt.model.percent

import kotlin.jvm.JvmInline

interface PercentIdea

val Int.percent get() = Percent(this)

@JvmInline
value class Percent(val percent: Int): PercentIdea