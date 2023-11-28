package matt.model.ctx.console

import matt.lang.function.SuspendOp
import matt.lang.model.file.FileOrURL
import matt.lang.model.file.FsFile
import matt.lang.model.file.betterURLIGuess
import matt.model.ctx.ShowContext


object ShowInConsoleContext : ShowContext {

    override fun show(text: String) {
        println(text)
    }

    override fun showOpenFileAction(file: FsFile) {
        println("open file: ${file.betterURLIGuess}")
    }

    override fun showOpenUrlAction(
        url: FileOrURL,
        label: String?
    ) {
        val labelPart = if (label == null) "" else "[$label]"
        println("open url$labelPart: $url")
    }

    override fun showGenericAction(
        text: String,
        action: SuspendOp
    ) {
        TODO("Not yet implemented")
    }

    override fun showRevealFileAction(file: FsFile) {
        println("open in finder: $file")
    }

    override fun showStatus(status: String) {
        println("status: $status")
    }
}