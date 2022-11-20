package com.dewerro.measurer.ar

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.dewerro.measurer.Constants
import com.dewerro.measurer.R
import com.dewerro.measurer.ar.RenderableUtils.createRenderable
import com.dewerro.measurer.ar.RenderableUtils.onCreationError
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*

class ArFragmentPrepareHelper {

    private lateinit var arrow1UpLinearLayout: LinearLayout
    private lateinit var arrow1DownLinearLayout: LinearLayout
    private lateinit var arrow1UpView: ImageView
    private lateinit var arrow1DownView: ImageView
    private lateinit var arrow1UpRenderable: Renderable
    private lateinit var arrow1DownRenderable: Renderable

    private lateinit var arrow10UpLinearLayout: LinearLayout
    private lateinit var arrow10DownLinearLayout: LinearLayout
    private lateinit var arrow10UpView: ImageView
    private lateinit var arrow10DownView: ImageView
    private lateinit var arrow10UpRenderable: Renderable
    private lateinit var arrow10DownRenderable: Renderable

    var cubeRenderable: ModelRenderable? = null
    var distanceCardViewRenderable: ViewRenderable? = null

    fun isInitialized(): Boolean {
        return cubeRenderable != null && distanceCardViewRenderable != null
    }

    fun initArrowView(context: Context) {
        arrow1UpLinearLayout = LinearLayout(context)
        arrow1UpLinearLayout.orientation = LinearLayout.VERTICAL
        arrow1UpLinearLayout.gravity = Gravity.CENTER
        arrow1UpView = ImageView(context)
        arrow1UpView.setImageResource(R.drawable.arrow_1up)
        arrow1UpLinearLayout.addView(arrow1UpView,
            Constants.arrowViewSize,
            Constants.arrowViewSize)

        arrow1DownLinearLayout = LinearLayout(context)
        arrow1DownLinearLayout.orientation = LinearLayout.VERTICAL
        arrow1DownLinearLayout.gravity = Gravity.CENTER
        arrow1DownView = ImageView(context)
        arrow1DownView.setImageResource(R.drawable.arrow_1down)
        arrow1DownLinearLayout.addView(arrow1DownView,
            Constants.arrowViewSize,
            Constants.arrowViewSize)

        arrow10UpLinearLayout = LinearLayout(context)
        arrow10UpLinearLayout.orientation = LinearLayout.VERTICAL
        arrow10UpLinearLayout.gravity = Gravity.CENTER
        arrow10UpView = ImageView(context)
        arrow10UpView.setImageResource(R.drawable.arrow_10up)
        arrow10UpLinearLayout.addView(arrow10UpView,
            Constants.arrowViewSize,
            Constants.arrowViewSize)

        arrow10DownLinearLayout = LinearLayout(context)
        arrow10DownLinearLayout.orientation = LinearLayout.VERTICAL
        arrow10DownLinearLayout.gravity = Gravity.CENTER
        arrow10DownView = ImageView(context)
        arrow10DownView.setImageResource(R.drawable.arrow_10down)
        arrow10DownLinearLayout.addView(arrow10DownView,
            Constants.arrowViewSize,
            Constants.arrowViewSize)
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
        createRenderable(context, arrow1UpLinearLayout) { arrow1UpRenderable = it }
        createRenderable(context, arrow1DownLinearLayout) { arrow1DownRenderable = it }
        createRenderable(context, arrow10UpLinearLayout) { arrow10UpRenderable = it }
        createRenderable(context, arrow10DownLinearLayout) { arrow10DownRenderable = it }
    }

}