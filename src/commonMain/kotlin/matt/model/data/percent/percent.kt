package matt.model.data.percent

import kotlin.jvm.JvmInline

interface PercentIdea

val Int.percent get() = Percent(this)

@JvmInline
value class Percent(val percent: Int): PercentIdea