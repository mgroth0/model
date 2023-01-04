package matt.model.code.mod

import matt.model.code.idea.ModIdea


interface ModType: ModIdea

interface RelativeMod: ModType

interface KMod: ModType

/*implemented by generated KSubProject*/
interface RelativeToKMod: RelativeMod, KMod {
  val relToKNames: List<String>
}

val RelativeToKMod.gradlePath get() = ":k:${relToKNames.joinToString(":")}"

interface AbsoluteMod: RelativeMod {
  val modName: String
  val groupName: String?
}

interface AbsoluteKMod: AbsoluteMod, RelativeToKMod {
  override val modName get() = relToKNames.last()
}

