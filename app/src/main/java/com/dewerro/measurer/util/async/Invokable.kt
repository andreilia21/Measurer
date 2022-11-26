package com.dewerro.measurer.util.async

import java.util.*

class Invokable<T> {

    private val actions = LinkedList<(T) -> Unit>()

    fun addAction(action: (T) -> Unit) {
        actions.add(action)
    }

    fun invoke(value: T) {
        actions.forEach { it(value) }
    }

}