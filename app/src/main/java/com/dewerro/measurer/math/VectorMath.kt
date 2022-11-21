package com.dewerro.measurer.math

import kotlin.math.pow
import kotlin.math.sqrt

fun Vector2d.distance(vector2d: Vector2d): Float {
    val normalized = Vector2d(vector2d.x - this.x, vector2d.y - this.y)

    return normalized.length()
}

fun Vector2d.add(vector2d: Vector2d): Vector2d {
    return Vector2d(x + vector2d.x, y + vector2d.y)
}

fun Vector2d.subtract(vector2d: Vector2d): Vector2d {
    return Vector2d(x - vector2d.x, y - vector2d.y)
}

fun Vector2d.normalize(): Vector2d {
    val xNormalized = if(x != 0f) 1 / x else x
    val yNormalized = if(y != 0f) 1 / y else y

    return Vector2d(xNormalized, yNormalized)
}

fun Vector2d.length(): Float {
    return sqrt(this.x.pow(2) + this.y.pow(2))
}

fun Vector2d.middlePoint(another: Vector2d): Vector2d {
    return Vector2d((another.x + x) / 2, (another.y + y) / 2)
}

object VectorMath {
    fun getCentroid(vectors: List<Vector2d>): Vector2d {
        val pointSize = vectors.size
        var xSum = 0f
        var ySum = 0f

        vectors.forEach {
            xSum += it.x
            ySum += it.y
        }

        return Vector2d(xSum/pointSize, ySum/pointSize)
    }
}