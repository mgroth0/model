package matt.model.sys

sealed interface OS {
  val caseSensitive: Boolean
  val pathSep: String
  val wrongPathSep: String
  fun replaceFileSeparators(string: String) = string.replace(wrongPathSep, pathSep)
}

sealed interface Unix: OS {
  override val pathSep get() = "/"
  override val wrongPathSep get() = "\\"
}

sealed interface Mac: Unix {
  override val caseSensitive get() = false
}

sealed interface IntelMac: Mac
sealed interface SiliconMac: Mac

sealed class Machine(
  getHomeDir: Machine.()->String,
  getRegisteredDir: Machine.()->String?,
): OS {
  val homeDir by lazy { getHomeDir() }
  val registeredDir by lazy { getRegisteredDir() }
}

sealed class SiliconMacMachine(
  homeDir: String,
  registeredDir: String?,
): Machine(
  getHomeDir = { homeDir },
  getRegisteredDir = { registeredDir }
), SiliconMac

sealed class IntelMacMachine(
  homeDir: String,
  registeredDir: String?,
): Machine(
  getHomeDir = { homeDir },
  getRegisteredDir = { registeredDir }
), IntelMac

object OLD_MAC: IntelMacMachine(
  homeDir = "/Users/matt",
  registeredDir = "Desktop/registered",
)

object NEW_MAC: SiliconMacMachine(
  homeDir = "/Users/matthewgroth",
  registeredDir = "registered",
)

class UnknownIntelMacMachine(homeDir: String): IntelMacMachine(homeDir = homeDir, registeredDir = null)
class UnknownSiliconMacMachine(homeDir: String): SiliconMacMachine(homeDir = homeDir, registeredDir = null)

sealed interface Windows: OS {
  override val caseSensitive get() = false
  override val pathSep get() = "\\"
  override val wrongPathSep get() = "/"
}

sealed class WindowsMachine(
  getHomeDir: Machine.()->String,
  getRegisteredDir: Machine.()->String,
): Machine(
  getHomeDir = getHomeDir,
  getRegisteredDir = getRegisteredDir
), Windows

object WINDOWS_11_PAR_WORK: WindowsMachine(
  getHomeDir = { "C:\\Users\\matthewgroth" },
  getRegisteredDir = { "registered" },
)

object GAMING_WINDOWS: WindowsMachine(
  getHomeDir = { error("idk what the homedir of gaming windows is") },
  getRegisteredDir = {
	error(
	  "idk what the registered dir of gaming windows is, btw, delete .registeredDir file on windows home folder"
	)
  }
)

class UnknownWindowsMachine(): WindowsMachine(
  getHomeDir = { "idk what the home dir of $this is" },
  getRegisteredDir = { "idk what the registered dir of $this is" },
)


sealed interface Linux: Unix {
  override val caseSensitive get() = true
}

sealed class LinuxMachine(
  getHomeDir: Machine.()->String,
  getRegisteredDir: Machine.()->String?,
): Machine(
  getHomeDir = getHomeDir,
  getRegisteredDir = getRegisteredDir
), Linux

class OpenMind(
  val node: OpenMindNode,
  val sImgLoc: String?,
  slurmJobID: String?
): LinuxMachine(
  getHomeDir = { "/om2/user/mjgroth" },
  getRegisteredDir = { "registered" },
) {
  val inSingularity = sImgLoc != null
  val slurmJobID = slurmJobID?.toInt()
  val inSlurmJob = slurmJobID != null

  init {
	require(inSlurmJob == node is OpenMindSlurmNode)
  }
}

sealed class OpenMindNode
object Polestar: OpenMindNode()
object OpenMindDTN: OpenMindNode()
object OpenMindMainHeadNode: OpenMindNode()
class OpenMindSlurmNode(val n: Int): OpenMindNode()

//enum class OpenMindNode {
//  Polestar,
//  OpenMindDTN,
//  OpenMindMainHeadNode /*7*/
//}

//object MainMachineForKcompRemoteExecution: OpenMind(
//  node = OpenMindMainHeadNode,
//  sImgLoc =
//)

class VagrantLinuxMachine: LinuxMachine(
  getHomeDir = { TODO() },
  getRegisteredDir = { TODO() },
)

class UnknownLinuxMachine(val hostname: String, homeDir: String): LinuxMachine(
  getHomeDir = { homeDir },
  getRegisteredDir = { null },
) {
  override fun toString() = "[${UnknownLinuxMachine::class.simpleName} with hostname=$hostname]"
}
