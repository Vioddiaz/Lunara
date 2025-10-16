// En CalendarView.kt
package com.example.lunara // O el paquete que hayas elegido

import android.widget.Toast // Import para Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // Necesario para by remember
import androidx.compose.runtime.setValue // Necesario para by remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Import para LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CalendarViewWithControls(modifier: Modifier = Modifier) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    val context = LocalContext.current // Obtenemos el contexto actual

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Contenedor para el calendario
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MonthHeader(
                calendar = currentMonth,
                onPreviousMonth = {
                    currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                    selectedDate = null // Limpiar selección al cambiar de mes
                },
                onNextMonth = {
                    currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                    selectedDate = null // Limpiar selección al cambiar de mes
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DaysOfWeekHeader()
            Spacer(modifier = Modifier.height(8.dp))
            MonthGrid(
                calendar = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = { day ->
                    val newSelectedDate = (currentMonth.clone() as Calendar).apply {
                        set(Calendar.DAY_OF_MONTH, day)
                    }
                    selectedDate = newSelectedDate
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp)) // Espacio antes de los botones

        // Botones de acción
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre botones
        ) {
            Button(
                onClick = {
                    selectedDate?.let { date ->
                        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date.time)
                        val message = "Fecha registrada: $dateString"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        // Aquí puedes añadir cualquier otra lógica de registro
                        println("Registrar: $dateString")
                    }
                },
                enabled = selectedDate != null // Habilitar solo si hay una fecha seleccionada
            ) {
                Text("Registrar")
            }

            Button(
                onClick = {
                    selectedDate?.let { date ->
                        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date.time)
                        val message = "Fecha eliminada: $dateString"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        // Aquí puedes añadir cualquier otra lógica de borrado
                        println("Borrar para: $dateString")
                        // Opcional: Deseleccionar la fecha después de borrar
                        // selectedDate = null
                    }
                },
                enabled = selectedDate != null // Habilitar solo si hay una fecha seleccionada
            ) {
                Text("Borrar")
            }
        }
    }
}

@Composable
fun MonthHeader(calendar: Calendar, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = onPreviousMonth) {
            Text("<")
        }
        Text(text = monthFormat.format(calendar.time), style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onNextMonth) {
            Text(">")
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MonthGrid(
    calendar: Calendar,
    selectedDate: Calendar?,
    onDateSelected: (Int) -> Unit
) {
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val dayOfWeekOfFirstDay = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) // Domingo = 1, Sábado = 7
    // Ajusta 'emptyCellsBefore' si tu Calendar.SUNDAY no es 1 o si la semana empieza en otro día.
    // Para Locale.getDefault(), Calendar.SUNDAY es usualmente 1.
    val emptyCellsBefore = dayOfWeekOfFirstDay - Calendar.getInstance().firstDayOfWeek

    Column {
        val numRows = (emptyCellsBefore + daysInMonth + 6) / 7 // Calcula filas necesarias

        for (week in 0 until numRows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) { // 0 para el primer día de la semana (ej. Domingo), 6 para el último
                    val cellIndex = week * 7 + dayOfWeek
                    val dayNumber = cellIndex - emptyCellsBefore + 1

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f) // Para hacer celdas cuadradas
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNumber > 0 && dayNumber <= daysInMonth) {
                            val currentDateIter = (calendar.clone() as Calendar).apply {
                                set(Calendar.DAY_OF_MONTH, dayNumber)
                            }
                            val isSelected = selectedDate?.let {
                                it.get(Calendar.YEAR) == currentDateIter.get(Calendar.YEAR) &&
                                        it.get(Calendar.MONTH) == currentDateIter.get(Calendar.MONTH) &&
                                        it.get(Calendar.DAY_OF_MONTH) == currentDateIter.get(Calendar.DAY_OF_MONTH)
                            } ?: false

                            TextButton(
                                onClick = { onDateSelected(dayNumber) },
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(text = dayNumber.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun CalendarViewWithControlsPreview() {
    MaterialTheme { // Asegúrate de tener un tema de Material
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            CalendarViewWithControls()
        }
    }
}
