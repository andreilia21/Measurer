package com.dewerro.measurer.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.dewerro.measurer.K.Placeholders.P_AREA
import com.dewerro.measurer.K.Placeholders.P_LENGTH
import com.dewerro.measurer.R
import com.dewerro.measurer.util.math.Vector2d
import com.dewerro.measurer.util.math.VectorMath.getCentroid
import com.dewerro.measurer.util.math.distance
import com.dewerro.measurer.util.math.middlePoint
import com.dewerro.measurer.util.math.round
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

    // Данный метод вызывается при каждом вызове метода invalidate()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Перересовываем так называемый "холст" (canvas)
        canvas?.let { redrawCanvas(it) }
    }

    /**
     * Рисует точки на картинке. Если точек 4, рисует текст ширины, высоты и площади.
     * Также проводит линии между точками и заполняет белым прозрачным цветом.
     * @param canvas "Холст" для рисования
     */
    private fun redrawCanvas(canvas: Canvas) {
        // Создаём инструмент рисования
        val paint = Paint().apply {
            // Ставим исходный цвет
            color = CIRCLE_COLOR
            // Применяем сглаживание, для более качественной картинки
            isAntiAlias = true
        }

        canvas.apply {

            // Рисуем все точки на картинке
            points.forEach {
                drawCircle(it.x, it.y, CIRCLE_RADIUS, paint)
            }

            // Если точек больше 4, рисуем измерения между точками
            if(getPointsAmount() != 4) return@apply

            // Ищем центр между всеми точками
            val middlePoint = getCentroid(points)

            // Инициализируем углы измеренной фигуры
            var leftTopCorner = middlePoint
            var rightTopCorner = middlePoint
            var leftBottomCorner = middlePoint
            var rightBottomCorner = middlePoint

            // Находим левый верхний угол
            points.forEach {
                if (it.x < leftTopCorner.x && it.y < leftTopCorner.y) {
                    leftTopCorner = it
                    return@forEach
                }
            }

            // Находим правый верхний угол
            points.forEach {
                if (it.x > rightTopCorner.x && it.y < rightTopCorner.y) {
                    rightTopCorner = it
                    return@forEach
                }
            }

            // Находим левый нижний угол
            points.forEach {
                if (it.x < leftBottomCorner.x && it.y > leftBottomCorner.y) {
                    leftBottomCorner = it
                    return@forEach
                }
            }

            // Находим правый нижний угол
            points.forEach {
                if (it.x > rightBottomCorner.x && it.y > rightBottomCorner.y) {
                    rightBottomCorner = it
                    return@forEach
                }
            }

            // Создаём инструмент рисования по точкам
            val path: Path = Path().apply {
                // Делаем левый верхний угол исходней точкой
                moveTo(leftTopCorner)
                // Делаем линию из исходной точки - в правый верхний угол
                lineTo(rightTopCorner)
                // Делаем линию из правого верхнего угла - в правый нижний угол
                lineTo(rightBottomCorner)
                // Делаем линию из правого нижнего угла - в левый нижний угол
                lineTo(leftBottomCorner)
                // Делаем линию из левого нижнего угла - в левый верхний угол
                lineTo(leftTopCorner)
            }

            // Устанавливаем стиль рисования "Линейный"
            paint.style = Paint.Style.STROKE

            // Рисуем две центральные линии на сторонах
            drawLine(leftTopCorner.middlePoint(rightTopCorner), leftBottomCorner.middlePoint(rightBottomCorner), paint)
            drawLine(leftTopCorner.middlePoint(leftBottomCorner), rightTopCorner.middlePoint(rightBottomCorner), paint)

            // Делаем ширину линии - 4
            paint.strokeWidth = 4f
            // Рисуем боковые стороны фигуры
            drawPath(path, paint)

            // Создаём прозрачный инструмент рисования
            val transparentPaint = Paint().apply {
                color = Color.argb(0.3f, 1.0f, 1.0f, 1.0f)

                // Устанавливаем стиль "Заливка"
                style = Paint.Style.FILL
            }

            // Делаем заполнение на пути
            path.fillType = Path.FillType.EVEN_ODD

            // Создаём чёрный инструмент рисования для текста
            val textPaint = Paint().apply {
                // Ставим чёрный цвет
                color = Color.BLACK
                // Применяем сглаживание
                isAntiAlias = true
                // Устанавливаем текст посередине
                textAlign = Paint.Align.CENTER
                // Ставим размер текста 18
                textSize = 18f
            }

            // Находим множитель масштаба длин фигуры
            val widthMultiplier = 1 / width.toFloat()
            val heightMultiplier = 1 / height.toFloat()

            // Находим измерения фигуры и округляем до сотых
            _measuredWidth = (leftBottomCorner.distance(rightBottomCorner) * pointLengthRatio * widthMultiplier).round(2)
            _measuredHeight = (rightTopCorner.distance(rightBottomCorner) * pointLengthRatio * heightMultiplier).round(2)

            // Находим площадь фигуры
            val area = (shapeHeight * shapeWidth).round(2)

            // Рисуем прозрачное заполнение
            drawPath(path, transparentPaint)

            // Рисуем текст ширины фигуры
            drawTextOnPath(toLengthString(shapeWidth), Path().apply {
                moveTo(leftBottomCorner)
                lineTo(rightBottomCorner)
            }, 0f, 5f, textPaint)

            // Рисуем текст высоты фигуры
            drawTextOnPath(toLengthString(shapeHeight), Path().apply {
                moveTo(rightTopCorner)
                lineTo(rightBottomCorner)
            }, 5f, 5f, textPaint)

            // Рисуем текст площади фигуры
            drawTextOnPath(toAreaString(area), Path().apply {
                moveTo(leftTopCorner.middlePoint(leftBottomCorner))
                lineTo(rightTopCorner.middlePoint(rightBottomCorner))
            }, 0f, 5f, textPaint)
        }
    }

    /**
     * Преобразует дробное значение длины в строковое, используя локализованный формат
     * 0.5f -> "0.5 м."
     * @param length Длина
     */
    private fun toLengthString(length: Float): String {
        return resources
            .getString(R.string.length_text).replace(P_LENGTH, "$length")
    }

    /**
     * Преобразует дробное значение площади в строковое, используя локализованный формат
     * 0.5f -> "0.5 м²."
     * @param area Площадь
     */
    private fun toAreaString(area: Float): String {
        return resources
            .getString(R.string.area_text).replace(P_AREA, "$area")
    }

    /**
     * Устанавливает масштаб измерений между точками и обновляет картинку, если поставлено 4 точки.
     */
    fun setPointLengthRatio(value: Float){
        pointLengthRatio = value
        if (getPointsAmount() >= 4) {
            invalidate()
        }
    }

    /**
     * Добавляет точку и обновляет картинку для перересовки
     */
    fun addPoint(position: Vector2d) {
        points.add(position)
        invalidate()
    }

    /**
     * Очищает точки на картинке и перересовывает её
     */
    fun clearPoints() {
        points.clear()
        invalidate()
    }

    /**
     * Возвращает количество поставленных точек
     * @return количество поставленных точек
     */
    fun getPointsAmount(): Int {
        return points.size
    }

    private fun Canvas.drawLine(startPoint: Vector2d, endPoint: Vector2d, paint: Paint) {
        drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint)
    }

    private fun Path.moveTo(point: Vector2d) {
        moveTo(point.x, point.y)
    }

    private fun Path.lineTo(point: Vector2d) {
        lineTo(point.x, point.y)
    }
}