package com.personalfinance.cuentas.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalBalanceCents by viewModel.consolidatedBalanceCents.collectAsState()
    val staticSavingsCents by viewModel.staticSavingsBalanceCents.collectAsState()
    val netAfterGmfCents by viewModel.totalNetAfterGmfCents.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val activeModel by viewModel.activeModel.collectAsState()
    val allModels by viewModel.allModels.collectAsState()
    val actualBlockEnvelopes by viewModel.actualBlockEnvelopes.collectAsState()
    val projectedBlockEnvelopes by viewModel.projectedBlockEnvelopes.collectAsState()
    val actualSafeToSpend by viewModel.actualSafeToSpend.collectAsState()
    val projectedSafeToSpend by viewModel.projectedSafeToSpend.collectAsState()
    val actualCashFlowProjection by viewModel.actualCashFlowProjection.collectAsState()
    val cashFlowProjection by viewModel.cashFlowProjection.collectAsState()
    val recurringItems by viewModel.recurringItems.collectAsState()
    val activeDebts by viewModel.activeDebts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val commitmentsSummary by viewModel.commitmentsSummary.collectAsState()
    val envelopeDetails by viewModel.envelopeDetails.collectAsState()
    val plannedMovements by viewModel.plannedMovements.collectAsState()
    val isFlagSecureEnabled by viewModel.isFlagSecureEnabled.collectAsState()
    val creditCardObligations by viewModel.creditCardObligations.collectAsState()
    val nextIncomeReserve by viewModel.nextIncomeReserve.collectAsState()
    val receivablesTotalCents by viewModel.receivablesTotalCents.collectAsState()
    val totalCardDebtCents by viewModel.totalCardDebtCents.collectAsState()
    val currentMonthPendingCents by viewModel.currentMonthPendingCents.collectAsState()
    val currentMonthNetCents by viewModel.currentMonthNetCents.collectAsState()
    val eventsByBlock by viewModel.eventsByBlock.collectAsState()
    val realSavingsCents by viewModel.realSavingsCents.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedHorizon by remember { mutableIntStateOf(30) }
    var showPlannedMovements by remember { mutableStateOf(false) }
    var showReserveDialog by remember { mutableStateOf(false) }

    var showChangeModelDialog by remember { mutableStateOf(false) }
    var blockToEditBudget by remember { mutableStateOf<BlockAllocationUiModel?>(null) }
    var envelopeDetailBlockId by remember { mutableStateOf<Long?>(null) }
    var recurringToEdit by remember { mutableStateOf<RecurringItemEntity?>(null) }

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
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Cabecera Principal con Ajustes y Bloqueo Inmediato
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mis Finanzas",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Tus cuentas, bolsillos y gastos al día",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ajustes y Copia de Seguridad",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.lockApp() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloquear sesión",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 1. Hero Card Compacto: Saldo + Safe-to-Spend unificados
            item {
                CompactFinancialHeroCard(
                    consolidatedCents = totalBalanceCents ?: 0L,
                    staticSavingsCents = staticSavingsCents ?: 0L,
                    currentMonthPendingCents = currentMonthPendingCents,
                    currentMonthNetCents = currentMonthNetCents,
                    onShowTimeline = { showPlannedMovements = true }
                )
            }

            // Reserva dinámica: dinero que debe quedar para lo que vence antes del próximo ingreso
            nextIncomeReserve?.let { reserve ->
                item {
                    NextIncomeReserveCard(
                        reserve = reserve,
                        onClick = { showReserveDialog = true }
                    )
                }
            }

            // Posición neta: lo que terceros deben ("por cobrar") y deuda neta de tarjetas
            if (receivablesTotalCents > 0L) {
                item {
                    NetPositionCard(
                        receivablesCents = receivablesTotalCents,
                        totalCardDebtCents = totalCardDebtCents
                    )
                }
            }

            // 2. Obligaciones Fijas Mensuales
            item {
                RecurringItemsSection(
                    items = recurringItems,
                    debts = activeDebts,
                    cards = creditCardObligations,
                    totalMonthlyCents = commitmentsSummary.totalNeedsCommitments,
                    accounts = accounts,
                    categories = categories,
                    onAddClick = { title, amount, accountId, categoryId, day ->
                        viewModel.addRecurringItem(title, amount, accountId, categoryId, day)
                    },
                    onPayClick = { item ->
                        viewModel.payRecurringItem(item)
                    },
                    onEditClick = { item ->
                        recurringToEdit = item
                    },
                    onDeleteClick = { item ->
                        viewModel.deleteRecurringItem(item)
                    }
                )
            }

            // 3. Bolsillos de Gasto (Envelopes)
            item {
                AllocationEnvelopesSection(
                    modelName = activeModel?.model?.name ?: "50/30/20",
                    blocks = actualBlockEnvelopes,
                    eventsByBlock = eventsByBlock,
                    onConfigureModelClick = { showChangeModelDialog = true },
                    onAdjustBudgetClick = { blockToEditBudget = it },
                    onViewDetailClick = { envelopeDetailBlockId = it.block.id }
                )
            }

            // 4. Proyección de Flujo de Caja
            item {
                CashFlowChartCard(
                    projections = actualCashFlowProjection.dailyProjections,
                    selectedHorizon = selectedHorizon,
                    onHorizonSelected = { selectedHorizon = it },
                    isProjectedView = false,
                    onViewModeChange = null
                )
            }

            // 5. Cuentas Registradas
            item {
                Text(
                    text = "Cuentas Activas (${accounts.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (accounts.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "No hay cuentas activas registradas aún. Ve a la pestaña 'Cuentas' para agregar Nequi, Daviplata o Efectivo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(accounts) { account ->
                    AccountItemRow(account)
                }
            }

            // 6. Diagnóstico de Seguridad y Privacidad (informativo, al final para no desplazar lo financiero)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Seguridad del Dispositivo",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        SecurityStatusRow("SQLCipher AES-256", "Activo")
                        SecurityStatusRow("Android Keystore", "Hardware TEE")
                        SecurityStatusRow("BiometricPrompt + PIN", "Configurado")
                        SecurityStatusRow(
                            "FLAG_SECURE Anti-Capturas",
                            if (isFlagSecureEnabled) "Activo" else "Desactivado"
                        )
                        SecurityStatusRow("Conexión a Internet", "100% Offline")
                    }
                }
            }
        }
    }

    // Diálogo para Cambiar Modelo de Asignación (50/30/20, 70/20/10, etc.)
    if (showChangeModelDialog) {
        AlertDialog(
            onDismissRequest = { showChangeModelDialog = false },
            title = { Text("¿Cómo repartir tus ingresos?") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Elige cómo quieres que se divida el dinero cada vez que te paguen:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )

                    allModels.forEach { modelWithBlocks ->
                        val isCurrent = modelWithBlocks.model.id == activeModel?.model?.id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.switchActiveModel(modelWithBlocks.model.id)
                                    showChangeModelDialog = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = modelWithBlocks.model.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                    if (isCurrent) {
                                        Text("ACTIVO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = modelWithBlocks.blocks.joinToString(" • ") { "${it.name} (${it.percentage}%)" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChangeModelDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Diálogo de detalle ampliado del bolsillo (cálculos, fechas y plan)
    envelopeDetailBlockId?.let { blockId ->
        val detail = envelopeDetails[blockId]
        val accent = actualBlockEnvelopes
            .firstOrNull { it.block.id == blockId }?.color ?: MaterialTheme.colorScheme.primary
        if (detail != null) {
            EnvelopeDetailDialog(
                detail = detail,
                accentColor = accent,
                onDismiss = { envelopeDetailBlockId = null }
            )
        }
    }

    if (showPlannedMovements) {
        PlannedMovementsDialog(
            movements = plannedMovements,
            onDismiss = { showPlannedMovements = false }
        )
    }

    nextIncomeReserve?.let { reserve ->
        if (showReserveDialog) {
            NextIncomeReserveDialog(
                reserve = reserve,
                onDismiss = { showReserveDialog = false }
            )
        }
    }

    // Diálogo para Fijar Presupuesto Mensual al Sobre
    blockToEditBudget?.let { item ->
        var budgetRaw by remember { mutableStateOf("") }

        LaunchedEffect(item) {
            budgetRaw = if (item.budgetLimitCents > 0L) (item.budgetLimitCents / 100).toString() else ""
        }

        val budgetCents = remember(budgetRaw) {
            CurrencyFormatter.parseCopToCents(budgetRaw)
        }

        AlertDialog(
            onDismissRequest = { blockToEditBudget = null },
            title = { Text("Tope mensual: ${item.block.name}") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Define cuánto dinero máximo quieres gastar este mes en este bolsillo. Te avisará en rojo si te pasas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    OutlinedTextField(
                        value = budgetRaw,
                        onValueChange = { newVal ->
                            val filtered = newVal.filter { it.isDigit() || it == ',' }
                            if (filtered.count { it == ',' } <= 1) {
                                val parts = filtered.split(",")
                                if (parts.size == 2 && parts[1].length > 2) return@OutlinedTextField
                                budgetRaw = filtered
                            }
                        },
                        label = { Text("Presupuesto en COP") },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setBudgetLimit(item.block.id, budgetCents)
                        blockToEditBudget = null
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { blockToEditBudget = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    recurringToEdit?.let { item ->
        EditRecurringDialog(
            item = item,
            accounts = accounts,
            categories = categories,
            onDismiss = { recurringToEdit = null },
            onConfirm = { title, amount, accountId, categoryId, day ->
                viewModel.updateRecurringItem(item, title, amount, accountId, categoryId, day)
                recurringToEdit = null
            }
        )
    }
}
