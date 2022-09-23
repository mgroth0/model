package matt.model.code

interface Code {

}
interface CodeGenerator<C: Code> {
  val product: C
}