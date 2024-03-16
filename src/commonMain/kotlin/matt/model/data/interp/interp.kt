package matt.model.data.interp

interface BasicInterpolatator<T> {
    /**
     *
     * Matt: Based on JavaFX so I don't have to depend on JavaFX in low level libraries!
     *
     * The function calculates an interpolated value along the fraction
     * {@code t} between {@code 0.0} and {@code 1.0}. When {@code t} = 1.0,
     * {@code endVal} is returned.
     *
     * @param end
     *            target value
     * @param fraction
     *            fraction between {@code 0.0} and {@code 1.0}
     * @return interpolated value
     */
    fun interpolate(
        start: T,
        end: T,
        fraction: Double
    ): T
}
