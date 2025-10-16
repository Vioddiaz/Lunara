// En MainActivity.kt
package com.example.lunara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.lunara.CalendarViewWithControls // Asegúrate de que el import sea correcto

// Definimos los posibles estados de la pantalla para que el código sea más claro
enum class ScreenState {
    LOGIN,
    REGISTER,
    MAIN_APP
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LunaraApp()
            }
        }
    }
}

@Composable
fun LunaraApp() {
    // El estado ahora puede ser uno de los tres definidos en ScreenState
    var currentScreen by remember { mutableStateOf(ScreenState.LOGIN) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Usamos when para decidir qué pantalla mostrar
        when (currentScreen) {
            ScreenState.LOGIN -> {
                // Muestra Login. Si tiene éxito, va a MAIN_APP. Si pide registro, va a REGISTER.
                LoginScreen(
                    onLoginSuccess = { currentScreen = ScreenState.MAIN_APP },
                    onNavigateToRegister = { currentScreen = ScreenState.REGISTER }
                )
            }
            ScreenState.REGISTER -> {
                // Muestra Registro. Si tiene éxito, va a MAIN_APP. Si quiere volver, va a LOGIN.
                RegistrationScreen(
                    onRegistrationSuccess = { currentScreen = ScreenState.MAIN_APP },
                    onNavigateToLogin = { currentScreen = ScreenState.LOGIN }
                )
            }
            ScreenState.MAIN_APP -> {
                // Muestra la app principal (el calendario)
                CalendarViewWithControls(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
