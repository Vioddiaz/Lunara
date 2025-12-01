package com.example.lunara20.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- COLORES DEL DISEÑO ---
val PrimaryPurple = Color(0xFF8A2BE2)
val PrimaryPink = Color(0xFFF4338F)
val CardBackground = Color.White
val LightGreyBackground = Color(0xFFF7F7F9)
val TabBarBackground = Color(0xFFF0F0F0)
val TextGrey = Color.Gray
val ErrorColor = Color.Red

// --- FUNCIONES DE UTILIDAD PARA VALIDACIÓN Y FILTRADO ---
private fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun filterInput(text: String): String {
    // Permite letras, números y caracteres especiales comunes, excluyendo emojis.
    return text.filter { char ->
        Character.isLetterOrDigit(char) || "@._-!#$%&'*+/=?^`{|}~ ".contains(char)
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    // --- ESTADOS ---
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoginSelected by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isEmailTouched by remember { mutableStateOf(false) }

    val emailIsInvalid = isEmailTouched && email.isNotEmpty() && !isEmailValid(email)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreyBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Encabezado (se muestra siempre) ---
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(35.dp))
                    .background(Brush.horizontalGradient(listOf(PrimaryPink, PrimaryPurple))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌙", fontSize = 36.sp)
            }
            Text(
                text = "LUNARA",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Tu compañera de bienestar menstrual",
                fontSize = 16.sp,
                color = TextGrey,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // --- Tarjeta Principal ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // --- Pestañas ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(TabBarBackground)
                            .padding(4.dp)
                    ) {
                        val clearFields = {
                            name = ""
                            email = ""
                            password = ""
                            confirmPassword = ""
                            isEmailTouched = false
                        }
                        TabButton(
                            text = "Iniciar sesión",
                            isSelected = isLoginSelected,
                            onClick = {
                                isLoginSelected = true
                                clearFields()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "Registrarse",
                            isSelected = !isLoginSelected,
                            onClick = {
                                isLoginSelected = false
                                clearFields()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    // --- LÓGICA PARA MOSTRAR FORMULARIO ---
                    if (isLoginSelected) {
                        LoginForm(
                            email = email,
                            onEmailChange = { email = filterInput(it) },
                            password = password,
                            onPasswordChange = { password = filterInput(it) },
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityChange = { passwordVisible = it },
                            emailIsInvalid = emailIsInvalid,
                            onEmailFocusChange = { isEmailTouched = true },
                            onLoginClick = {
                                val userName = email.split("@").firstOrNull() ?: "Usuario"
                                navController.navigate("home/$userName")
                            }
                        )
                    } else {
                        RegisterForm(
                            name = name,
                            onNameChange = { name = filterInput(it) },
                            email = email,
                            onEmailChange = { email = filterInput(it) },
                            password = password,
                            onPasswordChange = { password = filterInput(it) },
                            confirmPassword = confirmPassword,
                            onConfirmPasswordChange = { confirmPassword = filterInput(it) },
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityChange = { passwordVisible = it },
                            confirmPasswordVisible = confirmPasswordVisible,
                            onConfirmPasswordVisibilityChange = { confirmPasswordVisible = it },
                            emailIsInvalid = emailIsInvalid,
                            onEmailFocusChange = { isEmailTouched = true },
                            onRegisterClick = { navController.navigate("home/$name") }
                        )
                    }
                }
            }
        }
    }
}

// --- FORMULARIO DE INICIO DE SESIÓN ---
@Composable
fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    emailIsInvalid: Boolean,
    onEmailFocusChange: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column {
        Text(text = "Email", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "tu@email.com",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = TextGrey) },
            isError = emailIsInvalid,
            onFocusChange = { onEmailFocusChange() }
        )
        AnimatedVisibility(visible = emailIsInvalid) {
            Text("Por favor, introduce un email válido.", color = ErrorColor, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
        }

        Spacer(Modifier.height(20.dp))

        Text(text = "Contraseña", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        CustomTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "••••••••",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon", tint = TextGrey) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                Icon(
                    imageVector = image,
                    contentDescription = "Ocultar/Mostrar contraseña",
                    modifier = Modifier.clickable { onPasswordVisibilityChange(!passwordVisible) },
                    tint = TextGrey
                )
            }
        )

        Spacer(Modifier.height(32.dp))

        GradientButton(
            text = "Iniciar sesión",
            onClick = onLoginClick,
            gradientColors = listOf(PrimaryPink, PrimaryPurple),
            isEnabled = email.isNotBlank() && password.isNotBlank() && !emailIsInvalid
        )

        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "¿Olvidaste tu contraseña? ", color = TextGrey)
            ClickableText(text = "Recuperar", onClick = { /* Navegar a recuperación */ }, color = PrimaryPurple)
        }
    }
}

