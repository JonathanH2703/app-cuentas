package com.personalfinance.cuentas.presentation.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.core.ui.theme.FinancialAmber
import com.personalfinance.cuentas.core.ui.theme.FinancialBlue
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsState()
    val totalBalanceCents by viewModel.totalBalanceCents.collectAsState()
    val totalNetAvailableAfterGmfCents by viewModel.totalNetAvailableAfterGmfCents.collectAsState()
    val totalStaticSavingsCents by viewModel.totalStaticSavingsCents.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCreateDialog by remember { mutableStateOf(false) }
    var accountToAdjust by remember { mutableStateOf<AccountEntity?>(null) }
    var accountToEdit by remember { mutableStateOf<AccountEntity?>(null) }

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
                            text = "Mis Cuentas",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Billeteras, bancos y efectivo",
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
                        Text("Nueva", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Resumen de Saldo Consolidado y Neto Real 4x1000
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SALDO TOTAL CONSOLIDADO",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${accounts.count { it.includeInTotal && !it.isArchived }} activas",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = CurrencyFormatter.formatCentsToCop(totalBalanceCents),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Detalle del 4x1000 y Ahorro Estático
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = FinancialGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Neto real a retirar tras 4x1000: ${CurrencyFormatter.formatCentsToCop(totalNetAvailableAfterGmfCents)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            if (totalStaticSavingsCents > 0L) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = null,
                                        tint = FinancialBlue,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Ahorro protegido (estático): ${CurrencyFormatter.formatCentsToCop(totalStaticSavingsCents)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Lista de Cuentas
            if (accounts.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Aún no tienes cuentas registradas.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Crea cuentas como Nequi, Daviplata, Bancolombia o Efectivo pulsando el botón +",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(accounts, key = { it.id }) { account ->
                    AccountCardItem(
                        account = account,
                        onAdjustBalance = { accountToAdjust = account },
                        onEditDetails = { accountToEdit = account },
                        onToggleIncludeInTotal = { viewModel.toggleIncludeInTotal(account) },
                        onArchive = { viewModel.archiveAccount(account) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }

    if (showCreateDialog) {
        CreateAccountDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, type, balanceCents, colorHex, includeInTotal, isExemptFromGmf, yieldRate, isStaticSaving ->
                viewModel.createAccount(
                    name = name,
                    type = type,
                    initialBalanceCents = balanceCents,
                    colorHex = colorHex,
                    includeInTotal = includeInTotal,
                    isExemptFromGmf = isExemptFromGmf,
                    annualYieldRate = yieldRate,
                    isStaticSaving = isStaticSaving
                )
                showCreateDialog = false
            }
        )
    }

    accountToAdjust?.let { account ->
        AdjustBalanceDialog(
            account = account,
            onDismiss = { accountToAdjust = null },
            onConfirm = { newBalanceCents, reason ->
                viewModel.adjustBalance(account.id, newBalanceCents, reason)
                accountToAdjust = null
            }
        )
    }

    accountToEdit?.let { account ->
        EditAccountDialog(
            account = account,
            onDismiss = { accountToEdit = null },
            onConfirm = { name, type, colorHex, includeInTotal, isExemptFromGmf, yieldRate, isStaticSaving ->
                viewModel.updateAccountDetails(
                    account = account,
                    name = name,
                    type = type,
                    colorHex = colorHex,
                    includeInTotal = includeInTotal,
                    isExemptFromGmf = isExemptFromGmf,
                    annualYieldRate = yieldRate,
                    isStaticSaving = isStaticSaving
                )
                accountToEdit = null
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccountCardItem(
    account: AccountEntity,
    onAdjustBalance: () -> Unit,
    onEditDetails: () -> Unit,
    onToggleIncludeInTotal: () -> Unit,
    onArchive: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val iconVector = when (account.type) {
        AccountType.DIGITAL_WALLET -> Icons.Default.PhoneAndroid
        AccountType.CASH -> Icons.Default.AttachMoney
        AccountType.CHECKING -> Icons.Default.CreditCard
        else -> Icons.Default.AccountBalance
    }

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(account.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(parsedColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = parsedColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = when (account.type) {
                                AccountType.DIGITAL_WALLET -> "Billetera Digital"
                                AccountType.SAVINGS -> "Ahorros"
                                AccountType.CHECKING -> "Corriente"
                                AccountType.CASH -> "Efectivo"
                                AccountType.INVESTMENT -> "Inversión"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = CurrencyFormatter.formatCentsToCop(account.currentBalanceCents),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (account.includeInTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            softWrap = false
                        )
                    }

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar Características") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onEditDetails()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Ajustar Saldo") },
                                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onAdjustBalance()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (account.includeInTotal) "Excluir del Saldo Total" else "Incluir en Saldo Total") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (account.includeInTotal) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onToggleIncludeInTotal()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Archivar Cuenta") },
                                leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onArchive()
                                }
                            )
                        }
                    }
                }
            }

            // Insignias informativas de características
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Estado de inclusión en saldo total
                if (!account.includeInTotal) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Excluida del total",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Dinero Estático / Ahorro
                if (account.isStaticSaving) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FinancialBlue.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "🔒 Ahorro Estático",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = FinancialBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // 4x1000
                if (account.isExemptFromGmf) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FinancialGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "✓ Exenta 4x1000",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = FinancialGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (account.currentBalanceCents > 0L) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FinancialAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "4x1000: Neto ${CurrencyFormatter.formatCentsToCop(account.netBalanceAfterGmfCents)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = FinancialAmber,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Rentabilidad
                if (account.annualYieldRate != null && account.annualYieldRate > 0.0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFD700).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "📈 ${account.annualYieldRate}% E.A.",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFD4AF37),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        type: AccountType,
        balanceCents: Long,
        colorHex: String,
        includeInTotal: Boolean,
        isExemptFromGmf: Boolean,
        annualYieldRate: Double?,
        isStaticSaving: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.DIGITAL_WALLET) }
    var balanceText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF007A") }
    var includeInTotal by remember { mutableStateOf(true) }
    var isExemptFromGmf by remember { mutableStateOf(false) }
    var isStaticSaving by remember { mutableStateOf(false) }
    var yieldRateText by remember { mutableStateOf("") }

    val colorOptions = listOf(
        "#FF007A", // Nequi
        "#FFDD00", // Bancolombia
        "#ED1C24", // Daviplata
        "#820AD1", // Nu
        "#006C4C", // Esmeralda
        "#1565C0", // Azul Banco
        "#4CAF50", // Verde Efectivo
        "#9C27B0"  // Morado
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Cuenta") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre (ej. Nequi, Nu, Bancolombia)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Tipo de Cuenta:", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccountType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                if (type == AccountType.CASH) selectedColor = "#4CAF50"
                                if (type == AccountType.SAVINGS) selectedColor = "#1565C0"
                            },
                            label = {
                                Text(
                                    when (type) {
                                        AccountType.DIGITAL_WALLET -> "Billetera"
                                        AccountType.SAVINGS -> "Ahorros"
                                        AccountType.CHECKING -> "Corriente"
                                        AccountType.CASH -> "Efectivo"
                                        AccountType.INVESTMENT -> "Inversión"
                                    }
                                )
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Saldo Inicial en COP") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("$ ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = yieldRateText,
                    onValueChange = { yieldRateText = it },
                    label = { Text("Rentabilidad Anual % E.A. (opcional)") },
                    placeholder = { Text("ej. 13.0 para Nu o RappiPay") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("% E.A.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Opciones avanzadas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Cuenta Exenta de 4x1000", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("Marcada legalmente libre de GMF", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(checked = isExemptFromGmf, onCheckedChange = { isExemptFromGmf = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Dinero Estático (Ahorro intocable)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("No se cuenta en el 'Disponible para gastar'", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(checked = isStaticSaving, onCheckedChange = { isStaticSaving = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Incluir en Saldo Total Consolidado", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("Suma al patrimonio global de tus cuentas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(checked = includeInTotal, onCheckedChange = { includeInTotal = it })
                }

                Text("Color de la cuenta:", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(colorOptions) { hex ->
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .clickable { selectedColor = hex }
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rawCents = CurrencyFormatter.parseCopToCents(balanceText)
                    val yieldRate = yieldRateText.replace(",", ".").toDoubleOrNull()
                    onConfirm(name, selectedType, rawCents, selectedColor, includeInTotal, isExemptFromGmf, yieldRate, isStaticSaving)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Crear Cuenta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditAccountDialog(
    account: AccountEntity,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        type: AccountType,
        colorHex: String,
        includeInTotal: Boolean,
        isExemptFromGmf: Boolean,
        annualYieldRate: Double?,
        isStaticSaving: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf(account.name) }
    var selectedType by remember { mutableStateOf(account.type) }
    var selectedColor by remember { mutableStateOf(account.colorHex) }
    var includeInTotal by remember { mutableStateOf(account.includeInTotal) }
    var isExemptFromGmf by remember { mutableStateOf(account.isExemptFromGmf) }
    var isStaticSaving by remember { mutableStateOf(account.isStaticSaving) }
    var yieldRateText by remember { mutableStateOf(account.annualYieldRate?.toString() ?: "") }

    val colorOptions = listOf(
        "#FF007A", // Nequi
        "#FFDD00", // Bancolombia
        "#ED1C24", // Daviplata
        "#820AD1", // Nu
        "#006C4C", // Esmeralda
        "#1565C0", // Azul Banco
        "#4CAF50", // Verde Efectivo
        "#9C27B0"  // Morado
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Características: ${account.name}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Cuenta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Tipo de Cuenta:", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccountType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = {
                                Text(
                                    when (type) {
                                        AccountType.DIGITAL_WALLET -> "Billetera"
                                        AccountType.SAVINGS -> "Ahorros"
                                        AccountType.CHECKING -> "Corriente"
                                        AccountType.CASH -> "Efectivo"
                                        AccountType.INVESTMENT -> "Inversión"
                                    }
                                )
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = yieldRateText,
                    onValueChange = { yieldRateText = it },
                    label = { Text("Rentabilidad Anual % E.A. (opcional)") },
                    placeholder = { Text("ej. 13.0 para Nu o RappiPay") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("% E.A.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Cuenta Exenta de 4x1000", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("Marcada legalmente libre de GMF", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(checked = isExemptFromGmf, onCheckedChange = { isExemptFromGmf = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Dinero Estático (Ahorro intocable)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("No se toma en cuenta en el 'Disponible para gastar'", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(checked = isStaticSaving, onCheckedChange = { isStaticSaving = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Incluir en Saldo Total Consolidado", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("Si está apagado, no sumará en el patrimonio global", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(checked = includeInTotal, onCheckedChange = { includeInTotal = it })
                }

                Text("Color de la cuenta:", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(colorOptions) { hex ->
                        val isSelected = selectedColor.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .clickable { selectedColor = hex }
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val yieldRate = yieldRateText.replace(",", ".").toDoubleOrNull()
                    onConfirm(name, selectedType, selectedColor, includeInTotal, isExemptFromGmf, yieldRate, isStaticSaving)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AdjustBalanceDialog(
    account: AccountEntity,
    onDismiss: () -> Unit,
    onConfirm: (newBalanceCents: Long, reason: String?) -> Unit
) {
    var newBalanceText by remember { mutableStateOf(CurrencyFormatter.formatCentsForInput(account.currentBalanceCents)) }
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustar Saldo: ${account.name}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Saldo actual registrado: ${CurrencyFormatter.formatCentsToCop(account.currentBalanceCents)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )

                OutlinedTextField(
                    value = newBalanceText,
                    onValueChange = { newBalanceText = it },
                    label = { Text("Nuevo Saldo Real en COP (acepta centavos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("$ ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Motivo del ajuste (opcional)") },
                    placeholder = { Text("ej. Cuadre bancario o rendimientos") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newCents = CurrencyFormatter.parseCopToCents(newBalanceText)
                    onConfirm(newCents, reason.ifBlank { null })
                }
            ) {
                Text("Guardar Saldo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
