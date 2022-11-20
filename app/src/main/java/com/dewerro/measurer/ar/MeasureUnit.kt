package com.dewerro.measurer.ar

enum class MeasureUnit(
    private val multiplier: Float
) {
    METRES(1.0f),
    CENTIMETRES(100.0f),
    MILLIMETRES(1000.0f),

    ;

    fun convert(metres: Float): Float {
        return metres * multiplier
    }
}