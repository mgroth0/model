package matt.model.obj.path

interface PathLike<P: PathLike<P>> {
  fun resolveNames(names: List<String>): P
  fun relativeNamesFrom(other: P): List<String>
}