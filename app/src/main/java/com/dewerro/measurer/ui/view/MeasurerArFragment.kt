package com.dewerro.measurer.ui.view

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment;

// Данный класс применяет необходимые настройки в фрагменте дополненной реальности.
class MeasurerArFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {
        val config = super.getSessionConfiguration(session)

        // Конфигурируем фрагмент дополненной реальности так, чтобы он находил вертикальные
        // и горизонтальные поверхности.
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

        return config
    }

}
