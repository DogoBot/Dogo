package io.github.dogo.utils

class Holder<T> {
    private var hold: Any? = null

    fun hold() = hold as T
    fun hold(t: T) {
        hold = t
    }
}