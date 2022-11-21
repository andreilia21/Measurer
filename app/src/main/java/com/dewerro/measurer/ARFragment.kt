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
import com.dewerro.measurer.ar.ArFragmentPrepareHelper
import com.dewerro.measurer.ar.Constants
import com.dewerro.measurer.ar.RenderableUtils.createRenderable
import com.dewerro.measurer.databinding.FragmentArBinding
import com.google.android.filament.Filament
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
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

    private lateinit var pointTextView: TextView

    private val placedAnchors = ArrayList<Anchor>()
    private val placedAnchorNodes = ArrayList<AnchorNode>()
    private val fromGroundNodes = ArrayList<List<Node>>()

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

        val prepareHelper = ArFragmentPrepareHelper()

        prepareHelper.initArrowView(context!!)
        prepareHelper.initRenderable(context!!)

        initClearButton()

        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, _: Plane?, _: MotionEvent? ->
            if (!prepareHelper.isInitialized()) return@setOnTapArPlaneListener

            placePoint(hitResult)
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
        fromGroundNodes.clear()
    }

    private fun placeAnchor(hitResult: HitResult, renderable: Renderable){
        val anchor = hitResult.createAnchor()
        placedAnchors.add(anchor)

        val anchorNode = AnchorNode(anchor).apply {
            isSmoothed = true
            setParent(arFragment!!.arSceneView.scene)
        }
        placedAnchorNodes.add(anchorNode)

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

    private fun placePoint(hitResult: HitResult){
        if (placedAnchorNodes.size >= Constants.maxNumMultiplePoints){
            return
        }

        createRenderable(context!!, R.layout.point_text_layout){
            pointTextView = it.view as TextView
            pointTextView.text = (placedAnchors.size + 1).toString()
            placeAnchor(hitResult, it)
        }

        Log.i(TAG, "Number of anchors: ${placedAnchorNodes.size}")
    }

    @SuppressLint("SetTextI18n")
    override fun onUpdate(frameTime: FrameTime) {
        measureMultipleDistances()
    }

    private fun measureMultipleDistances(){
        if (placedAnchorNodes.size > 5){
            // TODO Добавить расстояние на экране между всеми поставленными точками, и площадь по центру
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        arFragment?.arSceneView?.session?.close()
        _binding = null
    }
}
