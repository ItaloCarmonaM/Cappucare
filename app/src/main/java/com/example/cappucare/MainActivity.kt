package com.example.cappucare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cappucare.navigation.AppNavigation
import com.example.cappucare.ui.theme.CappucareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CappucareTheme {
                AppNavigation()
            }
        }
    }
}