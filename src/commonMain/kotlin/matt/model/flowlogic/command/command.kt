package matt.model.flowlogic.command

interface Command {
  fun run(arg: String)
}


enum class ExitStatus {
  CONTINUE, EXIT
}


