package com.personalfinance.cuentas.presentation.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.data.local.entities.AccountEntity

/**
 * Hoja de registro rápido de un INGRESO (aumento en la cuenta).
 * Permite digitar el monto, elegir la cuenta destino y una descripción opcional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickIncomeSheet(
    sheetState: SheetState,
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onSaveIncome: (accountId: Long, amountCents: Long, description: String) -> Unit
) {
    var rawDigits by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0L) }
    var description by remember { mutableStateOf("") }

    val amountCents = remember(rawDigits) {
        CurrencyFormatter.parseCopToCents(rawDigits)
    }

    val displayAmountText = remember(rawDigits, amountCents) {
        if (rawDigits.isEmpty()) {
            "$ 0"
        } else if (rawDigits.contains(",")) {
            val parts = rawDigits.split(",")
            val integerPart = parts[0].toLongOrNull() ?: 0L
            val formattedInt = CurrencyFormatter.formatCentsToCop(integerPart * 100L).substringBefore(",")
            "$formattedInt,${parts.getOrElse(1) { "" }}"
        } else {
            CurrencyFormatter.formatCentsToCop(amountCents)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrar Ingreso",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = FinancialGreen.copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Monto a ingresar",
                        style = MaterialTheme.typography.labelSmall,
                        color = FinancialGreen.copy(alpha = 0.7f)
                    )
                    Text(
                        text = displayAmountText,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        ),
                        color = FinancialGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cuenta destino:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Start)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accounts) { account ->
                    FilterChip(
                        selected = selectedAccountId == account.id,
                        onClick = { selectedAccountId = account.id },
                        label = { Text("${account.name} (${CurrencyFormatter.formatCentsToCop(account.currentBalanceCents)})") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Keypad(
                onDigit = { digit ->
                    if (rawDigits.length < 12) {
                        if (rawDigits.contains(",")) {
                            val decimals = rawDigits.substringAfter(",")
                            if (decimals.length < 2) rawDigits += digit
                        } else {
                            rawDigits += digit
                        }
                    }
                },
                onDecimal = {
                    if (!rawDigits.contains(",")) {
                        rawDigits = if (rawDigits.isEmpty()) "0," else "$rawDigits,"
                    }
                },
                onBackspace = {
                    if (rawDigits.isNotEmpty()) rawDigits = rawDigits.dropLast(1)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    if (amountCents > 0L && selectedAccountId != 0L) {
                        onSaveIncome(selectedAccountId, amountCents, description.trim())
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = amountCents > 0L && selectedAccountId != 0L,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FinancialGreen)
            ) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (amountCents > 0L) "Registrar ${CurrencyFormatter.formatCentsToCop(amountCents)}" else "Registrar Ingreso",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
