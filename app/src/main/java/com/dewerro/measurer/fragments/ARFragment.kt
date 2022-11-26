package com.dewerro.measurer.fragments

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.R
import com.dewerro.measurer.ar.Constants
import com.dewerro.measurer.ar.Constants.MIN_OPENGL_VERSION
import com.dewerro.measurer.ar.RenderableTextWrapper
import com.dewerro.measurer.ar.RenderableUtils
import com.dewerro.measurer.ar.RenderableUtils.onCreationError
import com.dewerro.measurer.ar.Updatable
import com.dewerro.measurer.databinding.FragmentArBinding
import com.dewerro.measurer.util.math.VectorMath
import com.dewerro.measurer.util.math.distance
import com.dewerro.measurer.util.math.round
import com.dewerro.measurer.util.math.toPose
import com.google.android.filament.Filament
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.*

class ARFragment : Fragment(), Scene.OnUpdateListener {

    private val debugTag: String = ARFragment::class.java.simpleName

    private var _binding: FragmentArBinding? = null
    private val binding get() = _binding!!

    private var arFragment: ArFragment? = null

    private val placedAnchors = ArrayList<Anchor>()
    private val placedAnchorNodes = ArrayList<AnchorNode>()

    private val updatableElements = ArrayList<Updatable>()

    private var firstPlacedPoint: Node? = null
    private var lastPlacedPoint: Node? = null
    private var placedPoints = 0

    private var shapeHeight = 0.0f
    private var shapeWidth = 0.0f

