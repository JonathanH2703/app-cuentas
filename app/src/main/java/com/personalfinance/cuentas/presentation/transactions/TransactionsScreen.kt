package com.personalfinance.cuentas.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.core.ui.theme.FinancialAmber
import com.personalfinance.cuentas.core.ui.theme.FinancialBlue
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.core.ui.theme.FinancialRed
import com.personalfinance.cuentas.data.local.entities.BlockType
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount
import com.personalfinance.cuentas.data.local.relations.TransactionWithDetails
import com.personalfinance.cuentas.domain.anchor.AnchorOption
import com.personalfinance.cuentas.domain.anchor.AnchorRecommender
import com.personalfinance.cuentas.domain.anchor.AnchorType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.FilterChipDefaults as FilterChipDefaultsMat3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.automirrored.filled.TrendingUp

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedType by viewModel.selectedTypeFilter.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val anchorOptions by viewModel.anchorOptions.collectAsState()
    val pendingObligationPayments by viewModel.pendingObligationPayments.collectAsState()
    val cardPurchases by viewModel.cardPurchases.collectAsState()

    var anchorTarget by remember { mutableStateOf<TransactionWithDetails?>(null) }
    var editTarget by remember { mutableStateOf<TransactionWithDetails?>(null) }
    var showAutomationSettings by remember { mutableStateOf(false) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var showAccountMenu by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Movimientos",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = { showAutomationSettings = !showAutomationSettings },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (showAutomationSettings) MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Configurar automatización",
                        tint = if (showAutomationSettings) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Apartado de automatización desplegable (más compacto)
            AnimatedVisibility(visible = showAutomationSettings) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    BankAutomationCard(
                        onScanActiveNotifications = { viewModel.scanActiveNotifications() },
                        onSimulateTestNotification = { bank, msg -> viewModel.simulateTestNotification(bank, msg) }
                    )
                }
            }

            // Pagos de obligación pendientes
            if (pendingObligationPayments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                PendingObligationPaymentsCard(
                    payments = pendingObligationPayments,
                    onConfirm = { viewModel.confirmObligationPayment(it) },
                    onDiscard = { viewModel.discardObligationPayment(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Búsqueda Delgada y Filtros en Desplegables
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Barra de búsqueda más delgada
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Buscar...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1.2f).height(42.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                // Filtro de Tipo (Pequeño desplegable)
                Box {
                    Surface(
                        onClick = { showTypeMenu = true },
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.FilterList, null, modifier = Modifier.size(16.dp))
                            Text(
                                text = when(selectedType) {
                                    TransactionType.EXPENSE -> "Gastos"
                                    TransactionType.INCOME -> "Ingresos"
                                    TransactionType.ADJUSTMENT -> "Ajustes"
                                    else -> "Todos"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = showTypeMenu,
                        onDismissRequest = { showTypeMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos los tipos", fontSize = 13.sp) },
                            onClick = { viewModel.setTypeFilter(null); showTypeMenu = false }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        DropdownMenuItem(
                            text = { Text("Solo Gastos", fontSize = 13.sp) },
                            onClick = { viewModel.setTypeFilter(TransactionType.EXPENSE); showTypeMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Solo Ingresos", fontSize = 13.sp) },
                            onClick = { viewModel.setTypeFilter(TransactionType.INCOME); showTypeMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Solo Ajustes", fontSize = 13.sp) },
                            onClick = { viewModel.setTypeFilter(TransactionType.ADJUSTMENT); showTypeMenu = false }
                        )
                    }
                }

                // Filtro de Cuentas (Pequeño desplegable)
                Box {
                    Surface(
                        onClick = { showAccountMenu = true },
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.AccountBalance, null, modifier = Modifier.size(16.dp))
                            Text(
                                text = accounts.find { it.id == selectedAccountId }?.name ?: "Cuentas",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 70.dp)
                            )
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = showAccountMenu,
                        onDismissRequest = { showAccountMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas las cuentas", fontSize = 13.sp) },
                            onClick = { viewModel.setAccountFilter(null); showAccountMenu = false }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        accounts.forEach { account ->
                            DropdownMenuItem(
                                text = { Text(account.name, fontSize = 13.sp) },
                                onClick = { viewModel.setAccountFilter(account.id); showAccountMenu = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

        // Lista de Transacciones
        if (transactions.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se encontraron movimientos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Registra un gasto con el botón central (+) o confirma un ingreso.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            // Lista unificada: transacciones + compras de tarjeta ordenadas por fecha
            val cardRows = remember(cardPurchases, searchQuery, selectedType) {
                if (selectedType != null && selectedType != TransactionType.EXPENSE) emptyList()
                else cardPurchases.flatMap { cwa ->
                    cwa.purchases.filter { p ->
                        searchQuery.isBlank() ||
                        p.merchant.contains(searchQuery, ignoreCase = true) ||
                        cwa.account.name.contains(searchQuery, ignoreCase = true)
                    }.map { p -> cwa to p }
                }
            }
            val unified: List<Any> = remember(transactions, cardRows) {
                (transactions.map { it as Any } + cardRows.map { it as Any })
                    .sortedByDescending { item ->
                        when (item) {
                            is TransactionWithDetails -> item.transaction.dateMillis
                            is Pair<*, *> -> (item.second as? com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity)?.purchaseDateMillis ?: 0L
                            else -> 0L
                        }
                    }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(unified.size, key = { idx ->
                    when (val it = unified[idx]) {
                        is TransactionWithDetails -> "tx_${it.transaction.id}"
                        is Pair<*, *> -> "cp_${(it.second as? com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity)?.id}"
                        else -> "item_$idx"
                    }
                }) { idx ->
                    when (val item = unified[idx]) {
                        is TransactionWithDetails -> TransactionItemRow(
                            item = item,
                            onDelete = { viewModel.deleteTransaction(item.transaction) },
                            onAnchorClick = { anchorTarget = item },
                            onEditClick = { editTarget = item }
                        )
                        is Pair<*, *> -> {
                            @Suppress("UNCHECKED_CAST")
                            val pair = item as Pair<com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount,
                                com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity>
                            CardPurchaseItemRow(
                                purchase = pair.second,
                                cardName = "${pair.first.account.name} ••${pair.first.card.lastFourDigits ?: ""}"
                            )
                        }
                    }
                }
            }
        }

        // Diálogo para anclar el movimiento seleccionado a una obligación
        anchorTarget?.let { target ->
            AnchorTransactionDialog(
                transaction = target,
                anchorOptions = anchorOptions,
                onDismiss = { anchorTarget = null },
                onConfirm = { debtId, recurringItemId ->
                    viewModel.anchorTransaction(target.transaction.id, debtId, recurringItemId)
                }
            )
            }

        // Diálogo para editar categoría, monto y nota del movimiento seleccionado
        editTarget?.let { target ->
            EditTransactionDialog(
                transaction = target,
                categories = categories,
                onDismiss = { editTarget = null },
                onConfirm = { categoryId, amountCents, note, isLeisure, type ->
                    viewModel.updateTransaction(target.transaction, categoryId, amountCents, note, isLeisure, type)
                }
            )
        }
        }
    }
}

/**
 * Tarjeta con los pagos de obligación (tarjeta de crédito o crédito/préstamo) detectados
 * automáticamente (correos PSE/banco) que esperan confirmación. Al confirmar se baja el cupo
 * usado de la tarjeta o se reduce el saldo del crédito, sin debitar ninguna cuenta.
 */
@Composable
fun PendingObligationPaymentsCard(
    payments: List<PendingTransactionEntity>,
    onConfirm: (PendingTransactionEntity) -> Unit,
    onDiscard: (PendingTransactionEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = FinancialRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pagos detectados",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Confirma cada pago para actualizar la tarjeta o el crédito. Los correos repetidos del mismo pago se ignoran.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))

            payments.forEach { payment ->
                val isDebt = payment.suggestedDebtId != null
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = payment.suggestedMerchant ?: if (isDebt) "Crédito" else "Tarjeta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${if (isDebt) "Crédito" else "Tarjeta"} · ${CurrencyFormatter.formatCentsToCop(payment.suggestedAmountCents)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { onDiscard(payment) }) {
                        Text("Descartar", fontSize = 13.sp)
                    }
                    Button(onClick = { onConfirm(payment) }) {
                        Text("Confirmar", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AnchorTransactionDialog(
    transaction: TransactionWithDetails,
    anchorOptions: List<AnchorOption>,
    onDismiss: () -> Unit,
    onConfirm: (debtId: Long?, recurringItemId: Long?) -> Unit
) {
    val amountCents = transaction.transaction.amountCents
    val ranked = remember(anchorOptions, amountCents) {
        AnchorRecommender.rank(anchorOptions, amountCents)
    }

    var selectedType by remember(transaction.transaction.id) {
        mutableStateOf(
            transaction.transaction.linkedDebtId?.let { AnchorType.DEBT }
                ?: transaction.transaction.linkedRecurringItemId?.let { AnchorType.RECURRING }
        )
    }
    var selectedId by remember(transaction.transaction.id) {
        mutableStateOf(transaction.transaction.linkedDebtId ?: transaction.transaction.linkedRecurringItemId)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Anclar movimiento") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "${transaction.transaction.merchant ?: transaction.category.name} · ${CurrencyFormatter.formatCentsToCop(amountCents)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Indica a qué obligación corresponde este pago. Si es la cuota de un crédito o un gasto fijo, quedará marcada como cumplida.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(10.dp))

                AnchorOptionRow(
                    title = "Sin anclar",
                    subtitle = "Gasto libre: ocio, imprevistos, compras",
                    icon = Icons.Default.LinkOff,
                    selected = selectedId == null,
                    recommended = false,
                    onClick = {
                        selectedType = null
                        selectedId = null
                    }
                )

                ranked.forEach { option ->
                    AnchorOptionRow(
                        title = option.title,
                        subtitle = option.subtitle,
                        icon = if (option.type == AnchorType.DEBT) Icons.Default.CreditCard else Icons.Default.Repeat,
                        selected = selectedId == option.id && selectedType == option.type,
                        recommended = AnchorRecommender.isRecommended(option, amountCents),
                        onClick = {
                            selectedType = option.type
                            selectedId = option.id
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        if (selectedType == AnchorType.DEBT) selectedId else null,
                        if (selectedType == AnchorType.RECURRING) selectedId else null
                    )
                    onDismiss()
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun AnchorOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    recommended: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (recommended) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Monto similar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EditTransactionDialog(
    transaction: TransactionWithDetails,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (categoryId: Long, amountCents: Long, note: String?, isLeisure: Boolean, type: TransactionType) -> Unit
) {
    var amountText by remember(transaction.transaction.id) {
        mutableStateOf(CurrencyFormatter.formatCentsForInput(transaction.transaction.amountCents))
    }
    var selectedCategoryId by remember(transaction.transaction.id) {
        mutableStateOf(transaction.transaction.categoryId)
    }
    var note by remember(transaction.transaction.id) {
        mutableStateOf(transaction.transaction.note ?: "")
    }
    var isLeisure by remember(transaction.transaction.id) {
        mutableStateOf(transaction.transaction.isLeisure)
    }
    var selectedType by remember(transaction.transaction.id) {
        mutableStateOf(transaction.transaction.type)
    }

    val parsedAmount = remember(amountText) { CurrencyFormatter.parseCopToCents(amountText) }
    val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId }

    val dialogTitle = when (selectedType) {
        TransactionType.INCOME -> "Detalle de Ingreso"
        TransactionType.EXPENSE -> "Detalle de Gasto"
        TransactionType.TRANSFER_IN, TransactionType.TRANSFER_OUT -> "Detalle de Transferencia"
        TransactionType.ADJUSTMENT -> "Ajuste de Saldo"
        else -> "Detalle de Movimiento"
    }

    val accentColor = when (selectedType) {
        TransactionType.INCOME -> FinancialGreen
        TransactionType.EXPENSE -> FinancialRed
        TransactionType.TRANSFER_IN, TransactionType.TRANSFER_OUT -> FinancialBlue
        TransactionType.ADJUSTMENT -> FinancialAmber
        else -> MaterialTheme.colorScheme.primary
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(dialogTitle, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = when(selectedType) {
                        TransactionType.INCOME -> "Registra el dinero que ha entrado a tu cuenta."
                        TransactionType.EXPENSE -> "Ajusta los detalles de lo que has gastado o pagado."
                        else -> "Configura los detalles de este movimiento."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Tipo (Compacto y con colores)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        TransactionType.EXPENSE to "Gasto",
                        TransactionType.INCOME to "Ingreso",
                        TransactionType.TRANSFER_OUT to "Transf."
                    ).forEach { (type, label) ->
                        FilterChip(
                            selected = selectedType == type || (type == TransactionType.TRANSFER_OUT && selectedType == TransactionType.TRANSFER_IN),
                            onClick = { selectedType = type },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accentColor.copy(alpha = 0.2f),
                                selectedLabelColor = accentColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Monto Destacado
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.startsWith("-")) selectedType = TransactionType.EXPENSE
                        else if (input.startsWith("+")) selectedType = TransactionType.INCOME
                        amountText = input.filter { it.isDigit() || it == '.' || it == ',' || it == '-' || it == '+' }
                    },
                    label = { Text("Monto", fontSize = 12.sp) },
                    singleLine = true,
                    prefix = { Text("$ ", color = accentColor, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lógica Diferenciada
                if (selectedType == TransactionType.EXPENSE) {
                    // SECCIÓN GASTO: Ocio y Categorías de Gasto
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isLeisure) accentLeisureColor().copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .clickable { isLeisure = !isLeisure }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isLeisure) accentLeisureColor() else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gasto de Ocio / Deseo",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                color = if (isLeisure) accentLeisureColor() else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Se descontará del sobre de 'Ocio'",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        androidx.compose.material3.Switch(
                            checked = isLeisure,
                            onCheckedChange = { isLeisure = it },
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sección de Categoría
                if (selectedType != TransactionType.TRANSFER_IN && selectedType != TransactionType.TRANSFER_OUT) {
                    Text(
                        text = "Categoría",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val filteredCategories = remember(selectedType, categories) {
                        when (selectedType) {
                            TransactionType.INCOME -> categories.filter { 
                                val n = it.name.lowercase()
                                n.contains("ingreso") || n.contains("sueldo") || n.contains("nomina") || n.contains("regalo") || n.contains("ahorro")
                            }
                            TransactionType.EXPENSE -> categories.filter { 
                                val n = it.name.lowercase()
                                !n.contains("sueldo") && !n.contains("nomina")
                            }
                            else -> categories
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredCategories, key = { it.id }) { category ->
                            val isSelected = selectedCategoryId == category.id
                            val catColor = try { Color(android.graphics.Color.parseColor(category.colorHex)) } catch (e: Exception) { accentColor }
                            
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryId = category.id },
                                label = { Text(category.name, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = mapIconNameToVector(category.iconName),
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else catColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = catColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Nota y Cuenta (Info)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(when(selectedType) {
                        TransactionType.INCOME -> "Origen / Descripción"
                        TransactionType.EXPENSE -> "Comercio / Nota"
                        else -> "Nota"
                    }, fontSize = 12.sp) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cuenta: ${transaction.account.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedCategoryId, parsedAmount, note.ifBlank { null }, isLeisure, selectedType)
                    onDismiss()
                },
                enabled = parsedAmount > 0L
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun accentLeisureColor(): Color = Color(0xFFFFA000) // Amber/Orange for Leisure

private fun blockDisplayName(blockType: BlockType): String = when (blockType) {
    BlockType.NEEDS -> "Necesidades / Gastos fijos"
    BlockType.WANTS -> "Ocio y Deseos"
    BlockType.DEBTS -> "Deudas y Tarjetas"
    BlockType.SAVINGS -> "Ahorro e Inversión"
    BlockType.CUSTOM -> "Personalizado"
}


@Composable
fun TransactionItemRow(
    item: TransactionWithDetails,
    onDelete: () -> Unit,
    onAnchorClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    val dateFormatter = SimpleDateFormat("dd MMM, hh:mm a", Locale("es", "CO"))
    val formattedDate = dateFormatter.format(Date(item.transaction.dateMillis))

    val isExpense  = item.transaction.type == TransactionType.EXPENSE
    val isIncome   = item.transaction.type == TransactionType.INCOME
    val isLeisure  = item.transaction.isLeisure
    val hasAnchor  = item.linkedDebt != null || item.linkedRecurringItem != null

    val catColor = try { Color(android.graphics.Color.parseColor(item.category.colorHex)) }
                   catch (e: Exception) { MaterialTheme.colorScheme.primary }

    val rowColor = when {
        isIncome  -> FinancialGreen
        isLeisure -> FinancialAmber
        else      -> catColor
    }

    val signSymbol = if (isIncome || item.transaction.type == TransactionType.TRANSFER_IN) "+" else "-"

    val typeTag: String? = when {
        isIncome  -> null
        isLeisure -> "Ocio"
        item.linkedDebt != null -> "Deuda"
        item.linkedRecurringItem != null -> "Gasto fijo"
        else -> null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = androidx.compose.foundation.layout.IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra lateral de color
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(rowColor.copy(alpha = 0.75f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(rowColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isLeisure -> Icons.Default.Star
                            isIncome  -> Icons.AutoMirrored.Filled.TrendingUp
                            else      -> mapIconNameToVector(item.category.iconName)
                        },
                        contentDescription = null,
                        tint = rowColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Datos
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.transaction.merchant
                            ?: item.transaction.note?.take(40)
                            ?: item.category.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = catColor.copy(alpha = 0.10f)
                        ) {
                            Text(
                                text = item.category.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = catColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                maxLines = 1
                            )
                        }
                        if (typeTag != null) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = rowColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = typeTag,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = rowColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    val anchorLabel = item.linkedDebt?.name ?: item.linkedRecurringItem?.title
                    if (anchorLabel != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, null, tint = FinancialBlue, modifier = Modifier.size(10.dp))
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = anchorLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = FinancialBlue,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Monto + botones
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$signSymbol${CurrencyFormatter.formatCentsToCop(item.transaction.amountCents)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = rowColor,
                        maxLines = 1
                    )
                    Row {
                        IconButton(onClick = onEditClick, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
                        }
                        IconButton(onClick = onAnchorClick, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (hasAnchor) Icons.Default.Link else Icons.Default.LinkOff,
                                contentDescription = null,
                                tint = if (hasAnchor) FinancialBlue else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.DeleteOutline, null, tint = FinancialRed.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CardPurchaseItemRow(
    purchase: CardPurchaseEntity,
    cardName: String
) {
    val dateFormatter = java.text.SimpleDateFormat("dd MMM", java.util.Locale("es", "CO"))
    val formattedDate = dateFormatter.format(java.util.Date(purchase.purchaseDateMillis))
    val isSettled  = purchase.isFullyPaid
    val isDeferred = purchase.installmentsTotal > 1

    val accent = when {
        isSettled  -> MaterialTheme.colorScheme.outline
        isDeferred -> FinancialBlue
        else       -> FinancialRed
    }
    val statusLabel = when {
        isSettled  -> "Pagada"
        isDeferred -> "${purchase.installmentsPaid}/${purchase.installmentsTotal}c"
        else       -> "Pendiente"
    }
    val amountColor = when {
        isSettled  -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        isDeferred -> FinancialBlue
        else       -> FinancialRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = androidx.compose.foundation.layout.IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(FinancialBlue.copy(alpha = if (isSettled) 0.25f else 0.7f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(FinancialBlue.copy(alpha = if (isSettled) 0.05f else 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = FinancialBlue.copy(alpha = if (isSettled) 0.4f else 1f),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                // Columna central con peso para que el monto tenga espacio fijo
                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                    Text(
                        text = purchase.merchant,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isSettled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(text = "·", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = cardName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = FinancialBlue.copy(alpha = if (isSettled) 0.5f else 0.8f),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // Columna derecha: monto + chip de estado, ambos maxLines=1
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "-${com.personalfinance.cuentas.core.common.CurrencyFormatter.formatCentsToCop(purchase.totalAmountCents)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = amountColor,
                        maxLines = 1
                    )
                    androidx.compose.material3.Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                        color = accent.copy(alpha = 0.10f)
                    ) {
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = accent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
