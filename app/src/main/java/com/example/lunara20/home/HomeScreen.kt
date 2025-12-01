package com.example.lunara20.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.lunara20.ui.theme.Lunara20Theme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

// --- COLORES DEL DISEÑO ---
val PrimaryPurple = Color(0xFF6A0DAD)
val PrimaryPink = Color(0xFFFE3D97)
val BackgroundLight = Color(0xFFF7F7F7)
val CardBackground = Color.White
val TabBackground = Color(0xFFF0F0F0)
val PeriodColor = Color(0xFFF44336)
val PredictionColor = Color(0xFFFFC0CB)
val DialogButtonSelected = Color(0xFF1E1E1E)
val DialogButtonNormal = Color.White


/**
 * Pantalla principal (Dashboard) de Lunara.
 */
@Composable
fun HomeScreen(userName: String) {
    var selectedTabIndex by remember { mutableStateOf(0) } // Estado para la pestaña seleccionada

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        TopHeader(userName = userName)
        Text(
            text = "Tu compañera de bienestar menstrual",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )
        IconTabBar(selectedIndex = selectedTabIndex, onTabSelected = { selectedTabIndex = it })
        Spacer(modifier = Modifier.height(24.dp))

        // --- Contenido Dinámico ---
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> MainContent() // Contenido por defecto (Gráficos)
                1 -> CalendarView() // Vista de Calendario
                2 -> SettingsContent() // Placeholder para Configuración
                3 -> StatsContent() // Placeholder para Estadísticas
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- VISTA DE CALENDARIO ---
@Composable
fun CalendarView() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    if (showDialog && selectedDate != null) {
        RegisterPeriodDialog(
            date = selectedDate!!,
            onDismiss = { showDialog = false },
            onSave = { /* TODO: Lógica para guardar */ showDialog = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            CalendarHeader(currentMonth = currentMonth, onMonthChange = { currentMonth = it })
            Spacer(Modifier.height(24.dp))
            CalendarLegend()
            Spacer(Modifier.height(16.dp))
            WeekDaysHeader()
            Spacer(Modifier.height(8.dp))
            MonthDaysGrid(currentMonth = currentMonth, onDayClick = {
                selectedDate = currentMonth.atDay(it)
                showDialog = true
            })
        }
    }
}

// --- DIÁLOGO DE REGISTRO (¡NUEVO!) ---
@Composable
fun RegisterPeriodDialog(date: LocalDate, onDismiss: () -> Unit, onSave: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var selectedFlow by remember { mutableStateOf("Medio") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Título y botón de cerrar
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Registrar período", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Close, contentDescription = "Cerrar") }
                }
                Spacer(Modifier.height(24.dp))

                // Fecha de inicio
                Text("Fecha de inicio", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(value = date.format(formatter), onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))

                // Fecha de fin
                Text("Fecha de fin (opcional)", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("dd/mm/aaaa") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))

                // Flujo
                Text("Flujo", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                FlowSelector(selectedFlow = selectedFlow, onFlowSelected = { selectedFlow = it })
                Spacer(Modifier.height(32.dp))

                // Botón Guardar
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DialogButtonSelected)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FlowSelector(selectedFlow: String, onFlowSelected: (String) -> Unit) {
    val flows = listOf("Ligero", "Medio", "Abundante")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        flows.forEach { flow ->
            val isSelected = flow == selectedFlow
            Button(
                onClick = { onFlowSelected(flow) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) DialogButtonSelected else TabBackground,
                    contentColor = if (isSelected) Color.White else Color.Black
                ),
                border = if (!isSelected) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
            ) {
                Text(flow)
            }
        }
    }
}


// --- COMPONENTES DEL CALENDARIO ---
@Composable
fun CalendarHeader(currentMonth: YearMonth, onMonthChange: (YearMonth) -> Unit) {
    val monthName = currentMonth.month.getDisplayName(JavaTextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.titlecase() }
    val year = currentMonth.year

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "$monthName $year", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Row {
            MonthNavigator(icon = Icons.Default.ChevronLeft, onClick = { onMonthChange(currentMonth.minusMonths(1)) })
            Spacer(Modifier.width(8.dp))
            MonthNavigator(icon = Icons.Default.ChevronRight, onClick = { onMonthChange(currentMonth.plusMonths(1)) })
        }
    }
}

@Composable
fun MonthNavigator(icon: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(32.dp).border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))) {
        Icon(imageVector = icon, contentDescription = "Navegar mes", tint = Color.Gray)
    }
}

