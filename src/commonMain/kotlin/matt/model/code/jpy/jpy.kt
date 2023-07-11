package matt.model.code.jpy

import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY

annotation class PyClass
annotation class ExcludeConstructorFromPython
annotation class ExcludeFromPython
annotation class PythonName(val value: String)
annotation class PyGlobalFun

@Target(PROPERTY, FUNCTION)
annotation class PythonCallableName(val value: String)