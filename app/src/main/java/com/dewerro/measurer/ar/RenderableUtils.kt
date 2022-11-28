package com.dewerro.measurer.ar

import android.app.AlertDialog
import android.content.Context
import com.google.ar.sceneform.rendering.ViewRenderable

object RenderableUtils {

    fun onCreationError(context: Context, throwable: Throwable) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(throwable.message).setTitle("Error")
        val dialog = builder.create()
        dialog.show()
    }

    fun createRenderable(context: Context, layoutId: Int, thenAccept: (ViewRenderable) -> Unit) {
        ViewRenderable
            .builder()
            .setView(context, layoutId)
            .build()
            .thenAccept {
                it.isShadowCaster = false
                it.isShadowReceiver = false
                thenAccept(it)
            }
            .exceptionally {
                onCreationError(context, it)
                return@exceptionally null
            }
    }

}