package io.github.dogo.utils

/**
 * Simple container to hold Objects of the type [T].
 *
 * @author NathanPB
 * @since 3.1.0
 */
class Holder<T> {
    private var hold: Any? = null

    /**
     * Get the contained value.
     * @return the contained value.
     */
    fun hold() = hold as T

    /**
     * Holds the value [t].
     *
     * @param[t] the value to hold.
     */
    fun hold(t: T) {
        hold = t
    }
}