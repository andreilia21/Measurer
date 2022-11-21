package com.dewerro.measurer.ar

import android.content.Context
import android.graphics.Color
import com.dewerro.measurer.R
import com.dewerro.measurer.ar.RenderableUtils.createRenderable
import com.dewerro.measurer.ar.RenderableUtils.onCreationError
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*

class ArFragmentPrepareHelper {

    var cubeRenderable: ModelRenderable? = null
    var distanceCardViewRenderable: ViewRenderable? = null

    fun isInitialized(): Boolean {
        return cubeRenderable != null && distanceCardViewRenderable != null
    }

    fun initRenderable(context: Context) {
        MaterialFactory.makeTransparentWithColor(
            context,
            Color(Color.RED)
        )
            .thenAccept { material: Material? ->
                cubeRenderable = ShapeFactory.makeSphere(
                    0.02f,
                    Vector3.zero(),
                    material
                )
                cubeRenderable!!.isShadowCaster = false
                cubeRenderable!!.isShadowReceiver = false
            }
            .exceptionally {
                onCreationError(context, it)
                return@exceptionally null
            }

        createRenderable(context, R.layout.distance_text_layout) { distanceCardViewRenderable = it }
    }

}