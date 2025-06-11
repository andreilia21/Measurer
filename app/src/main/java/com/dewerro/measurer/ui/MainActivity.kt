package com.dewerro.measurer.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.dewerro.measurer.R
import com.dewerro.measurer.data.auth.Auth
import com.dewerro.measurer.data.auth.firebase.FirebaseAuthService
import com.dewerro.measurer.data.order.Orders
import com.dewerro.measurer.data.order.firebase.FirebaseOrderService
import com.dewerro.measurer.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MeasurerApp()
        }
    }
}