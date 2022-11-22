package com.dewerro.measurer.math

import com.google.ar.core.Pose
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlin.math.pow
import kotlin.math.sqrt

fun Vector2d.distance(vector2d: Vector2d): Float {
    return vector2d.subtract(this).length()
}

fun Vector2d.add(vector2d: Vector2d): Vector2d {
    return Vector2d(x + vector2d.x, y + vector2d.y)
}

fun Vector2d.subtract(vector2d: Vector2d): Vector2d {
    return Vector2d(x - vector2d.x, y - vector2d.y)
}

fun Vector2d.normalized(): Vector2d {
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

fun Vector3.middlePoint(another: Vector3): Vector3 {
    return Vector3((another.x + x) / 2, (another.y + y) / 2, (another.z + z) / 2)
}

fun Vector3.toPose(quaternion: Quaternion): Pose {
    return PoseUtils.createPose(this, quaternion)
}

fun Vector3.toPose(): Pose {
    val rotationFromAToB = Quaternion.lookRotation(this.normalized(), Vector3.up())

    return PoseUtils.createPose(this, rotationFromAToB)
}

fun Vector3.subtract(vector3: Vector3): Vector3 {
    return Vector3(x - vector3.x, y - vector3.y, z - vector3.z)
}

fun Vector3.distance(vector3: Vector3): Float {
    return vector3.subtract(this).length()
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

    fun getCentroid(vectors: List<Vector3>): Vector3 {
        val pointSize = vectors.size
        var xSum = 0f
        var ySum = 0f
        var zSum = 0f

        vectors.forEach {
            xSum += it.x
            ySum += it.y
            zSum += it.z
        }

        return Vector3(xSum/pointSize, ySum/pointSize, zSum/pointSize)
    }
}