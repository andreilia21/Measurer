package com.dewerro.measurer.util.math

import kotlin.math.pow

// Округляет дробное число с заданным количеством знаков после запятой
fun Float.round(precision: Int): Float {
    val pr = 10.0.pow(precision).toFloat()
    val n = this * pr
    return n.toInt().toFloat() / pr
}