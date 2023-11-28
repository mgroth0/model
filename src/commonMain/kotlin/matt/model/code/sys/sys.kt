package matt.model.code.sys

import matt.lang.anno.SeeURL
import matt.lang.model.file.BaseFileSystem
import matt.lang.model.file.CaseSensitivity.CaseInSensitive
import matt.lang.model.file.CaseSensitivity.CaseSensitive
import matt.lang.model.file.FileSystem
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.UnixFileSystem
import matt.lang.platform.OSIdea
import matt.lang.assertions.require.requireEquals
import matt.model.code.sys.OsArchitecture.LinuxAarch64
import matt.model.code.sys.OsArchitecture.MacIntel
import matt.model.code.sys.OsArchitecture.MacSilicon
import matt.model.code.sys.OsArchitecture.OtherLinux

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
    abstract val architecture: OsArchitecture
}

sealed class SiliconMacMachine(
    homeDir: String,
    registeredDir: String?,
) : Machine(
    getHomeDir = { homeDir },
    getRegisteredDir = { registeredDir }
), SiliconMac {
    override val architecture = MacSilicon
}

sealed class IntelMacMachine(
    homeDir: String,
    registeredDir: String?,
) : Machine(
    getHomeDir = { homeDir },
    getRegisteredDir = { registeredDir }
), IntelMac {
    override val architecture = MacIntel
}

object OldMac : IntelMacMachine(
    homeDir = "/Users/matt",
    registeredDir = "Desktop/registered",
)

object NewMac : SiliconMacMachine(
    homeDir = "/Users/matthewgroth",
    registeredDir = "registered",
)

class UnknownIntelMacMachine(homeDir: String) : IntelMacMachine(homeDir = homeDir, registeredDir = null)
class UnknownSiliconMacMachine(homeDir: String) : SiliconMacMachine(homeDir = homeDir, registeredDir = null)

data object WindowsFileSystem : BaseFileSystem() {
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
), Windows {
    override val architecture = OsArchitecture.Windows
}

object WINDOWS_11_PAR_WORK : WindowsMachine(
    getHomeDir = { "C:\\Users\\matthewgroth" },
    getRegisteredDir = { "registered" },
)

object WindowsLaptop : WindowsMachine(
    getHomeDir = { error("idk what the homedir of windows laptop is") },
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
data object LinuxFileSystem : UnixFileSystem() {
    override val caseSensitivity = CaseSensitive
}

sealed interface Linux : Unix {
    override val fileSystem get() = LinuxFileSystem
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


    override val architecture get() = TODO()
}

sealed class OpenMindNode
data object Polestar : OpenMindNode()
data object OpenMindDTN : OpenMindNode()
data object OpenMindMainHeadNode : OpenMindNode()
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
    override val architecture get() = TODO()
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
    override val architecture by lazy {
        if (isAarch64.value) LinuxAarch64 else OtherLinux
    }
}


interface OsArchitectureIdea

enum class OsArchitecture : OsArchitectureIdea {
    MacIntel,
    MacSilicon,
    LinuxAarch64,
    OtherLinux, /*todo*/
    Windows /*todo*/
}
