package matt.model.code

interface FormatterConfig {
    object None : FormatterConfig
}


interface Code<C : Code<C, F>, F : FormatterConfig> {
    val code: String
    fun formatted(formatterConfig: F): C
}

interface SimpleFormatCode<C : SimpleFormatCode<C>> : Code<C, FormatterConfig.None> {
    override fun formatted(formatterConfig: FormatterConfig.None): C = formatted()
    fun formatted(): C
}

/*fun <C : Code<C, None>> C.formatted() = formatted(None)*/

interface CodeGenerator<C : Code<C, *>> {
    fun generate(): C
}

interface SimpleCodeGenerator<C : SimpleFormatCode<C>> : CodeGenerator<C>