package matt.model.report

interface Reporter {
  fun local(prefix: String): Reporter
}