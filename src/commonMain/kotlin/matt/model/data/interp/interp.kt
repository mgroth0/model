package matt.model.data.interp

interface BasicInterpolatable<T: Any> {
    /**
     *
     * Matt: Copied from JavaFX so I don't have to depend on JavaFX in low level libraries!
     *
     * The function calculates an interpolated value along the fraction
     * {@code t} between {@code 0.0} and {@code 1.0}. When {@code t} = 1.0,
     * {@code endVal} is returned.
     *
     * @param endValue
     *            target value
     * @param t
     *            fraction between {@code 0.0} and {@code 1.0}
     * @return interpolated value
     */
    fun interpolate(endValue: BasicInterpolatable<*>, t: Double): T
}
