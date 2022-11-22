package com.dewerro.measurer.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.dewerro.measurer.R
import com.dewerro.measurer.math.Vector2d
import com.dewerro.measurer.math.VectorMath.getCentroid
import com.dewerro.measurer.math.distance
import com.dewerro.measurer.math.middlePoint
import com.dewerro.measurer.math.round
import com.google.android.material.imageview.ShapeableImageView

class MeasurerImageView : ShapeableImageView {

    companion object {
        private const val CIRCLE_RADIUS = 6f
        private const val CIRCLE_COLOR = Color.WHITE
    }

    private val points = mutableListOf<Vector2d>()
    private var pointLengthRatio = 1.0f

    private var _measuredWidth: Float = 0.0f
    val shapeWidth get() = _measuredWidth

    private var _measuredHeight: Float = 0.0f
    val shapeHeight get() = _measuredHeight

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, value: Int) : super(context, attributeSet, value)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            val paint = Paint().apply {
                color = CIRCLE_COLOR
                isAntiAlias = true
            }

            points.forEach {
                drawCircle(it.x, it.y, CIRCLE_RADIUS, paint)
            }

            if(getPointsAmount() != 4) return@apply

            val middlePoint = getCentroid(points)

            var leftTopCorner = middlePoint
            var rightTopCorner = middlePoint
            var leftBottomCorner = middlePoint
            var rightBottomCorner = middlePoint

            points.forEach {
                if(it.x < leftTopCorner.x && it.y < leftTopCorner.y){
                    leftTopCorner = it
                    return@forEach
                }
            }
            points.forEach {
                if(it.x > rightTopCorner.x && it.y < rightTopCorner.y){
                    rightTopCorner = it
                    return@forEach
                }
            }
            points.forEach {
                if(it.x < leftBottomCorner.x && it.y > leftBottomCorner.y){
                    leftBottomCorner = it
                    return@forEach
                }
            }
            points.forEach {
                if(it.x > rightBottomCorner.x && it.y > rightBottomCorner.y){
                    rightBottomCorner = it
                    return@forEach
                }
            }
            val path: Path = Path().apply {
                moveTo(leftTopCorner)
                lineTo(rightTopCorner)
                lineTo(rightBottomCorner)
                lineTo(leftBottomCorner)
                lineTo(leftTopCorner)
            }

            paint.style = Paint.Style.STROKE

            drawLine(leftTopCorner.middlePoint(rightTopCorner), leftBottomCorner.middlePoint(rightBottomCorner), paint)
            drawLine(leftTopCorner.middlePoint(leftBottomCorner), rightTopCorner.middlePoint(rightBottomCorner), paint)

            paint.strokeWidth = 4f
            drawPath(path, paint)

            val transparentPaint = Paint().apply {
                color = Color.argb(0.3f, 1.0f, 1.0f, 1.0f)
                style = Paint.Style.FILL
            }

            path.fillType = Path.FillType.EVEN_ODD

            val textPaint = Paint().apply {
                color = Color.BLACK
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                textSize = 18f
            }

            val widthMultiplier = 1 / width.toFloat()
            val heightMultiplier = 1 / height.toFloat()

            _measuredWidth = (leftBottomCorner.distance(rightBottomCorner) * pointLengthRatio * widthMultiplier).round(2)
            _measuredHeight = (rightTopCorner.distance(rightBottomCorner) * pointLengthRatio * heightMultiplier).round(2)
            val area = (shapeHeight * shapeWidth).round(2)

            drawPath(path, transparentPaint)
            drawTextOnPath(toLengthString(shapeWidth), Path().apply {
                moveTo(leftBottomCorner)
                lineTo(rightBottomCorner)
            }, 0f, 5f, textPaint)
            drawTextOnPath(toLengthString(shapeHeight), Path().apply {
                moveTo(rightTopCorner)
                lineTo(rightBottomCorner)
            }, 5f, 5f, textPaint)
            drawTextOnPath(toAreaString(area), Path().apply {
                moveTo(leftTopCorner.middlePoint(leftBottomCorner))
                lineTo(rightTopCorner.middlePoint(rightBottomCorner))
            }, 0f, 5f, textPaint)
        }
    }

    private fun toLengthString(length: Float): String {
        return resources
            .getString(R.string.length_text).replace("%length%", "$length")
    }

    private fun toAreaString(area: Float): String {
        return resources
            .getString(R.string.area_text).replace("%area%", "$area")
    }

    fun setPointLengthRatio(value: Float){
        pointLengthRatio = value
        if(getPointsAmount() >= 4){
            invalidate()
        }
    }

    fun addPoint(position: Vector2d) {
        points.add(position)
        invalidate()
    }

    fun clearPoints() {
        points.clear()
        invalidate()
    }

    fun getPointsAmount(): Int {
        return points.size
    }

    private fun Canvas.drawLine(startPoint: Vector2d, endPoint: Vector2d, paint: Paint) {
        drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint)
    }

    private fun Path.moveTo(point: Vector2d){
        moveTo(point.x, point.y)
    }

    private fun Path.lineTo(point: Vector2d){
        lineTo(point.x, point.y)
    }
}