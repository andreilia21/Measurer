package com.dewerro.measurer.view.measurement

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout

open abstract class MeasureCalculatorView(context: Context) : ConstraintLayout(context), MeasureCalculator {}