@Composable
fun CalendarLegend() {
    Row {
        LegendItem(color = PeriodColor, text = "Período")
        Spacer(Modifier.width(16.dp))
        LegendItem(color = PredictionColor, text = "Predicción")
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun WeekDaysHeader() {
    val weekDays = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        weekDays.forEach { day ->
            Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun MonthDaysGrid(currentMonth: YearMonth, onDayClick: (Int) -> Unit) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
    val startOffset = (firstDayOfMonth.value % 7) // Offset para que Domingo sea 0

    LazyVerticalGrid(columns = GridCells.Fixed(7)) {
        items(startOffset) {}
        items(daysInMonth) { day -> DayCell(day = day + 1, onClick = { onDayClick(day + 1) }) }
    }
}

@Composable
fun DayCell(day: Int, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.aspectRatio(1f).clickable(onClick = onClick) // Celda cuadrada y clickable
    ) {
        Text(text = day.toString(), fontSize = 14.sp)
    }
}

// ... (Resto de componentes sin cambios)
@Composable
fun SettingsContent() { Text("Vista de Configuración (próximamente)") }

@Composable
fun StatsContent() { Text("Vista de Estadísticas (próximamente)") }

@Composable
fun TopHeader(userName: String) {
    val logoGradient = Brush.horizontalGradient(listOf(Color(0xFFF9A8D4), Color(0xFFC4B5FD)))
    val textGradient = Brush.horizontalGradient(listOf(PrimaryPink, PrimaryPurple))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(logoGradient), contentAlignment = Alignment.Center) {
                Text(text = "🌙", fontSize = 24.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = "LUNARA", style = TextStyle(brush = textGradient, fontSize = 28.sp, fontWeight = FontWeight.Bold))
                Text(text = "Hola, $userName", fontSize = 14.sp, color = Color.Gray)
            }
        }
        IconButton(onClick = { /* Lógica de cerrar sesión */ }, modifier = Modifier.size(40.dp).border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(4.dp)) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = "Salir", tint = Color.Gray)
        }
    }
}

@Composable
fun MainContent() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        MainActionCard()
        Spacer(modifier = Modifier.height(16.dp))
        InfoCard(icon = Icons.Default.CalendarToday, title = "Próximo período", subtitle = "Registra tu período para ver predicciones", content = null, iconTint = PrimaryPurple)
        Spacer(modifier = Modifier.height(16.dp))
        InfoCard(icon = Icons.Default.Nightlight, title = "Duración del ciclo", subtitle = "Período de 5 días", content = "28 días", iconTint = PrimaryPurple)
        Spacer(modifier = Modifier.height(16.dp))
        CurrentPhaseCard()
    }
}

@Composable
fun IconTabBar(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(Icons.Default.ShowChart to "Gráficos", Icons.Default.CalendarMonth to "Calendario", Icons.Default.Settings to "Configuración", Icons.Default.BarChart to "Estadísticas")
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(TabBackground).padding(vertical = 4.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        tabs.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex
            IconTabItem(icon = item.first, contentDescription = item.second, isSelected = isSelected, onClick = { onTabSelected(index) })
        }
    }
}

@Composable
fun IconTabItem(icon: ImageVector, contentDescription: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (isSelected) CardBackground else Color.Transparent).clickable(onClick = onClick).padding(12.dp), contentAlignment = Alignment.Center) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = if (isSelected) PrimaryPurple else Color.Gray, modifier = Modifier.size(28.dp))
    }
}

@Composable
fun MainActionCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F0F8)), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Registrar", tint = PrimaryPurple, modifier = Modifier.size(40.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(text = "Registra tu período", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
            Text(text = "Sin datos", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun InfoCard(icon: ImageVector, title: String, subtitle: String, content: String?, iconTint: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(TabBackground), contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
                }
            }
            if (content != null) {
                Text(text = content, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
            }
        }
    }
}

@Composable
fun CurrentPhaseCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text(text = "Sobre tu fase actual", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Registro requerido", tint = PrimaryPurple, modifier = Modifier.padding(end = 8.dp, top = 2.dp).size(20.dp))
                Text(text = "Registra tu período en el calendario para comenzar a rastrear tu ciclo.", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLunaraHomeScreen() {
    Lunara20Theme {
        Box(modifier = Modifier.background(BackgroundLight)) {
            HomeScreen(userName = "Usuario")
        }
    }
}

@Preview
@Composable
fun PreviewRegisterDialog() {
    Lunara20Theme {
        RegisterPeriodDialog(date = LocalDate.now(), onDismiss = {}, onSave = {})
    }
}
