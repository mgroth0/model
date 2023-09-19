package matt.model.code.sys

import matt.lang.anno.SeeURL
import matt.lang.model.file.BaseFileSystem
import matt.lang.model.file.CaseSensitivity.CaseInSensitive
import matt.lang.model.file.CaseSensitivity.CaseSensitive
import matt.lang.model.file.FileSystem
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.UnixFileSystem
import matt.lang.platform.OSIdea
import matt.lang.require.requireEquals

sealed interface OS : OSIdea {
    val fileSystem: FileSystem
    val wrongPathSep: String
    fun replaceFileSeparators(string: String) = string.replace(wrongPathSep, fileSystem.separatorChar.toString())
}

sealed interface Unix : OS {
    override val fileSystem: UnixFileSystem
    override val wrongPathSep get() = "\\"
}


sealed interface Mac : Unix {
    override val fileSystem get() = MacFileSystem
}

sealed interface IntelMac : Mac
sealed interface SiliconMac : Mac

sealed class Machine(
    getHomeDir: Machine.() -> String,
    getRegisteredDir: Machine.() -> String?,
) : OS {
    val homeDir by lazy { getHomeDir() }
    val registeredDir by lazy { getRegisteredDir() }
}

sealed class SiliconMacMachine(
    homeDir: String,
    registeredDir: String?,
) : Machine(
    getHomeDir = { homeDir },
    getRegisteredDir = { registeredDir }
), SiliconMac

sealed class IntelMacMachine(
    homeDir: String,
    registeredDir: String?,
) : Machine(
    getHomeDir = { homeDir },
    getRegisteredDir = { registeredDir }
), IntelMac

object OLD_MAC : IntelMacMachine(
    homeDir = "/Users/matt",
    registeredDir = "Desktop/registered",
)

object NEW_MAC : SiliconMacMachine(
    homeDir = "/Users/matthewgroth",
    registeredDir = "registered",
)

class UnknownIntelMacMachine(homeDir: String) : IntelMacMachine(homeDir = homeDir, registeredDir = null)
class UnknownSiliconMacMachine(homeDir: String) : SiliconMacMachine(homeDir = homeDir, registeredDir = null)

object WindowsFileSystem : BaseFileSystem() {
    override val caseSensitivity = CaseInSensitive
    override val separatorChar = '\\'
}

sealed interface Windows : OS {
    override val fileSystem get() = WindowsFileSystem
    override val wrongPathSep get() = "/"
}

sealed class WindowsMachine(
    getHomeDir: Machine.() -> String,
    getRegisteredDir: Machine.() -> String,
) : Machine(
    getHomeDir = getHomeDir,
    getRegisteredDir = getRegisteredDir
), Windows

object WINDOWS_11_PAR_WORK : WindowsMachine(
    getHomeDir = { "C:\\Users\\matthewgroth" },
    getRegisteredDir = { "registered" },
)

object GAMING_WINDOWS : WindowsMachine(
    getHomeDir = { error("idk what the homedir of gaming windows is") },
    getRegisteredDir = {
        error(
            "idk what the registered dir of gaming windows is, btw, delete .registeredDir file on windows home folder"
        )
    }
)

class UnknownWindowsMachine() : WindowsMachine(
    getHomeDir = { "idk what the home dir of $this is" },
    getRegisteredDir = { "idk what the registered dir of $this is" },
)

/*Careful! Android's OS is case-sensitive yes, but commonly attached file devices like sd cards are often case-insensitive! */
@SeeURL("https://stackoverflow.com/a/6502881/6596010")
object LinuxFileSystem : UnixFileSystem() {
    override val caseSensitivity = CaseSensitive
}

sealed interface Linux : Unix {
    override val fileSystem get() = LinuxFileSystem
    val isAarch64: Boolean
}

sealed class LinuxMachine(
    getHomeDir: Machine.() -> String,
    getRegisteredDir: Machine.() -> String?,
) : Machine(
    getHomeDir = getHomeDir,
    getRegisteredDir = getRegisteredDir
), Linux

@SeeURL("https://github.mit.edu/MGHPCC/OpenMind/issues/4435")
class OpenMind(
    val node: OpenMindNode,
    val sImgLoc: String?,
    slurmJobID: String?
) : LinuxMachine(
    getHomeDir = { "/om2/vast/user/mjgroth" },
    getRegisteredDir = { "registered" },
) {
    val inSingularity = sImgLoc != null
    val slurmJobID = slurmJobID?.toInt()
    val inSlurmJob = slurmJobID != null

    init {
        requireEquals(inSlurmJob, node is OpenMindSlurmNode)
    }

    override val isAarch64: Boolean
        get() = TODO("Not yet implemented")
}

sealed class OpenMindNode
object Polestar : OpenMindNode()
object OpenMindDTN : OpenMindNode()
object OpenMindMainHeadNode : OpenMindNode()
class OpenMindSlurmNode(val n: Int) : OpenMindNode()

//enum class OpenMindNode {
//  Polestar,
//  OpenMindDTN,
//  OpenMindMainHeadNode /*7*/
//}

//object MainMachineForKcompRemoteExecution: OpenMind(
//  node = OpenMindMainHeadNode,
//  sImgLoc =
//)

class VagrantLinuxMachine : LinuxMachine(
    getHomeDir = { TODO() },
    getRegisteredDir = { TODO() },
) {
    override val isAarch64: Boolean
        get() = TODO("Not yet implemented")
}

class UnknownLinuxMachine(
    val hostname: String,
    homeDir: String,
    isAarch64: Lazy<Boolean>
) : LinuxMachine(
    getHomeDir = { homeDir },
    getRegisteredDir = { null },
) {
    override fun toString() = "[${UnknownLinuxMachine::class.simpleName} with hostname=$hostname]"
    override val isAarch64 by isAarch64
}
