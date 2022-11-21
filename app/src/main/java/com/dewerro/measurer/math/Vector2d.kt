package com.dewerro.measurer.math

data class Vector2d(
    val x: Float,
    val y: Float
)

fun Vector2d.toMutable(): MutableVector2d {
    return MutableVector2d(x, y)
}
