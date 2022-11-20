package com.dewerro.measurer.view;

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment;

class MeasurerArFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {
        val config = super.getSessionConfiguration(session)

        config.planeFindingMode = Config.PlaneFindingMode.VERTICAL

        return config
    }

}
