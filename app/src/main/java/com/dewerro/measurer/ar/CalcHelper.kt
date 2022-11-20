package com.dewerro.measurer.ar

import com.google.ar.core.Pose
import com.google.ar.sceneform.math.Vector3
import kotlin.math.pow
import kotlin.math.sqrt

object CalcHelper {

    fun calculateDistance(x: Float, y: Float, z: Float): Float{
        return sqrt(x.pow(2) + y.pow(2) + z.pow(2))
    }

    fun calculateDistance(objectPose0: Pose, objectPose1: Pose): Float{
        return calculateDistance(
            objectPose0.tx() - objectPose1.tx(),
            objectPose0.ty() - objectPose1.ty(),
            objectPose0.tz() - objectPose1.tz())
    }


    fun calculateDistance(objectPose0: Vector3, objectPose1: Pose): Float{
        return calculateDistance(
            objectPose0.x - objectPose1.tx(),
            objectPose0.y - objectPose1.ty(),
            objectPose0.z - objectPose1.tz()
        )
    }

    fun calculateDistance(objectPose0: Vector3, objectPose1: Vector3): Float{
        return calculateDistance(
            objectPose0.x - objectPose1.x,
            objectPose0.y - objectPose1.y,
            objectPose0.z - objectPose1.z
        )
    }

}