    private var cachedLengthText: String? = null
    private var cachedShapeAreaText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArBinding.inflate(inflater, container, false)
        Filament.init()
        return binding.root
    }

    // Данный метод вызывается при запуске фрагмента.
    // В этом методе инициализируются все виджеты данного фрагмента.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверяем, поддерживает устройство дополненную реальность или нет
        // Если нет, закрываем фрагмент
        checkIsSupportedDeviceOrFinish(activity!!)

        // Инициализируем виджеты
        initToolbar()
        initClearButton()
        initNextButton()
        initArFragment()

        // Показываем подсказку
        showTooltip()
    }

    /**
     * Проверяет поддерживает ли устройство дополненную реальность
     * @param activity активити для получения информации об устройстве
     */
    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val openGlVersionString =
            (Objects.requireNonNull(activity
                .getSystemService(Context.ACTIVITY_SERVICE)) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(debugTag, "Sceneform requires OpenGL ES $MIN_OPENGL_VERSION later")
            Toast.makeText(activity,
                "Sceneform requires OpenGL ES $MIN_OPENGL_VERSION or later",
                Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }

    /**
     * Инициализирует верхнюю панель во фрагменте.
     * При нажатии кнопки назад возвращает в предыдущий фрагмент
     */
    private fun initToolbar() {
        binding.arToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    /**
     * Инициализирует кнопку очистки.
     * При нажатии кнопки вызывает обработчик события
     * @see onClear
     */
    private fun initClearButton() {
        binding.arClearButton.setOnClickListener { onClear() }
    }

    /**
     * Обрабатывает событие нажатия на кнопку "Очистить".
     */
    private fun onClear() {
        clearPlacedObjects()

        firstPlacedPoint = null
        lastPlacedPoint = null

        shapeHeight = 0f
        shapeWidth = 0f

        disableNextButton()
        hideAreaTextView()
    }

    /**
     * Включает кнопку "Далее".
     * Позволяет пользователю нажимать на кнопку.
     */
    private fun enableNextButton() {
        binding.arNextButton.isEnabled = true
    }

    /**
     * Выключает кнопку "Далее".
     * В выключенном состоянии при нажатии на кнопку ничего происходить не будет.
     */
    private fun disableNextButton() {
        binding.arNextButton.isEnabled = false
    }

    /**
     * Отображает виджет площади.
     */
    private fun showAreaTextView() {
        binding.arAreaTextView.visibility = View.VISIBLE
    }

    /**
     * Скрывает виджет площади.
     */
    private fun hideAreaTextView() {
        binding.arAreaTextView.visibility = View.GONE
    }

    /**
     * Инициализирует кнопку "Далее".
     * При нажатии кнопки отправляет во фрагмент MeasureFragment
     * @see MeasureFragment
     */
    private fun initNextButton() {
        binding.arNextButton.setOnClickListener {

            // Нужно для избежания проблем с возвращением во фрагмент
            placedPoints = 0

            // Отправляем во фрагмент MeasureFragment
            findNavController().navigate(
                R.id.action_ARFragment_to_MeasureFragment,
                MeasureFragment.BundleFactory.of(shapeWidth, shapeHeight)
            )
        }
    }

    /**
     * Инициализирует виджет дополненной реальности.
     * При нажатии на экран вызывается обработчик этого события.
     * @see onTap
     */
    private fun initArFragment() {
        arFragment = binding.sceneformFragment.getFragment()

        // Устанавливает обработчик события
        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, _: Plane?, _: MotionEvent? ->
            // Данный вызов метода обрабатывает HitResult
            onTap(hitResult)
        }
    }

    /**
     * Обрабатывает нажатие на виджет дополненной реальности.
     * Если количество поставленных точек не превышает лимит (4 штуки)
     * ставит точку в виджете дополненной реальности.
     * @param hitResult Данные нажатия на виджет
     */
    private fun onTap(hitResult: HitResult) {
        // Если поставлено 4 точки, отменяем обработку события.
        if(placedPoints >= Constants.maxNumMultiplePoints) return

        // Увеличиваем счётчик поставленных точек
        placedPoints++

        // Ставим точку
        placePoint(hitResult)

        // Если поставлено 4 точки, включаем кнопку "Далее"
        // И показываем текстовый виджет площади
        if(placedPoints == Constants.maxNumMultiplePoints) {
            enableNextButton()
            showAreaTextView()
        }
    }

    /**
     * Показывает подсказку "Найдите поверхность и поставьте 4 точки"
     */
    private fun showTooltip() {
        makeToast(
            resources.getString(R.string.find_plane)
        ).show()
    }

    /**
     * Создаёт Toast-сообщение с указанным сообщением.
     * @param message сообщение для показа
     */
    private fun makeToast(message: String): Toast {
        return Toast.makeText(context,
            message,
            Toast.LENGTH_LONG
        )
    }

    /**
     * Очищает все поставленные объекты в виджете дополненной реальности.
     */
    private fun clearPlacedObjects() {
        updatableElements.clear()
        placedAnchors.clear()

        for (anchorNode in placedAnchorNodes){
            arFragment!!.arSceneView.scene.removeChild(anchorNode)
            anchorNode.isEnabled = false
            anchorNode.anchor?.detach()
            anchorNode.setParent(null)
        }

        placedAnchorNodes.clear()

        placedPoints = 0
    }

    /**
     * Устанавливает точку на плоскость с моделью белой сферы
     * @param hitResult необходим для получения позиции для установки точки
     */
    private fun placePoint(hitResult: HitResult) {
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

                placePoint(hitResult, sphereRenderable)
            }
            .exceptionally {
                onCreationError(context!!, it)
                return@exceptionally null
            }
    }

    /**
     * Устанавливает точку на плоскость
     * @param hitResult необходим для получения позиции для установки точки
     * @param renderable отображаемая модель точки
     */
    private fun placePoint(hitResult: HitResult, renderable: Renderable) {
        // Устанавливаем исходное положение точки
        val placedPosition = hitResult.createAnchor()
        placedAnchors.add(placedPosition)

        // Создаём точку в позиции placedPosition
        val placedPoint = AnchorNode(placedPosition).apply {
            isSmoothed = true
            setParent(arFragment!!.arSceneView.scene)
        }

        placedAnchorNodes.add(placedPoint)

        // Если это первая точка, устанавливаем её первой поставленной
        if(firstPlacedPoint == null) {
            firstPlacedPoint = placedPoint
        }

        // Если поставлено две точки, создаём текст длины
        if(placedPoints == 2) {
            placeTextBetweenTwoPoints(placedPoint, lastPlacedPoint) {
                shapeHeight = it
            }
        }

        // Если поставлено три точки, создаём текст ширины
        if(placedPoints == 3) {
            placeTextBetweenTwoPoints(placedPoint, lastPlacedPoint) {
                shapeWidth = it
            }
        }

        // Делаем точку последней поставленной
        lastPlacedPoint = placedPoint

        // Создаём внешний вид точки
        val node = TransformableNode(arFragment!!.transformationSystem)
            .apply {
                this.rotationController.isEnabled = false
                this.scaleController.isEnabled = false
                this.translationController.isEnabled = true
                this.renderable = renderable // Используем данную модель
                setParent(placedPoint)
            }

        arFragment!!.arSceneView.scene.addOnUpdateListener(this)
        arFragment!!.arSceneView.scene.addChild(placedPoint)
        node.select()
    }

    /**
     * Создаёт текст расстояния между двумя точками
     * @param firstPoint Первая точка
     * @param secondPoint Вторая точка
     * @param onDistanceUpdate Лямбда-выражение, вызывающееся при обновлении длины между точками.
     */
    private fun placeTextBetweenTwoPoints(firstPoint: Node?, secondPoint: Node?, onDistanceUpdate: (Float) -> Unit) {
        if(firstPoint == null) return
        if(secondPoint == null) return

        val pointRotation = firstPlacedPoint!!.worldRotation

        placeTextBetween(listOf(firstPoint, secondPoint), pointRotation) {
            // Данный блок кода вызывается на каждом кадре

            // Считаем длину между двумя точками
            val distance = secondPoint.worldPosition?.let {
                firstPoint.worldPosition.distance(it).round(2)
            } ?: 0.0f

            onDistanceUpdate(distance)
            return@placeTextBetween toLengthString(distance)
        }
    }

    /**
     * Создаёт текст расстояния между несколькими точками
     * @param points Список нескольких точек
     * @param quaternion Поворот виджета текста в пространстве
     * @param onTextUpdate Лямбда-выражение, вызывающееся при обновлении текста.
     */
    private fun placeTextBetween(points: List<Node>, quaternion: Quaternion, onTextUpdate: () -> String) {

        // Создаём виджет текста
        RenderableUtils.createRenderable(context!!, R.layout.point_text_layout) { viewRenderable ->
            // Данный блок кода вызывается, когда виджет был создан

            val textView = viewRenderable.view as TextView

            // Находим среднюю точку между всеми точками
            val anchorPose = VectorMath.getCentroid(points.map {
                it.worldPosition
            }).toPose(quaternion)

            // Создаём позицию в средней точке
            val anchor = arFragment!!.arSceneView.session!!.createAnchor(anchorPose)
            placedAnchors.add(anchor)

            // Создаём внешний вид текста
            val anchorNode = AnchorNode().apply {
                isSmoothed = true
                setParent(arFragment!!.arSceneView.scene)
                renderable = viewRenderable
            }
            placedAnchorNodes.add(anchorNode)

            // Делаем так, чтобы текст обновлялся на каждом кадре
            updatableElements.add(RenderableTextWrapper(anchorNode) {
                // Данный блок кода вызывается на каждом кадре

                textView.text = onTextUpdate()

                // Находим новую позицию текстового виджета, в случае,
                // если исходные точки были смещены
                return@RenderableTextWrapper VectorMath.getCentroid(points.map { it.worldPosition })
            })
        }
    }

    /**
     * Данный метод вызывается на каждом кадре.
     * Необходим для обновления текста дистанций между точками и информации о площади
     */
    override fun onUpdate(frameTime: FrameTime) {
        // Обновляем все зарегистрированные обновляемые элементы
        updatableElements.forEach { it.onUpdate() }

        // Если виджет площади отображён, обновляем площадь.
        if(binding.arAreaTextView.visibility == View.VISIBLE) {
            // Находим новую площадь и округляем до сотых
            val roundedShapeArea = (shapeWidth * shapeHeight).round(2)

            // Обновляем виджет площади
            updateAreaTextView(roundedShapeArea)
        }
    }

    /**
     * Обновляет текст виджета площади
     * @param area Площадь
     */
    private fun updateAreaTextView(area: Float) {
        binding.arAreaTextView.text = toAreaString(area)
    }

    /**
     * Преобразует дробное значение длины в строковое, используя локализованный формат
     * 0.5f -> "0.5 м."
     * @param length Длина
     */
    private fun toLengthString(length: Float): String {
        if(cachedLengthText == null) {
            cachedLengthText = resources.getString(R.string.length_text)
        }

        return cachedLengthText!!.replace("%length%", "$length")
    }

    /**
     * Преобразует дробное значение площади в строковое, используя локализованный формат
     * 0.5f -> "0.5 м²."
     * @param area Площадь
     */
    private fun toAreaString(area: Float): String {
        if(cachedShapeAreaText == null) {
            cachedShapeAreaText = resources.getString(R.string.shape_area_text)
        }

        return cachedShapeAreaText!!.replace("%area%", "$area")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        arFragment?.arSceneView?.session?.close()
        _binding = null
    }
}
