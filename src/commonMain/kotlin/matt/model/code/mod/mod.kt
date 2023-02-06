package matt.model.code.mod

import matt.model.code.idea.ModIdea
import kotlin.jvm.JvmInline


interface ModType: ModIdea

interface RelativeMod: ModType

interface KMod: ModType

/*implemented by generated KSubProject*/
interface RelativeToKMod: RelativeMod, KMod {
  val relToKNames: List<String>
}

@JvmInline
value class GradlePath(val path: String)

val RelativeToKMod.gradlePath get() = ":k:${relToKNames.joinToString(":")}"

interface AbsoluteMod: RelativeMod {
  val modName: String
  val groupName: String?
}

interface AbsoluteKMod: AbsoluteMod, RelativeToKMod {
  override val modName get() = relToKNames.last()
}

