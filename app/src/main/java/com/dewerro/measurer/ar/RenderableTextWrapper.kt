package com.dewerro.measurer.ar

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3

class RenderableTextWrapper(
    private val node: Node,
    private val positionUpdater: () -> Vector3
) : Updatable {

    override fun onUpdate() {
        node.worldPosition = positionUpdater()
    }

}