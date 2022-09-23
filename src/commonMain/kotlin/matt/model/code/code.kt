package matt.model.code

interface Code<C: Code<C>> {
  val code: String
  fun formatted(): C
}
interface CodeGenerator<C: Code<C>> {
  fun generate(): C
}