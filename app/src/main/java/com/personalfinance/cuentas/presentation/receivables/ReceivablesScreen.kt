package com.personalfinance.cuentas.presentation.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.ContactEntity
import com.personalfinance.cuentas.data.local.entities.ContactKind
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.relations.TransactionWithDetails

@Composable
fun ReceivablesScreen(viewModel: ReceivablesViewModel) {
    val totalPending by viewModel.totalPendingCents.collectAsState()
    val pendingRows by viewModel.pendingRows.collectAsState()
    val settledRows by viewModel.settledRows.collectAsState()
    val contactBalances by viewModel.contactBalances.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val expenseTransactions by viewModel.expenseTransactions.collectAsState()
    val unanchoredIncomes by viewModel.unanchoredIncomeTransactions.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { snackbarHostState.showSnackbar(it) }
    }

    var showNewContact by remember { mutableStateOf(false) }
    var showNewReceivable by remember { mutableStateOf(false) }
    var settleTarget by remember { mutableStateOf<ReceivableRowUi?>(null) }
    var showSettled by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Me deben",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Dinero que prestaste o que otros te deben de gastos compartidos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = FinancialGreen.copy(alpha = 0.12f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TOTAL POR COBRAR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(totalPending),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = FinancialGreen
                    )
                    Text(
                        text = "${pendingRows.size} deuda(s) pendiente(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showNewReceivable = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cargar deuda", maxLines = 1)
                }
                OutlinedButton(
                    onClick = { showNewContact = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Contacto", maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Pendientes")
            if (pendingRows.isEmpty()) {
                EmptyHint("Aún no tienes deudas por cobrar registradas.")
            } else {
                pendingRows.forEach { row ->
                    ReceivableCard(
                        row = row,
                        onSettle = { settleTarget = row },
                        onDelete = { viewModel.deleteReceivable(row.receivable) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Contactos")
            if (contactBalances.isEmpty()) {
                EmptyHint("Crea personas o sitios para cargarles lo que te deben.")
            } else {
                contactBalances.forEach { bal ->
                    ContactRow(
                        balance = bal,
                        onDelete = { viewModel.deleteContact(bal.contact) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { showSettled = !showSettled }) {
                Text(if (showSettled) "Ocultar cobrados" else "Ver cobrados (${settledRows.size})")
            }
            if (showSettled) {
                settledRows.forEach { row ->
                    ReceivableCard(
                        row = row,
                        onSettle = null,
                        onDelete = { viewModel.deleteReceivable(row.receivable) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showNewContact) {
        NewContactSheet(
            onDismiss = { showNewContact = false },
            onSave = { name, kind ->
                viewModel.addContact(name, kind)
                showNewContact = false
            }
        )
    }

    if (showNewReceivable) {
        NewReceivableSheet(
            contacts = contacts,
            expenseTransactions = expenseTransactions,
            onDismiss = { showNewReceivable = false },
            onQuickCreateContact = { name, onCreated ->
                viewModel.addContact(name, ContactKind.PERSON, onCreated)
            },
            onSave = { sourceId, note, shares ->
                viewModel.createReceivables(sourceId, note, shares)
                showNewReceivable = false
            }
        )
    }

    settleTarget?.let { target ->
        SettleDialog(
            accountName = target.contactName,
            amountCents = target.receivable.owedCents,
            accounts = accounts,
            existingIncomes = unanchoredIncomes,
            onDismiss = { settleTarget = null },
            onConfirm = { accountId, existingTransactionId ->
                viewModel.settle(target.receivable, accountId, existingTransactionId)
                settleTarget = null
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun ReceivableCard(
    row: ReceivableRowUi,
    onSettle: (() -> Unit)?,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.contactName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = row.sourceLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = row.dateLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatCentsToCop(row.receivable.owedCents),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (row.isSettled) FinancialGreen else MaterialTheme.colorScheme.onSurface
                )
                if (onSettle != null) {
                    TextButton(onClick = onSettle) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cobrar", fontSize = 12.sp)
                    }
                } else {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            if (onSettle != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    balance: ContactBalanceUi,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (balance.contact.kind == ContactKind.PLACE) Icons.Default.Place else Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = balance.contact.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (balance.contact.kind == ContactKind.PLACE) "Sitio" else "Persona",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                text = CurrencyFormatter.formatCentsToCop(balance.pendingCents),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (balance.pendingCents > 0L) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.outline
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar contacto",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewContactSheet(
    onDismiss: () -> Unit,
    onSave: (String, ContactKind) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf(ContactKind.PERSON) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "Nuevo contacto",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la persona o sitio") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = kind == ContactKind.PERSON,
                    onClick = { kind = ContactKind.PERSON },
                    label = { Text("Persona") }
                )
                FilterChip(
                    selected = kind == ContactKind.PLACE,
                    onClick = { kind = ContactKind.PLACE },
                    label = { Text("Sitio") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSave(name, kind) },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Crear contacto")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewReceivableSheet(
    contacts: List<ContactEntity>,
    expenseTransactions: List<TransactionWithDetails>,
    onDismiss: () -> Unit,
    onQuickCreateContact: (String, (Long) -> Unit) -> Unit,
    onSave: (sourceTransactionId: Long?, note: String?, shares: List<ShareInput>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedSource by remember { mutableStateOf<TransactionWithDetails?>(null) }
    var manualTotalRaw by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedIds by remember { mutableStateOf(listOf<Long>()) }
    var amountsRaw by remember { mutableStateOf(mapOf<Long, String>()) }
    var newContactName by remember { mutableStateOf("") }

    val totalCents = selectedSource?.transaction?.amountCents
        ?: parseCents(manualTotalRaw)

    val assignedCents = selectedIds.sumOf { parseCents(amountsRaw[it].orEmpty()) }
    val yourPartCents = (totalCents - assignedCents).coerceAtLeast(0L)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Cargar deuda a alguien",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Elige un gasto del histórico o crea un préstamo directo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Gasto de origen (opcional):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedSource == null,
                        onClick = { selectedSource = null },
                        label = { Text("Préstamo directo") }
                    )
                }
                items(expenseTransactions, key = { it.transaction.id }) { item ->
                    val label = "${item.transaction.note ?: item.transaction.merchant ?: "Gasto"} · ${CurrencyFormatter.formatCentsToCop(item.transaction.amountCents)}"
                    FilterChip(
                        selected = selectedSource?.transaction?.id == item.transaction.id,
                        onClick = {
                            selectedSource = item
                            amountsRaw = emptyMap()
                        },
                        label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            if (selectedSource == null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = manualTotalRaw,
                    onValueChange = { manualTotalRaw = it.filter { c -> c.isDigit() || c == ',' } },
                    label = { Text("Monto prestado") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", style = MaterialTheme.typography.bodyMedium)
                        Text(CurrencyFormatter.formatCentsToCop(totalCents), fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Asignado a otros", style = MaterialTheme.typography.bodyMedium)
                        Text(CurrencyFormatter.formatCentsToCop(assignedCents), fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tu parte", style = MaterialTheme.typography.bodyMedium, color = FinancialGreen)
                        Text(
                            CurrencyFormatter.formatCentsToCop(yourPartCents),
                            fontWeight = FontWeight.Bold,
                            color = FinancialGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("¿Quién debe pagar?", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            if (contacts.isEmpty()) {
                EmptyHint("Aún no tienes contactos. Crea uno abajo.")
            }
            contacts.forEach { contact ->
                val checked = selectedIds.contains(contact.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isOn ->
                            selectedIds = if (isOn) selectedIds + contact.id
                            else selectedIds - contact.id
                        }
                    )
                    Text(
                        text = contact.name,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedTextField(
                        value = amountsRaw[contact.id].orEmpty(),
                        onValueChange = { v ->
                            amountsRaw = amountsRaw + (contact.id to v.filter { c -> c.isDigit() || c == ',' })
                        },
                        enabled = checked,
                        singleLine = true,
                        label = { Text("Monto", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(130.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    val split = equalSplit(totalCents, selectedIds)
                    amountsRaw = amountsRaw +
                        split.mapValues { (_, cents) -> centsToRaw(cents) }
                },
                enabled = selectedIds.isNotEmpty() && totalCents > 0L,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Dividir en partes iguales entre ${selectedIds.size}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Nota (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Crear contacto rápido:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newContactName,
                    onValueChange = { newContactName = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val name = newContactName
                        onQuickCreateContact(name) { id ->
                            selectedIds = selectedIds + id
                        }
                        newContactName = ""
                    },
                    enabled = newContactName.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Crear")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    val shares = selectedIds.map { id ->
                        ShareInput(id, parseCents(amountsRaw[id].orEmpty()))
                    }
                    onSave(selectedSource?.transaction?.id, note.ifBlank { null }, shares)
                },
                enabled = selectedIds.isNotEmpty() && assignedCents > 0L,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Registrar deuda por cobrar")
            }
        }
    }
}

@Composable
private fun SettleDialog(
    accountName: String,
    amountCents: Long,
    accounts: List<AccountEntity>,
    existingIncomes: List<TransactionEntity>,
    onDismiss: () -> Unit,
    onConfirm: (accountId: Long, existingTransactionId: Long?) -> Unit
) {
    var linkExisting by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0L) }
    var selectedIncomeId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cobrar a $accountName") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "El cobro queda anclado a un movimiento real por ${CurrencyFormatter.formatCentsToCop(amountCents)}.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !linkExisting,
                        onClick = { linkExisting = false },
                        label = { Text("Ingreso nuevo") }
                    )
                    FilterChip(
                        selected = linkExisting,
                        onClick = { linkExisting = true },
                        enabled = existingIncomes.isNotEmpty(),
                        label = { Text("Anclar movimiento") }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (linkExisting) {
                    Text(
                        text = "Movimientos recibidos sin anclar:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (existingIncomes.isEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No hay ingresos reales sin anclar todavía.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    existingIncomes.forEach { tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedIncomeId = tx.id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedIncomeId == tx.id,
                                onClick = { selectedIncomeId = tx.id }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.note ?: tx.merchant ?: "Ingreso",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = CurrencyFormatter.formatCentsToCop(tx.amountCents),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Cuenta que recibe:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    accounts.forEach { account ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAccountId = account.id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedAccountId == account.id,
                                onClick = { selectedAccountId = account.id }
                            )
                            Text(account.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedAccountId, if (linkExisting) selectedIncomeId else null) },
                enabled = if (linkExisting) selectedIncomeId != null else accounts.isNotEmpty()
            ) { Text("Cobrar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun parseCents(raw: String): Long {
    if (raw.isBlank()) return 0L
    return runCatching { CurrencyFormatter.parseCopToCents(raw) }.getOrDefault(0L)
}

private fun centsToRaw(cents: Long): String {
    if (cents <= 0L) return ""
    val formatted = CurrencyFormatter.formatCentsToCop(cents) // $12.345,67
    val withoutSymbol = formatted.removePrefix("$").trim()
    return withoutSymbol.substringBefore(",").filter { it.isDigit() || it == '.' }
}

/** Reparte [total] en centavos en partes iguales, absorbiendo el residuo en las primeras. */
private fun equalSplit(total: Long, ids: List<Long>): Map<Long, Long> {
    if (ids.isEmpty() || total <= 0L) return ids.associateWith { 0L }
    val base = total / ids.size
    var remainder = total - base * ids.size
    return ids.associateWith {
        if (remainder > 0L) {
            remainder--
            base + 1L
        } else base
    }
}
