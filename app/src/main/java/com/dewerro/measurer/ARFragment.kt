package com.dewerro.measurer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.ar.Constants
import com.dewerro.measurer.ar.RenderableTextWrapper
import com.dewerro.measurer.ar.RenderableUtils
import com.dewerro.measurer.ar.RenderableUtils.onCreationError
import com.dewerro.measurer.ar.Updatable
import com.dewerro.measurer.databinding.FragmentArBinding
import com.dewerro.measurer.math.VectorMath
import com.dewerro.measurer.math.distance
import com.dewerro.measurer.math.round
import com.dewerro.measurer.math.toPose
import com.google.android.filament.Filament
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ARFragment : Fragment(), Scene.OnUpdateListener {

    private var _binding: FragmentArBinding? = null
    private val binding get() = _binding!!

    private val MIN_OPENGL_VERSION = 3.0
    private val TAG: String = ARFragment::class.java.simpleName

    private var arFragment: ArFragment? = null

    private val placedAnchors = ArrayList<Anchor>()
    private val placedAnchorNodes = ArrayList<AnchorNode>()

    private val updatableElements = ArrayList<Updatable>()

    private var firstPlacedPoint: Node? = null
    private var lastPlacedPoint: Node? = null
    private var placedPoints = 0

    private var shapeHeight = 0.0f
    private var shapeWidth = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArBinding.inflate(inflater, container, false)
        Filament.init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish(activity!!)) {
            Toast.makeText(activity!!.applicationContext, "Device not supported", Toast.LENGTH_LONG)
                .show()
        }

        binding.arToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        arFragment = binding.sceneformFragment.getFragment()

        Toast.makeText(context,
            resources.getString(R.string.find_plane),
            Toast.LENGTH_LONG
        ).show()

        initClearButton()

        binding.arNextButton.setOnClickListener {
            placedPoints = 0

            findNavController().navigate(
                R.id.action_ARFragment_to_MeasureFragment,
                MeasureFragment.BundleFactory.of(shapeWidth, shapeHeight)
            )
        }

        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, _: Plane?, _: MotionEvent? ->
            if(placedPoints >= Constants.maxNumMultiplePoints) return@setOnTapArPlaneListener

            placedPoints++
            placePoint(hitResult)

            if(placedPoints == Constants.maxNumMultiplePoints){
                binding.arNextButton.isEnabled = true
            }
        }
    }

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val openGlVersionString =
            (Objects.requireNonNull(activity
                .getSystemService(Context.ACTIVITY_SERVICE)) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES $MIN_OPENGL_VERSION later")
            Toast.makeText(activity,
                "Sceneform requires OpenGL ES $MIN_OPENGL_VERSION or later",
                Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }

    private fun initClearButton(){
        binding.arClearButton.setOnClickListener { clearAllAnchors() }
    }

    private fun clearAllAnchors(){
        placedAnchors.clear()
        for (anchorNode in placedAnchorNodes){
            arFragment!!.arSceneView.scene.removeChild(anchorNode)
            anchorNode.isEnabled = false
            anchorNode.anchor!!.detach()
            anchorNode.setParent(null)
        }
        placedAnchorNodes.clear()
        updatableElements.clear()

        firstPlacedPoint = null
        lastPlacedPoint = null

        placedPoints = 0

        shapeHeight = 0f
        shapeWidth = 0f

        binding.arNextButton.isEnabled = false
    }

    private fun placeAnchor(hitResult: HitResult, renderable: Renderable){
        val anchor = hitResult.createAnchor()
        placedAnchors.add(anchor)

        val anchorNode = AnchorNode(anchor).apply {
            isSmoothed = true
            setParent(arFragment!!.arSceneView.scene)
        }
        placedAnchorNodes.add(anchorNode)

        if(firstPlacedPoint == null) {
            firstPlacedPoint = anchorNode
        }

        if(lastPlacedPoint != null && (placedPoints == 2 || placedPoints == 3)) {
            val currentPlacedPoints = placedPoints
            placeTextBetween(listOf(anchorNode, lastPlacedPoint!!)){
                val distance = lastPlacedPoint?.worldPosition?.let {
                    anchorNode.worldPosition.distance(it).round(2)
                } ?: return@placeTextBetween "0.0 m."

                if(currentPlacedPoints == 2){
                    shapeHeight = distance
                } else {
                    shapeWidth = distance
                }

                return@placeTextBetween "$distance m."
            }
        }

        lastPlacedPoint = anchorNode

        val node = TransformableNode(arFragment!!.transformationSystem)
            .apply {
                this.rotationController.isEnabled = false
                this.scaleController.isEnabled = false
                this.translationController.isEnabled = true
                this.renderable = renderable
                setParent(anchorNode)
            }

        arFragment!!.arSceneView.scene.addOnUpdateListener(this)
        arFragment!!.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun placeTextBetween(points: List<Node>, onTextUpdate: () -> String) {
        RenderableUtils.createRenderable(context!!, R.layout.point_text_layout) { viewRenderable ->
            val textView = viewRenderable.view as TextView

            val anchorPose = VectorMath.getCentroid(points.map {
                it.worldPosition
            }).toPose()

            val anchor = arFragment!!.arSceneView.session!!.createAnchor(anchorPose)
            placedAnchors.add(anchor)

            val anchorNode = AnchorNode().apply {
                isSmoothed = true
                setParent(arFragment!!.arSceneView.scene)
                renderable = viewRenderable
            }
            placedAnchorNodes.add(anchorNode)

            updatableElements.add(RenderableTextWrapper(anchorNode){
                textView.text = onTextUpdate()
                val newPosition = VectorMath.getCentroid(points.map { it.worldPosition })

                Log.i(TAG, "Updating textView[${textView.text}], position = $newPosition")

                return@RenderableTextWrapper newPosition
            })
        }
    }

    private fun placePoint(hitResult: HitResult){
        MaterialFactory.makeTransparentWithColor(
            context!!,
            Color(android.graphics.Color.WHITE)
        )
            .thenAccept { material: Material? ->
                val sphereRenderable = ShapeFactory.makeSphere(
                    0.02f,
                    Vector3.zero(),
                    material
                )
                sphereRenderable.isShadowCaster = false
                sphereRenderable.isShadowReceiver = false

                placeAnchor(hitResult, sphereRenderable)
            }
            .exceptionally {
                onCreationError(context!!, it)
                return@exceptionally null
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onUpdate(frameTime: FrameTime) {
        updatableElements.forEach { it.onUpdate() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        arFragment?.arSceneView?.session?.close()
        _binding = null
    }
}
