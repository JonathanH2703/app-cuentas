package com.personalfinance.cuentas.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.domain.anchor.AnchorOption
import com.personalfinance.cuentas.domain.anchor.AnchorRecommender
import com.personalfinance.cuentas.domain.anchor.AnchorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickExpenseSheet(
    sheetState: SheetState,
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>,
    anchorOptions: List<AnchorOption> = emptyList(),
    onDismiss: () -> Unit,
    onSaveExpense: (
        accountId: Long,
        categoryId: Long,
        amountCents: Long,
        note: String?,
        linkedDebtId: Long?,
        linkedRecurringItemId: Long?
    ) -> Unit
) {
    var rawDigits by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0L) }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: 0L) }
    var selectedAnchor by remember { mutableStateOf<AnchorOption?>(null) }
    var note by remember { mutableStateOf("") }
    var showNoteInput by remember { mutableStateOf(false) }

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
                text = "Registro Rápido de Gasto",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pantalla Principal del Monto (Gigante, estilo app bancaria con soporte para centavos)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Monto a descontar",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = displayAmountText,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector Horizontal de Cuenta
            Text(
                text = "Cuenta de pago:",
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

            // Selector Horizontal de Categorías con iconos rápidos
            Text(
                text = "Categoría:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Start)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryId == category.id
                    val catColor = try {
                        Color(android.graphics.Color.parseColor(category.colorHex))
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.primary
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategoryId = category.id }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) catColor else catColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = mapIconNameToVector(category.iconName),
                                contentDescription = category.name,
                                tint = if (isSelected) Color.White else catColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = category.name.split(" ").firstOrNull() ?: category.name,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) catColor else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Ancla opcional a una obligación (crédito o gasto fijo mensual)
            if (anchorOptions.isNotEmpty()) {
                val rankedAnchors = remember(anchorOptions, amountCents) {
                    AnchorRecommender.rank(anchorOptions, amountCents)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¿Para qué fue? (opcional):",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.Start)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedAnchor == null,
                            onClick = { selectedAnchor = null },
                            label = { Text("Sin anclar", maxLines = 1) }
                        )
                    }
                    items(rankedAnchors, key = { "${it.type}-${it.id}" }) { option ->
                        val isSelected = selectedAnchor?.let { it.type == option.type && it.id == option.id } == true
                        val recommended = AnchorRecommender.isRecommended(option, amountCents)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedAnchor = option },
                            label = {
                                Text(
                                    text = option.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = if (recommended) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Monto similar",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null
                        )
                    }
                }

                selectedAnchor?.let { anchor ->
                    Text(
                        text = anchor.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp)
                    )
                }
            }

            // Nota opcional
            if (showNoteInput) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota o comercio (opcional)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )
            } else {
                Text(
                    text = "+ Agregar nota o comercio",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { showNoteInput = true }
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teclado Numérico Ergonómico de Una Mano con soporte para coma decimal (1 a 9, coma, 0, Borrar)
            Keypad(
                onDigit = { digit ->
                    if (rawDigits.length < 12) {
                        // Limitar a 2 decimales si ya hay coma
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

            // Botón de Confirmación final (3er toque)
            Button(
                onClick = {
                    if (amountCents > 0L && selectedAccountId != 0L && selectedCategoryId != 0L) {
                        val anchor = selectedAnchor
                        onSaveExpense(
                            selectedAccountId,
                            selectedCategoryId,
                            amountCents,
                            note.ifBlank { null },
                            if (anchor?.type == AnchorType.DEBT) anchor.id else null,
                            if (anchor?.type == AnchorType.RECURRING) anchor.id else null
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = amountCents > 0L,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (amountCents > 0L) "Registrar ${CurrencyFormatter.formatCentsToCop(amountCents)}" else "Registrar Gasto",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun Keypad(
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(",", "0", "⌫")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { key ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                when (key) {
                                    "⌫" -> onBackspace()
                                    "," -> onDecimal()
                                    else -> onDigit(key)
                                }
                            },
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (key == "⌫") {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Borrar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun mapIconNameToVector(iconName: String): ImageVector {
    return when (iconName) {
        "restaurant" -> Icons.Default.Restaurant
        "home" -> Icons.Default.Home
        "directions_car" -> Icons.Default.DirectionsCar
        "medical_services" -> Icons.Default.MedicalServices
        "school" -> Icons.Default.School
        "local_cafe" -> Icons.Default.LocalCafe
        "sports_esports" -> Icons.Default.SportsEsports
        "tv" -> Icons.Default.Tv
        "shopping_bag" -> Icons.Default.ShoppingBag
        "credit_card" -> Icons.Default.CreditCard
        "savings" -> Icons.Default.Savings
        else -> Icons.Default.Category
    }
}
