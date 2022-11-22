package com.dewerro.measurer.math

import kotlin.math.pow

fun Float.round(precision: Int): Float {
    val pr = 10.0.pow(precision).toFloat()
    val n = this * pr
    return n.toInt().toFloat() / pr
}