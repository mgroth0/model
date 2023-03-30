package matt.model.obj.path

import kotlin.jvm.JvmName

interface PathLike<P : PathLike<P>> {
    fun resolveNames(names: List<String>): P
    fun relativeNamesFrom(other: P): List<String>
}

class PathMapping<P1 : PathLike<P1>, P2 : PathLike<P2>>(
    val root1: P1,
    val root2: P2
) {
    @JvmName("resolveP1")
    fun resolve(p1Path: P1): P2 {
        val rel = p1Path.relativeNamesFrom(root1)
        return root2.resolveNames(rel)
    }

    @JvmName("resolveP2")
    fun resolve(p2Path: P2): P1 {
        val rel = p2Path.relativeNamesFrom(root2)
        return root1.resolveNames(rel)
    }
}