package matt.model.ctx.console

import matt.lang.function.SuspendOp
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.AnyResolvableFileOrUrl
import matt.lang.model.file.betterURLIGuess
import matt.model.ctx.ShowContext


object ShowInConsoleContext : ShowContext {


    override fun show(text: String) {
        println(text)
    }

    override fun showOpenFileAction(file: AnyFsFile) {
        println("open file: ${file.betterURLIGuess}")
    }

    override fun showOpenUrlAction(
        url: AnyResolvableFileOrUrl,
        label: String?
    ) {
        val labelPart = if (label == null) "" else "[$label]"
        println("open url$labelPart: $url")
    }

    override fun showGenericAction(
        text: String,
        action: SuspendOp
    ) {
        TODO()
    }

    override fun showRevealFileAction(file: AnyFsFile) {
        println("open in finder: $file")
    }

    override fun showStatus(status: String) {
        println("showing status in ShowInConsoleContext...")
        println("status: $status")
    }
}

