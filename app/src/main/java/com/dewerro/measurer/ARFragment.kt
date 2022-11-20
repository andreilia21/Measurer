package com.dewerro.measurer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color.WHITE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.dewerro.measurer.ar.ArFragmentPrepareHelper
import com.dewerro.measurer.ar.CalcHelper.calculateDistance
import com.dewerro.measurer.ar.MeasureUnit
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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val MIN_OPENGL_VERSION = 3.0
    private val TAG: String = ARFragment::class.java.simpleName

    private var arFragment: ArFragment? = null

    private lateinit var pointTextView: TextView
    private lateinit var multipleDistanceTableLayout: TableLayout

    private val placedAnchors = ArrayList<Anchor>()
    private val placedAnchorNodes = ArrayList<AnchorNode>()
    private val fromGroundNodes = ArrayList<List<Node>>()

    private val multipleDistances = Array(Constants.maxNumMultiplePoints) {
        Array<TextView?>(Constants.maxNumMultiplePoints) {
            null
        }
    }
    private lateinit var initCM: String

    private lateinit var clearButton: Button

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

        arFragment = binding.sceneformFragment.getFragment()
        multipleDistanceTableLayout = binding.multipleDistanceTable

        initCM = resources.getString(R.string.initCM)

        initDistanceTable()

        Toast.makeText(context,
            "Find plane and tap 4 points",
            Toast.LENGTH_LONG)
            .show()

        val prepareHelper = ArFragmentPrepareHelper()

        prepareHelper.initArrowView(context!!)
        prepareHelper.initRenderable(context!!)

        initClearButton()

        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, _: Plane?, _: MotionEvent? ->
            if (!prepareHelper.isInitialized()) return@setOnTapArPlaneListener

            tapDistanceOfMultiplePoints(hitResult)
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

    private fun initDistanceTable() {
        val layoutParams = multipleDistanceTableLayout.layoutParams
        layoutParams.height = Constants.multipleDistanceTableHeight
        multipleDistanceTableLayout.layoutParams = layoutParams

        for (i in 0 until Constants.maxNumMultiplePoints+1) {
            val tableRow = TableRow(context)
            multipleDistanceTableLayout.addView(tableRow,
                multipleDistanceTableLayout.width,
                Constants.multipleDistanceTableHeight / (Constants.maxNumMultiplePoints + 1)
            )
            for (j in 0 until Constants.maxNumMultiplePoints+1) {
                val textView = TextView(context)
                textView.setTextColor(WHITE)
                if (i==0) {
                    if (j==0) {
                        textView.text = "cm"
                    }
                    else {
                        textView.text = (j-1).toString()
                    }
                }
                else {
                    if (j==0) {
                        textView.text = (i-1).toString()
                    }
                    else if(i==j) {
                        textView.text = "-"
                        multipleDistances[i-1][j-1] = textView
                    }
                    else {
                        textView.text = initCM
                        multipleDistances[i-1][j-1] = textView
                    }
                }
                tableRow.addView(textView,
                    tableRow.layoutParams.width / (Constants.maxNumMultiplePoints + 1),
                    tableRow.layoutParams.height)
            }
        }
    }

    private fun initClearButton(){
        clearButton = binding.clearButton
        clearButton.setOnClickListener { clearAllAnchors() }
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
        for (i in 0 until Constants.maxNumMultiplePoints){
            for (j in 0 until Constants.maxNumMultiplePoints){
                if (multipleDistances[i][j] != null){
                    multipleDistances[i][j]!!.text = if(i==j) "-" else initCM
                }
            }
        }
        fromGroundNodes.clear()
    }

    private fun placeAnchor(hitResult: HitResult,
                            renderable: Renderable){
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

    private fun tapDistanceOfMultiplePoints(hitResult: HitResult){
        if (placedAnchorNodes.size >= Constants.maxNumMultiplePoints){
            return
        }

        createRenderable(context!!, R.layout.point_text_layout){
            pointTextView = it.view as TextView
            pointTextView.text = placedAnchors.size.toString()
            placeAnchor(hitResult, it)
        }
        Log.i(TAG, "Number of anchors: ${placedAnchorNodes.size}")
    }

    @SuppressLint("SetTextI18n")
    override fun onUpdate(frameTime: FrameTime) {
        measureMultipleDistances()
    }

    private fun measureMultipleDistances(){
        if (placedAnchorNodes.size > 1){
            for (i in 0 until placedAnchorNodes.size){
                for (j in i+1 until placedAnchorNodes.size){
                    val distanceMeter = calculateDistance(
                        placedAnchorNodes[i].worldPosition,
                        placedAnchorNodes[j].worldPosition
                    )
                    val distanceCM = MeasureUnit.CENTIMETRES.convert(distanceMeter)
                    val distanceCMFloor = "%.2f".format(distanceCM)
                    multipleDistances[i][j]!!.text = distanceCMFloor
                    multipleDistances[j][i]!!.text = distanceCMFloor
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