// --- FORMULARIO DE REGISTRO ---
@Composable
fun RegisterForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: (Boolean) -> Unit,
    emailIsInvalid: Boolean,
    onEmailFocusChange: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val passwordsDoNotMatch = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword

    val isFormValid = name.isNotBlank() &&
            isEmailValid(email) &&
            password.length >= 6 &&
            !passwordsDoNotMatch

    Column {
        Text(text = "Nombre", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        CustomTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Tu nombre",
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name Icon", tint = TextGrey) }
        )

        Spacer(Modifier.height(20.dp))

        Text(text = "Email", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "tu@email.com",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = TextGrey) },
            isError = emailIsInvalid,
            onFocusChange = { onEmailFocusChange() }
        )
        AnimatedVisibility(visible = emailIsInvalid) {
            Text("Por favor, introduce un email válido.", color = ErrorColor, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
        }

        Spacer(Modifier.height(20.dp))

        Text(text = "Contraseña", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        CustomTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Mínimo 6 caracteres",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon", tint = TextGrey) },
            isError = password.isNotEmpty() && password.length < 6,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                Icon(
                    imageVector = image,
                    contentDescription = "Ocultar/Mostrar contraseña",
                    modifier = Modifier.clickable { onPasswordVisibilityChange(!passwordVisible) },
                    tint = TextGrey
                )
            }
        )

        Spacer(Modifier.height(20.dp))

        Text(text = "Confirmar contraseña", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        CustomTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "Repite tu contraseña",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon", tint = TextGrey) },
            isError = passwordsDoNotMatch,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                Icon(
                    imageVector = image,
                    contentDescription = "Ocultar/Mostrar contraseña",
                    modifier = Modifier.clickable { onConfirmPasswordVisibilityChange(!confirmPasswordVisible) },
                    tint = TextGrey
                )
            }
        )
        AnimatedVisibility(visible = passwordsDoNotMatch) {
            Text(
                text = "Las contraseñas no coinciden.",
                color = ErrorColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        GradientButton(
            text = "Crear cuenta",
            onClick = onRegisterClick,
            gradientColors = listOf(PrimaryPink, PrimaryPurple),
            isEnabled = isFormValid
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Al registrarte, aceptas nuestros Términos de Servicio y Política de Privacidad",
            fontSize = 12.sp, color = TextGrey, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// --- Componentes Reutilizables ---

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (isSelected) Color.White else Color.Transparent
    val textColor = if (isSelected) PrimaryPurple else TextGrey

    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, contentColor = textColor),
        shape = RoundedCornerShape(10.dp),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) else null,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun GradientButton(
    text: String, onClick: () -> Unit, gradientColors: List<Color>,
    modifier: Modifier = Modifier, isEnabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(),
        enabled = isEnabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = RoundedCornerShape(12.dp)
                )
                .then(if (isEnabled) Modifier else Modifier.clip(RoundedCornerShape(12.dp)).background(Color.Gray.copy(alpha = 0.5f))),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun CustomTextField(
    value: String, onValueChange: (String) -> Unit, placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    onFocusChange: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    onFocusChange()
                }
            },
        placeholder = { Text(placeholder, color = TextGrey) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = TabBarBackground,
            unfocusedContainerColor = TabBarBackground,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.DarkGray,
            focusedBorderColor = if (isError) ErrorColor else Color.Transparent,
            unfocusedBorderColor = if (isError) ErrorColor else Color.Transparent,
            cursorColor = PrimaryPurple,
            errorCursorColor = ErrorColor
        )
    )
}

@Composable
fun ClickableText(text: String, onClick: () -> Unit, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoginScreenPreview() {
    //LoginScreen(onNavigateToHome = {})
}
