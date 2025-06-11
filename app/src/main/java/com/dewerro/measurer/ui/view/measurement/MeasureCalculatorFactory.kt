package com.dewerro.measurer.ui.view.measurement

import android.content.Context

/**
 * Создаёт виджет подсчёта измерений объекта относительно приведенного типа.
 * Реализует паттерн Simple Factory (простая фабрика)
 */
interface MeasureCalculatorFactory {

    fun createCalculatorView(context: Context, orderType: String): MeasureCalculatorView

}