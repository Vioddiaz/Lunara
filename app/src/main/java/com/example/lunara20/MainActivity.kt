package com.example.lunara20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lunara20.navigation.AppNavigation
import com.example.lunara20.ui.theme.Lunara20Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lunara20Theme {
                AppNavigation()
            }
        }
    }
}
