package matt.model.code.ctx

import matt.lang.function.Produce
import matt.lang.function.SuspendOp
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.AnyResolvableFileOrUrl

@ShowDslMarker
interface StatusContext {
    fun showStatus(status: String)
}

@ShowDslMarker
inline fun <R> StatusContext.showRunningAndFinishedStatuses(
    opLabel: String,
    op: Produce<R>
) = showVerbAndFinishedStatuses("Running", opLabel, op)

@ShowDslMarker
inline fun <R> StatusContext.showVerbAndFinishedStatuses(
    verb: String,
    opLabel: String,
    op: Produce<R>
): R {
    showStatus("$verb $opLabel")
    val r = op()
    showStatus("Finished $verb $opLabel")
    return r
}


@DslMarker
annotation class ShowDslMarker

@ShowDslMarker
interface ShowContext : StatusContext {
    fun show(text: String)
    fun showOpenFileAction(file: AnyFsFile)
    fun showOpenUrlAction(
        url: AnyResolvableFileOrUrl,
        label: String? = null
    )

    fun showGenericAction(
        text: String,
        action: SuspendOp
    )

    fun showRevealFileAction(file: AnyFsFile)
}
