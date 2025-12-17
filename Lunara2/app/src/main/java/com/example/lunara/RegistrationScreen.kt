// En RegistrationScreen.kt
package com.example.lunara

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Estados para los campos del formulario
    var email by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Nueva Cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // --- CAMPOS DEL FORMULARIO ---
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it.filter { char -> char.isDigit() } }, // Solo permite números
            label = { Text("Edad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- MENSAJE DE ERROR ---
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        // --- BOTÓN DE REGISTRO ---
        Button(
            onClick = {
                // Lógica de validación
                if (email.isBlank() || nombre.isBlank() || edad.isBlank() || password.isBlank()) {
                    errorMessage = "Todos los campos son obligatorios."
                } else if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden."
                } else {
                    // En un caso real, aquí guardarías el usuario en la base de datos.
                    // Como la simulación fue exitosa:
                    errorMessage = null
                    onRegistrationSuccess() // Navega a la pantalla principal (calendario)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- BOTÓN PARA VOLVER A LOGIN ---
        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes una cuenta? Inicia Sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    MaterialTheme {
        RegistrationScreen(onRegistrationSuccess = {}, onNavigateToLogin = {})
    }
}
