package matt.model.code.report

interface Reporter {
  fun local(prefix: String): Reporter
}