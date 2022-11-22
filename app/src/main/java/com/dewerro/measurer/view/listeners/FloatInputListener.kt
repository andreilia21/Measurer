package com.dewerro.measurer.view.listeners;

import android.widget.EditText

class FloatInputListener : InputListener<Float>() {

    override fun onInput(view: EditText) {
        var str = view.text.toString()

        str = preprocess(str)

        try {
            inputListener(str.toFloat())
        } catch (e: NumberFormatException) {
            errorListener(e)
        }
    }
}
