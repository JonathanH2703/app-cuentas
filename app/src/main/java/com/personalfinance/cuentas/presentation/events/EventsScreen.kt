package com.personalfinance.cuentas.presentation.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.core.ui.theme.FinancialAmber
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.core.ui.theme.FinancialRed
import com.personalfinance.cuentas.data.local.entities.EventEntity
import com.personalfinance.cuentas.data.local.entities.EventType

private val MONTHS_ES = listOf(
    "Enero","Febrero","Marzo","Abril","Mayo","Junio",
    "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
)


@Composable
fun EventsScreen(
    viewModel: EventsViewModel,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<EventEntity?>(null) }

    LaunchedEffect(Unit) { viewModel.userMessage.collect { snackbar.showSnackbar(it) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fechas Importantes",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground)
                Text("Cumpleanos, aniversarios y fechas que no quieres olvidar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline)
                    }
                    // Botón + integrado en el encabezado, sin flotar
                    Surface(
                        onClick = { showAdd = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar fecha",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Nueva", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            val soonEvents = events.filter { it.daysUntil in 0..7 }
            if (soonEvents.isNotEmpty()) {
                item {
                    Surface(shape = RoundedCornerShape(16.dp),
                        color = FinancialAmber.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, null, tint = FinancialAmber, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Esta semana",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FinancialAmber)
                            }
                            Spacer(Modifier.height(8.dp))
                            soonEvents.forEach { ev ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(buildTitle(ev),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(when (ev.daysUntil) { 0 -> "Hoy!"; 1 -> "Manana"; else -> "En ${ev.daysUntil}d" },
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (ev.daysUntil == 0) FinancialRed else FinancialAmber)
                                }
                            }
                        }
                    }
                }
            }

            if (events.isEmpty()) {
                item {
                    Surface(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
                        Column(modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Celebration, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                            Text("Sin fechas guardadas", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.outline)
                            Text("Agrega cumpleanos, aniversarios\ny fechas que no quieres olvidar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                items(events, key = { it.entity.id }) { ev ->
                    EventCard(ev,
                        onEdit = { editTarget = ev.entity },
                        onDelete = { viewModel.deleteEvent(ev.entity) },
                        onToggle = { viewModel.toggleActive(ev.entity) })
                }
            }
        }
    }

    if (showAdd) {
        EventDialog(null, { showAdd = false }) { title, type, day, month, year, person, notify, amount, coverage, block ->
            viewModel.addEvent(title, type, day, month, year, person, notify, amount, coverage, block)
            showAdd = false
        }
    }
    editTarget?.let { target ->
        EventDialog(target, { editTarget = null }) { title, type, day, month, year, person, notify, amount, coverage, block ->
            viewModel.updateEvent(
                target.copy(title = title, type = type, dayOfMonth = day,
                    monthOfYear = month, year = year, personName = person,
                    notifyDaysBefore = notify, totalAmountCents = amount, coveragePercent = coverage, blockType = block))
            editTarget = null
        }
    }
}

@Composable
private fun EventCard(event: EventUi, onEdit: () -> Unit, onDelete: () -> Unit, onToggle: () -> Unit) {
    val e = event.entity
    val inactive = !e.isActive
    val isFinancial = event.isFinancial

    val accent = when {
        inactive               -> MaterialTheme.colorScheme.outline
        event.daysUntil == 0   -> FinancialRed
        event.daysUntil <= 7   -> FinancialAmber
        event.daysUntil <= 30  -> MaterialTheme.colorScheme.primary
        else                   -> FinancialGreen
    }
    val tag = when {
        inactive              -> "Inactivo"
        event.daysUntil == 0  -> "Hoy!"
        event.daysUntil == 1  -> "Manana"
        else                  -> "En ${event.daysUntil} dias"
    }

    Card(modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (inactive) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                             else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(if (inactive) 0.dp else 1.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {

            // Fila principal
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center) {
                    Icon(iconFor(e.type), null, tint = accent, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(buildTitle(event),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (inactive) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${MONTHS_ES[e.monthOfYear - 1]} ${e.dayOfMonth}${if (e.year != null) " de ${e.year}" else " (anual)"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                    Surface(shape = RoundedCornerShape(6.dp), color = accent.copy(alpha = 0.10f), modifier = Modifier.padding(top = 4.dp)) {
                        Text(tag, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = accent, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Switch(checked = e.isActive, onCheckedChange = { onToggle() })
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(15.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, null, tint = FinancialRed.copy(alpha = 0.6f), modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            // Panel financiero (solo para OBLIGATION_*)
            if (isFinancial && e.totalAmountCents != null && e.totalAmountCents > 0L && event.targetCents != null) {
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {

                        // Cobertura elegida
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Costo total",
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Text(CurrencyFormatter.formatCentsToCop(e.totalAmountCents),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Quiero abarcar (${e.coveragePercent}%)",
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Text(CurrencyFormatter.formatCentsToCop(event.targetCents),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary)
                        }

                        // Barra de progreso vs meta de cobertura
                        val progressFloat = event.progressPct / 100f
                        val progressColor = when {
                            progressFloat >= 1f   -> FinancialGreen
                            progressFloat >= 0.5f -> MaterialTheme.colorScheme.primary
                            else                  -> FinancialAmber
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Acumulado: ${CurrencyFormatter.formatCentsToCop(e.accumulatedCents)}",
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Text("${event.progressPct}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = progressColor)
                        }
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { progressFloat },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = progressColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant)

                        // Ahorro mensual sugerido
                        if ((event.monthlySaving ?: 0L) > 0L && event.monthsUntil > 0) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text("Aparta cada mes (${event.monthsUntil} meses):",
                                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Surface(shape = RoundedCornerShape(6.dp), color = accent.copy(alpha = 0.12f)) {
                                    Text(CurrencyFormatter.formatCentsToCop(event.monthlySaving!!),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = accent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }
                        }

                        // Si ya cubrió la meta
                        if (event.progressPct >= 100) {
                            Surface(shape = RoundedCornerShape(8.dp), color = FinancialGreen.copy(alpha = 0.10f),
                                modifier = Modifier.fillMaxWidth()) {
                                Text("Meta cubierta para esta obligacion",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = FinancialGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventDialog(
    initial: EventEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, EventType, Int, Int, Int?, String?, Int, Long?, Int, String?) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: EventType.BIRTHDAY) }
    var dayStr by remember { mutableStateOf(initial?.dayOfMonth?.toString() ?: "") }
    var monthIdx by remember { mutableIntStateOf((initial?.monthOfYear ?: 1) - 1) }
    var yearStr by remember { mutableStateOf(initial?.year?.toString() ?: "") }
    var person by remember { mutableStateOf(initial?.personName ?: "") }
    var notifyStr by remember { mutableStateOf(initial?.notifyDaysBefore?.toString() ?: "1") }
    var amountStr by remember { mutableStateOf(
        if (initial?.totalAmountCents != null) (initial.totalAmountCents / 100).toString() else "") }
    var coveragePct by remember { mutableIntStateOf(initial?.coveragePercent ?: 100) }
    var blockTypeSel by remember { mutableStateOf(initial?.blockType) }

    val isFinancial = type == EventType.OBLIGATION_ANNUAL || type == EventType.OBLIGATION_SPORADIC
    val day = dayStr.toIntOrNull() ?: 0
    val isValid = title.isNotBlank() && day in 1..31

    // Calculo en tiempo real del monto objetivo y mensual
    val totalCents = amountStr.toLongOrNull()?.let { it * 100L }
    val targetCents = if (totalCents != null) totalCents * coveragePct / 100L else null
    // Estimacion de meses: usamos 12 como valor de referencia si no hay fecha exacta
    val estimatedMonths = run {
        if (day > 0 && monthIdx in 0..11) {
            val (d, _) = EventsViewModel.daysUntilNextOccurrence(day, monthIdx + 1, yearStr.toIntOrNull())
            maxOf(1, d / 30)
        } else 12
    }
    val monthlySuggestion = if (targetCents != null && estimatedMonths > 0) targetCents / estimatedMonths else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nueva fecha" else "Editar fecha",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── Tipo ──────────────────────────────────────────────────────
                Text("Tipo", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Text("Fechas personales", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(EventType.BIRTHDAY, EventType.ANNIVERSARY, EventType.CUSTOM).forEach { t ->
                        FilterChip(selected = type == t, onClick = { type = t },
                            label = { Text(labelFor(t), style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(iconFor(t), null, modifier = Modifier.size(14.dp)) })
                    }
                }
                Text("Obligaciones con ahorro", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(EventType.OBLIGATION_ANNUAL, EventType.OBLIGATION_SPORADIC).forEach { t ->
                        FilterChip(selected = type == t, onClick = { type = t },
                            label = { Text(labelFor(t), style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(iconFor(t), null, modifier = Modifier.size(14.dp)) })
                    }
                }

                // ── Bolsillo del dashboard ─────────────────────────────────────
                if (isFinancial) {
                    Text("Cargar al bolsillo",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("NEEDS" to "Necesidades", "WANTS" to "Ocio", "SAVINGS" to "Ahorro").forEach { (key, label) ->
                            FilterChip(
                                selected = blockTypeSel == key,
                                onClick = { blockTypeSel = if (blockTypeSel == key) null else key },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                    if (blockTypeSel == null) {
                        Text("Sin asignar: no aparece en el dashboard",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline)
                    }
                }

                // ── Titulo ────────────────────────────────────────────────────
                OutlinedTextField(value = title, onValueChange = { title = it },
                    label = { Text(when(type) {
                        EventType.BIRTHDAY -> "Nombre de la persona"
                        EventType.ANNIVERSARY -> "Descripcion del aniversario"
                        EventType.OBLIGATION_ANNUAL, EventType.OBLIGATION_SPORADIC -> "Nombre (SOAT, Impuesto...)"
                        else -> "Titulo"
                    }) }, singleLine = true, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(value = person, onValueChange = { person = it },
                    label = { Text("Apodo / nota (opcional)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())

                // ── Campos financieros ─────────────────────────────────────────
                if (isFinancial) {
                    OutlinedTextField(value = amountStr,
                        onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Costo total estimado ($)") },
                        prefix = { Text("$ ") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth())

                    if (totalCents != null && totalCents > 0L) {
                        // Slider de cobertura
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Quiero abarcar: $coveragePct%",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                if (targetCents != null) {
                                    Text(CurrencyFormatter.formatCentsToCop(targetCents),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            androidx.compose.material3.Slider(
                                value = coveragePct.toFloat(),
                                onValueChange = { coveragePct = it.toInt().coerceIn(5, 100) },
                                valueRange = 5f..100f,
                                steps = 18,  // pasos de 5%
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("5%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("100%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            if (monthlySuggestion != null && monthlySuggestion > 0L) {
                                Surface(shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                    modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically) {
                                        Text("Deberias apartar al mes:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface)
                                        Text(CurrencyFormatter.formatCentsToCop(monthlySuggestion),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Fecha ─────────────────────────────────────────────────────
                OutlinedTextField(value = dayStr,
                    onValueChange = { dayStr = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Dia del mes (1-31)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())

                Text("Mes", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(MONTHS_ES.size) { i ->
                        FilterChip(selected = monthIdx == i, onClick = { monthIdx = i },
                            label = { Text(MONTHS_ES[i].take(3), style = MaterialTheme.typography.labelSmall) })
                    }
                }

                OutlinedTextField(value = yearStr,
                    onValueChange = { yearStr = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text("Anio (vacio = repite cada anio)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())

                OutlinedTextField(value = notifyStr,
                    onValueChange = { notifyStr = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Notificar X dias antes") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountCents = if (isFinancial) amountStr.toLongOrNull()?.let { it * 100L } else null
                onConfirm(title.trim(), type, day, monthIdx + 1,
                    yearStr.toIntOrNull(), person.trim().takeIf { it.isNotBlank() },
                    notifyStr.toIntOrNull() ?: 1, amountCents, coveragePct, blockTypeSel)
            }, enabled = isValid) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun labelFor(type: EventType): String = when (type) {
    EventType.BIRTHDAY            -> "Cumpleanos"
    EventType.ANNIVERSARY         -> "Aniversario"
    EventType.CUSTOM              -> "Otro"
    EventType.OBLIGATION_ANNUAL   -> "Anual (SOAT...)"
    EventType.OBLIGATION_SPORADIC -> "Eventual"
}

private fun iconFor(type: EventType): ImageVector = when (type) {
    EventType.BIRTHDAY    -> Icons.Default.Cake
    EventType.ANNIVERSARY -> Icons.Default.Favorite
    EventType.CUSTOM               -> Icons.Default.Star
    EventType.OBLIGATION_ANNUAL    -> Icons.Default.DirectionsCar
    EventType.OBLIGATION_SPORADIC  -> Icons.Default.Savings
}

private fun buildTitle(ev: EventUi): String =
    if (ev.entity.personName != null) "${ev.entity.title} (${ev.entity.personName})" else ev.entity.title
