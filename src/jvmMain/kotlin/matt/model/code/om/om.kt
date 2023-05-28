package matt.model.code.om

@JvmInline
value class OmScriptUID(private val value: Long) {
    override fun toString(): String {
        return value.toString()
    }
}