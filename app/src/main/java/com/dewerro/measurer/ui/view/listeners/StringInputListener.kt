package com.dewerro.measurer.ui.view.listeners

import android.widget.EditText

class StringInputListener : InputListener<String>() {

    override fun onInput(view: EditText) {
        val str = view.text.toString()

        inputListener(preprocess(str))
    }
}
