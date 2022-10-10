package matt.model.mod

import matt.model.idea.ModIdea


interface ModType: ModIdea

interface RelativeMod: ModType

interface KMod: ModType

/*implemented by generated KSubProject*/
interface RelativeToKMod: RelativeMod, KMod {
  val relToKNames: List<String>
}

val RelativeToKMod.gradlePath get() = ":k:${relToKNames.joinToString(":")}"

interface AbsoluteMod: RelativeMod
interface AbsoluteKMod: AbsoluteMod, RelativeToKMod