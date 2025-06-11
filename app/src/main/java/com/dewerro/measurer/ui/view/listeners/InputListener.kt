package com.dewerro.measurer.ui.view.listeners

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

abstract class InputListener<T> : TextView.OnEditorActionListener, View.OnFocusChangeListener {

    protected var inputListener: (T) -> Unit = { }
    protected var errorListener: (Throwable) -> Unit = { }
    protected var preprocess: (String) -> String = { it }

    protected abstract fun onInput(view: EditText)

    fun applyListenerTo(@NonNull view: EditText) {
        view.setOnEditorActionListener(this)
        view.setOnFocusChangeListener(this)
    }

    fun preprocessor(preprocess: (String) -> String) {
        this.preprocess = preprocess
    }

    fun onInput(inputListener: (T) -> Unit) {
        this.inputListener = inputListener
    }

    fun onError(errorListener: (Throwable) -> Unit) {
        this.errorListener = errorListener
    }

    override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
            actionId == EditorInfo.IME_ACTION_DONE ||
            event != null &&
            event.action == KeyEvent.ACTION_DOWN &&
            event.keyCode == KeyEvent.KEYCODE_ENTER
        ) {

            if (event == null || !event.isShiftPressed) {
                // the user is done typing.
                (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()

                onInput(view as EditText)

                return true // consume.
            }

        }
        return false
    }

    @Override
    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (!hasFocus) {
            onInput(view as EditText)
        }
    }
}
