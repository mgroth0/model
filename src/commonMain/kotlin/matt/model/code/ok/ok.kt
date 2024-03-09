package matt.model.code.ok

import kotlin.annotation.AnnotationTarget.FILE


/*Because setting JvmName is messing things up for android. Basically seems like only way to have expect and actual functions and classes for android, for now


I think this has not yet been made to work with Detekt because the only place it is currently being used is in a Compose module, where Detekt is still not executing as of now*/
@Target(FILE)
annotation class FilePackageNameMismatchIsOk
