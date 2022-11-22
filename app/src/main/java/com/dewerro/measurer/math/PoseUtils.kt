package com.dewerro.measurer.math

import com.google.ar.core.Pose
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3

object PoseUtils {
    fun createPose(position: Vector3, quaternion: Quaternion): Pose {
        val positionArray = floatArrayOf(position.x, position.y, position.z)
        val quaternionArray = floatArrayOf(quaternion.x, quaternion.y, quaternion.z, quaternion.w)

        return Pose(positionArray, quaternionArray)
    }
}
