package matt.model.code.ok

import kotlin.annotation.AnnotationTarget.FILE



@Target(FILE)
annotation class UnnamedPackageIsOk

/*Because setting JvmName is messing things up for android. Basically seems like only way to have expect and actual functions and classes for android, for now*/
@Target(FILE)
annotation class FilePackageNameMismatchIsOk