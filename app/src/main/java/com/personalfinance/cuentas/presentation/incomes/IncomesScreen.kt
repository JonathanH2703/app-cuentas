package com.personalfinance.cuentas.presentation.incomes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.core.ui.theme.FinancialRed
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.IncomeEntity
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.Timeline

import com.personalfinance.cuentas.presentation.dashboard.PlannedMovementsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomesScreen(
    viewModel: IncomesViewModel,
    modifier: Modifier = Modifier
) {
    val pendingIncomes by viewModel.pendingIncomes.collectAsState()
    val receivedIncomes by viewModel.receivedIncomes.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val totalPendingCents by viewModel.totalPendingCents.collectAsState()
    val selectedHorizon by viewModel.selectedHorizonMonths.collectAsState()
    val plannedMovements by viewModel.plannedMovements.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCreateDialog by remember { mutableStateOf(false) }
    var incomeToConfirm by remember { mutableStateOf<IncomeEntity?>(null) }
    var incomeToEdit by remember { mutableStateOf<IncomeEntity?>(null) }
    var showTimeline by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ingresos",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Cobros, nómina y proyecciones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Button(
                        onClick = { showCreateDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Resumen de Ingresos Esperados
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = FinancialGreen.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "INGRESOS PENDIENTES",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp),
                                    color = FinancialGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = CurrencyFormatter.formatCentsToCop(totalPendingCents),
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                    color = FinancialGreen
                                )
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    onClick = { showTimeline = true },
                                    shape = CircleShape,
                                    color = FinancialGreen.copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Timeline,
                                            contentDescription = "Ver línea de tiempo",
                                            tint = FinancialGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedHorizon == 1,
                                onClick = { viewModel.setHorizon(1) },
                                label = { Text("Este mes") },
                                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FinancialGreen.copy(alpha = 0.3f),
                                    selectedLabelColor = FinancialGreen
                                )
                            )
                            FilterChip(
                                selected = selectedHorizon == 5,
                                onClick = { viewModel.setHorizon(5) },
                                label = { Text("Próximos 5 meses") },
                                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FinancialGreen.copy(alpha = 0.3f),
                                    selectedLabelColor = FinancialGreen
                                )
                            )
                        }
                    }
                }
            }

            // Sección: Ingresos Programados / Pendientes
            item {
                Text(
                    text = "Pendientes por Confirmar (${pendingIncomes.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (pendingIncomes.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "No tienes ingresos pendientes. Pulsa + para programar tu nómina, quincena o pagos freelance.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(pendingIncomes, key = { it.id }) { income ->
                    val targetAccount = accounts.firstOrNull { it.id == income.accountId }
                    PendingIncomeCard(
                        income = income,
                        targetAccount = targetAccount,
                        onConfirmClick = { incomeToConfirm = income },
                        onEdit = { incomeToEdit = income },
                        onDelete = { viewModel.deleteIncome(income) }
                    )
                }
            }

            // Sección: Histórico de Recibidos
            if (receivedIncomes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Historial Recibidos (${receivedIncomes.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(receivedIncomes, key = { it.id }) { income ->
                    val targetAccount = accounts.firstOrNull { it.id == income.accountId }
                    ReceivedIncomeCard(
                        income = income,
                        targetAccount = targetAccount,
                        onEdit = { incomeToEdit = income },
                        onDelete = { viewModel.deleteIncome(income) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }

    if (showCreateDialog) {
        CreateIncomeDialog(
            accounts = accounts,
            onDismiss = { showCreateDialog = false },
            onConfirm = { desc, amountCents, accountId, freq, dateMillis ->
                viewModel.createIncome(desc, amountCents, accountId, freq, dateMillis)
                showCreateDialog = false
            }
        )
    }

    incomeToEdit?.let { income ->
        EditIncomeDialog(
            income = income,
            accounts = accounts,
            onDismiss = { incomeToEdit = null },
            onConfirm = { desc, amountCents, accountId, freq, dateMillis ->
                viewModel.updateIncome(income, desc, amountCents, accountId, freq, dateMillis)
                incomeToEdit = null
            }
        )
    }

    incomeToConfirm?.let { income ->
        ConfirmIncomeDialog(
            income = income,
            accounts = accounts,
            onDismiss = { incomeToConfirm = null },
            onConfirm = { actualAmountCents, targetAccountId ->
                viewModel.confirmIncome(income.id, actualAmountCents, targetAccountId)
                incomeToConfirm = null
            }
        )
    }

    if (showTimeline) {
        PlannedMovementsDialog(
            movements = plannedMovements,
            onDismiss = { showTimeline = false }
        )
    }
}

@Composable
fun PendingIncomeCard(
    income: IncomeEntity,
    targetAccount: AccountEntity?,
    onConfirmClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("es", "CO"))
    val formattedDate = dateFormatter.format(Date(income.expectedDateMillis))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = income.description,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Esperado: $formattedDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (targetAccount != null) {
                        Text(
                            text = "Destino: ${targetAccount.name}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(income.amountCents),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = FinancialGreen,
                        softWrap = false
                    )

                    var menuExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar Ingreso") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = FinancialRed) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = null,
                                        tint = FinancialRed
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón de Confirmación y Ajuste a Valor Real
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinancialGreen,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirmar Recibido (Ajustar valor real)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ReceivedIncomeCard(
    income: IncomeEntity,
    targetAccount: AccountEntity?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "CO"))
    val formattedDate = dateFormatter.format(Date(income.receivedDateMillis ?: income.expectedDateMillis))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = FinancialGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = income.description,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Recibido el $formattedDate • ${targetAccount?.name ?: ""}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+${CurrencyFormatter.formatCentsToCop(income.amountCents)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = FinancialGreen,
                    softWrap = false
                )

                var menuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar Ingreso") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = FinancialRed) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = null,
                                    tint = FinancialRed
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

private val incomeDayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("es", "CO"))

/**
 * Calcula la próxima fecha (desde hoy) en que cae un día del mes dado.
 * Si el día ya pasó este mes, salta al siguiente.
 */
private fun nextDateForDayOfMonth(day: Int, today: LocalDate = LocalDate.now()): LocalDate {
    val d = day.coerceIn(1, 31)
    val thisMonthDay = d.coerceAtMost(today.lengthOfMonth())
    return if (today.dayOfMonth <= thisMonthDay) {
        today.withDayOfMonth(thisMonthDay)
    } else {
        val next = today.plusMonths(1)
        next.withDayOfMonth(d.coerceAtMost(next.lengthOfMonth()))
    }
}

private fun dayOfMonthToMillis(day: Int): Long =
    nextDateForDayOfMonth(day).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

/**
 * Selector del día del mes en que llega un ingreso (ej. nómina el 15 y el 30).
 * Solo pide el día; la fecha concreta se calcula a partir de hoy.
 */
@Composable
fun IncomeDayOfMonthField(
    label: String,
    dayOfMonth: Int,
    onDayChange: (Int) -> Unit
) {
    Text(label, style = MaterialTheme.typography.labelMedium)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onDayChange(if (dayOfMonth <= 1) 31 else dayOfMonth - 1) }) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Día anterior",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Día $dayOfMonth de cada mes",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Próximo: ${nextDateForDayOfMonth(dayOfMonth).format(incomeDayFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = { onDayChange(if (dayOfMonth >= 31) 1 else dayOfMonth + 1) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Día siguiente",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateIncomeDialog(
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onConfirm: (description: String, amountCents: Long, accountId: Long, frequency: IncomeFrequency, dateMillis: Long) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0L) }
    var selectedFrequency by remember { mutableStateOf(IncomeFrequency.BIWEEKLY) }
    var dayOfMonth by remember { mutableStateOf(LocalDate.now().dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Programar Ingreso") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (ej. Nómina Quincena 1)") },
                    placeholder = { Text("ej. Salario con auxilio de transporte") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto Estimado en COP (acepta centavos)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                IncomeDayOfMonthField(
                    label = "¿Qué día del mes llega este dinero?",
                    dayOfMonth = dayOfMonth,
                    onDayChange = { dayOfMonth = it }
                )

                Text("Frecuencia:", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        IncomeFrequency.BIWEEKLY to "Quincenal",
                        IncomeFrequency.MONTHLY to "Mensual",
                        IncomeFrequency.WEEKLY to "Semanal",
                        IncomeFrequency.ONE_TIME to "Único"
                    ).forEach { (freq, label) ->
                        FilterChip(
                            selected = selectedFrequency == freq,
                            onClick = { selectedFrequency = freq },
                            label = { Text(label) }
                        )
                    }
                }

                Text("Cuenta Destino:", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    accounts.forEach { acc ->
                        FilterChip(
                            selected = selectedAccountId == acc.id,
                            onClick = { selectedAccountId = acc.id },
                            label = { Text(acc.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rawCents = CurrencyFormatter.parseCopToCents(amountText)
                    onConfirm(description, rawCents, selectedAccountId, selectedFrequency, dayOfMonthToMillis(dayOfMonth))
                },
                enabled = description.isNotBlank() && amountText.isNotBlank() && selectedAccountId != 0L
            ) {
                Text("Programar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

/**
 * Diálogo para anclar el valor real recibido al momento de confirmar un ingreso.
 * Permite ajustar si hubo deducción de almuerzos, auxilio de transporte, comisiones, etc.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfirmIncomeDialog(
    income: IncomeEntity,
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onConfirm: (actualAmountCents: Long, targetAccountId: Long) -> Unit
) {
    var realAmountText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(income.amountCents)) }
    var selectedAccountId by remember { mutableStateOf(income.accountId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Pago Recibido") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = income.description,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Valor programado originalmente: ${CurrencyFormatter.formatCentsToCop(income.amountCents)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "💡 Puedes ajustar al valor real exacto (por ejemplo si variaron los almuerzos del mes o llegó con auxilio de transporte).",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinancialGreen
                        )
                    }
                }

                OutlinedTextField(
                    value = realAmountText,
                    onValueChange = { realAmountText = it },
                    label = { Text("Monto Real Recibido en COP (acepta centavos)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Cuenta donde ingresó el dinero:", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    accounts.forEach { acc ->
                        FilterChip(
                            selected = selectedAccountId == acc.id,
                            onClick = { selectedAccountId = acc.id },
                            label = { Text(acc.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalCents = CurrencyFormatter.parseCopToCents(realAmountText)
                    onConfirm(finalCents, selectedAccountId)
                },
                enabled = realAmountText.isNotBlank() && selectedAccountId != 0L,
                colors = ButtonDefaults.buttonColors(containerColor = FinancialGreen)
            ) {
                Text("Acreditar Saldo Real")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditIncomeDialog(
    income: IncomeEntity,
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onConfirm: (description: String, amountCents: Long, accountId: Long, frequency: IncomeFrequency, dateMillis: Long) -> Unit
) {
    var description by remember { mutableStateOf(income.description) }
    var amountText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(income.amountCents)) }
    var selectedAccountId by remember { mutableStateOf(income.accountId) }
    var selectedFrequency by remember { mutableStateOf(income.frequency) }
    var dayOfMonth by remember {
        mutableStateOf(
            Instant.ofEpochMilli(income.expectedDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate().dayOfMonth
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Ingreso Programado") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto en COP (acepta centavos)") },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                IncomeDayOfMonthField(
                    label = "¿Qué día del mes llega este dinero?",
                    dayOfMonth = dayOfMonth,
                    onDayChange = { dayOfMonth = it }
                )

                Text("Frecuencia:", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        IncomeFrequency.BIWEEKLY to "Quincenal",
                        IncomeFrequency.MONTHLY to "Mensual",
                        IncomeFrequency.WEEKLY to "Semanal",
                        IncomeFrequency.ONE_TIME to "Único"
                    ).forEach { (freq, label) ->
                        FilterChip(
                            selected = selectedFrequency == freq,
                            onClick = { selectedFrequency = freq },
                            label = { Text(label) }
                        )
                    }
                }

                Text("Cuenta Destino:", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    accounts.forEach { acc ->
                        FilterChip(
                            selected = selectedAccountId == acc.id,
                            onClick = { selectedAccountId = acc.id },
                            label = { Text(acc.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rawCents = CurrencyFormatter.parseCopToCents(amountText)
                    onConfirm(description, rawCents, selectedAccountId, selectedFrequency, dayOfMonthToMillis(dayOfMonth))
                },
                enabled = description.isNotBlank() && amountText.isNotBlank() && selectedAccountId != 0L
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

