package matt.model.data.tuple


fun <T> List<T>.verifyToQuad(): UniformQuad<T> {
    check(size == 4)
    return Quad(this[0], this[1], this[2], this[3])
}

typealias UniformQuad<T> = Quad<T, T, T, T>

data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) {

    override fun toString(): String = "($first, $second, $third, ${fourth})"

}


fun <T> UniformQuad<T>.toList() = listOf(first, second, third, fourth)


fun <T, R> UniformQuad<T>.map(op: (T) -> R): UniformQuad<R> = Quad(op(first), op(second), op(third), op(fourth))