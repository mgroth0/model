package matt.model.obj.text

interface HasText {
  val text: String
}

interface WritableText: HasText {
  override var text: String
}