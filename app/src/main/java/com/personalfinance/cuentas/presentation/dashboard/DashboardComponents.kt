package com.personalfinance.cuentas.presentation.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.core.ui.theme.FinancialAmber
import com.personalfinance.cuentas.core.ui.theme.FinancialBlue
import com.personalfinance.cuentas.core.ui.theme.FinancialGreen
import com.personalfinance.cuentas.core.ui.theme.FinancialRed
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity
import com.personalfinance.cuentas.domain.math.CashFlowDayProjection
import com.personalfinance.cuentas.domain.math.SafeToSpendCalculation
import com.personalfinance.cuentas.domain.math.SafeToSpendStatus
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

/**
 * Fila de estado de auditoría de seguridad
 */
@Composable
fun SecurityStatusRow(label: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                maxLines = 1
            )
        }
    }
}

/**
 * Fila de cuenta en Dashboard
 */
@Composable
fun AccountItemRow(account: AccountEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = account.type.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = CurrencyFormatter.formatCentsToCop(account.currentBalanceCents),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Tarjeta de Semáforo Inteligente Safe-to-Spend (Disponible para gastar hoy y este mes).
 */
@Composable
fun SafeToSpendCard(
    calculation: SafeToSpendCalculation,
    modifier: Modifier = Modifier
) {
    val statusColor = when (calculation.status) {
        SafeToSpendStatus.GREEN -> FinancialGreen
        SafeToSpendStatus.YELLOW -> FinancialAmber
        SafeToSpendStatus.RED -> FinancialRed
    }

    val statusIcon = when (calculation.status) {
        SafeToSpendStatus.GREEN -> Icons.Default.CheckCircle
        SafeToSpendStatus.YELLOW -> Icons.Default.Warning
        SafeToSpendStatus.RED -> Icons.Default.Warning
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DISPONIBLE PARA GASTAR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp),
                            color = statusColor
                        )
                        Text(
                            text = "Para tus compras y gustos sin descuadrarte",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = when (calculation.status) {
                            SafeToSpendStatus.GREEN -> "VAS BIEN"
                            SafeToSpendStatus.YELLOW -> "CUIDADO"
                            SafeToSpendStatus.RED -> "LLEGASTE AL TOPE"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Monto Disponible Total y Diario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Libre este mes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(calculation.safeToSpendTotalCents),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (calculation.safeToSpendTotalCents > 0L) MaterialTheme.colorScheme.onSurface else FinancialRed,
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Gasto diario (${calculation.daysRemainingInMonth}d)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${CurrencyFormatter.formatCentsToCop(calculation.safeToSpendDailyCents)} / día",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mensaje explicativo
            Text(
                text = calculation.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Bloque con gráfico Donut Canvas y progreso de sobres del modelo de asignación.
 */
@Composable
fun AllocationEnvelopesSection(
    modelName: String,
    blocks: List<BlockAllocationUiModel>,
    eventsByBlock: Map<String, List<com.personalfinance.cuentas.presentation.events.EventUi>> = emptyMap(),
    onConfigureModelClick: () -> Unit,
    onAdjustBudgetClick: (BlockAllocationUiModel) -> Unit,
    onViewDetailClick: (BlockAllocationUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tus Bolsillos: $modelName",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Distribución y control de tus gastos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                TextButton(onClick = onConfigureModelClick) {
                    Text("Cambiar", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gráfico Donut Moderno con Canvas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ModernDonutChart(
                        blocks = blocks,
                        modifier = Modifier.size(110.dp)
                    )
                    Text(
                        text = "100%",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    blocks.forEach { block ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(block.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = block.block.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${block.block.percentage}%",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = block.color,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Tarjetas individuales de cada sobre (Sobre / Envelope Progress)
            Text(
                text = "Sobres de Gasto de este Mes",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(8.dp))

            blocks.forEach { item ->
                val blockKey = item.block.type.name  // "NEEDS", "WANTS", "SAVINGS"
                val blockEvents = eventsByBlock[blockKey] ?: emptyList()
                EnvelopeItemRow(
                    item = item,
                    events = blockEvents,
                    onEditBudget = { onAdjustBudgetClick(item) },
                    onViewDetail = { onViewDetailClick(item) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EnvelopeItemRow(
    item: BlockAllocationUiModel,
    events: List<com.personalfinance.cuentas.presentation.events.EventUi> = emptyList(),
    onEditBudget: () -> Unit,
    onViewDetail: () -> Unit
) {
    val blockType = item.block.type

    // ── Calculos de progreso ──────────────────────────────────────────────────
    // Para NEEDS: progreso = pagado / total obligaciones del mes
    // Para WANTS: progreso = gastado / asignado dinámico
    // Para SAVINGS: progreso = ahorrado / meta sugerida (puede superar 100%)
    val (progress, isOver) = when (blockType) {
        com.personalfinance.cuentas.data.local.entities.BlockType.NEEDS,
        com.personalfinance.cuentas.data.local.entities.BlockType.DEBTS -> {
            val total = item.allocatedCents.coerceAtLeast(1L)
            val p = (item.spentCents.toFloat() / total.toFloat()).coerceIn(0f, 1f)
            p to (item.spentCents > total)
        }
        com.personalfinance.cuentas.data.local.entities.BlockType.WANTS -> {
            val limit = item.budgetLimitCents.takeIf { it > 0L } ?: item.allocatedCents.coerceAtLeast(1L)
            val p = (item.spentCents.toFloat() / limit.toFloat()).coerceIn(0f, 1.5f)
            p to (item.spentCents > limit)
        }
        com.personalfinance.cuentas.data.local.entities.BlockType.SAVINGS -> {
            val meta = item.allocatedCents.coerceAtLeast(1L)
            val realSavForProgress = item.realSavingsCents.coerceAtLeast(0L)
            val p = (realSavForProgress.toFloat() / meta.toFloat()).coerceIn(0f, 1f)
            p to false
        }
        else -> {
            val limit = item.budgetLimitCents.takeIf { it > 0L } ?: item.allocatedCents.coerceAtLeast(1L)
            val p = (item.spentCents.toFloat() / limit.toFloat()).coerceIn(0f, 1f)
            p to (item.spentCents > limit)
        }
    }

    val progressColor = when {
        isOver -> FinancialRed
        blockType == com.personalfinance.cuentas.data.local.entities.BlockType.SAVINGS && progress < 0.5f -> FinancialAmber
        else -> item.color
    }

    Surface(
        onClick = onViewDetail,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Fila cabecera: nombre + chip % + badge adaptado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = androidx.compose.ui.Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(item.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.block.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = item.color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${item.block.percentage}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            color = item.color,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                // Badge de estado
                val badgeText = when (blockType) {
                    com.personalfinance.cuentas.data.local.entities.BlockType.NEEDS,
                    com.personalfinance.cuentas.data.local.entities.BlockType.DEBTS ->
                        if (item.spentCents >= item.allocatedCents && item.allocatedCents > 0L) "Al dia" else "Pendiente"
                    com.personalfinance.cuentas.data.local.entities.BlockType.SAVINGS ->
                        if (item.spentCents >= item.allocatedCents && item.allocatedCents > 0L) "Meta alcanzada" else "En curso"
                    else -> if (isOver) "Excedido" else "Disponible"
                }
                val badgeColor = when {
                    blockType == com.personalfinance.cuentas.data.local.entities.BlockType.NEEDS && item.spentCents >= item.allocatedCents && item.allocatedCents > 0L -> FinancialGreen
                    blockType == com.personalfinance.cuentas.data.local.entities.BlockType.SAVINGS && item.spentCents >= item.allocatedCents && item.allocatedCents > 0L -> FinancialGreen
                    isOver -> FinancialRed
                    else -> MaterialTheme.colorScheme.outline
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Barra de progreso con label debajo
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fila de números
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                when (blockType) {
                    com.personalfinance.cuentas.data.local.entities.BlockType.NEEDS,
                    com.personalfinance.cuentas.data.local.entities.BlockType.DEBTS -> {
                        Column {
                            Text(
                                text = "Pagado este mes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = CurrencyFormatter.formatCentsToCop(item.spentCents),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isOver) FinancialRed else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (item.spentCents >= item.allocatedCents && item.allocatedCents > 0L)
                                    "Todo pagado"
                                else
                                    "Falta: ${CurrencyFormatter.formatCentsToCop((item.allocatedCents - item.spentCents).coerceAtLeast(0L))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (item.spentCents >= item.allocatedCents && item.allocatedCents > 0L) FinancialGreen else FinancialAmber
                            )
                            Text(
                                text = "Total mes: ${CurrencyFormatter.formatCentsToCop(item.allocatedCents)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    com.personalfinance.cuentas.data.local.entities.BlockType.WANTS -> {
                        // Izquierda: lo que ya gastaste en ocio este mes
                        Column {
                            Text(
                                text = "Gastado en ocio",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = CurrencyFormatter.formatCentsToCop(item.spentCents),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isOver) FinancialRed else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Derecha: cuánto puedes gastar aún / si te excediste
                        Column(horizontalAlignment = Alignment.End) {
                            val limit = item.budgetLimitCents.takeIf { it > 0L } ?: item.allocatedCents
                            Text(
                                text = if (isOver)
                                    "Te pasaste: ${CurrencyFormatter.formatCentsToCop(item.spentCents - limit)}"
                                else
                                    "Puedes gastar: ${CurrencyFormatter.formatCentsToCop((limit - item.spentCents).coerceAtLeast(0L))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOver) FinancialRed else FinancialGreen
                            )
                            Text(
                                text = "Max este mes: ${CurrencyFormatter.formatCentsToCop(limit)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    com.personalfinance.cuentas.data.local.entities.BlockType.SAVINGS -> {
                        val realSav = item.realSavingsCents
                        val isNeg   = realSav < 0L
                        val minimo  = item.allocatedCents
                        val deficit = (minimo - realSav).coerceAtLeast(0L)
                        val isAbove = realSav >= minimo && minimo > 0L

                        // Layout vertical: numero grande arriba, estado y minimo abajo
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text(
                                        text = "Dinero quieto",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatCentsToCop(realSav),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isNeg) FinancialRed else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Mín. sugerido (20%)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatCentsToCop(minimo),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Estado en una línea propia con chip
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    isNeg   -> FinancialRed.copy(alpha = 0.10f)
                                    isAbove -> FinancialGreen.copy(alpha = 0.10f)
                                    else    -> FinancialAmber.copy(alpha = 0.10f)
                                }
                            ) {
                                Text(
                                    text = when {
                                        isNeg   -> "No alcanza: ${CurrencyFormatter.formatCentsToCop(-realSav)} en deficit"
                                        isAbove -> "Por encima del minimo ✓"
                                        else    -> "Falta: ${CurrencyFormatter.formatCentsToCop(deficit)} para el minimo"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                                    color = when {
                                        isNeg   -> FinancialRed
                                        isAbove -> FinancialGreen
                                        else    -> FinancialAmber
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = CurrencyFormatter.formatCentsToCop(item.spentCents),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Fila inferior: hint de detalle
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Ver detalle  >",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = item.color
                )
            }

            // ── Fechas asignadas a este bolsillo ──────────────────────────────
            if (events.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Aparta para:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(4.dp))
                events.forEach { ev ->
                    val monthly = ev.monthlySaving ?: 0L
                    val progress = ev.progressPct / 100f
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = ev.entity.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "En ${ev.daysUntil} dias  ·  ${ev.progressPct}% acumulado",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.outline
                            )
                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { progress.coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp).height(4.dp)
                                    .clip(CircleShape),
                                color = if (progress >= 1f) FinancialGreen else item.color,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = item.color.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${CurrencyFormatter.formatCentsToCop(monthly)}/mes",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold, fontSize = 10.sp
                                ),
                                color = item.color,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Diálogo con el detalle ampliado de un bolsillo: cálculos, fechas de pago y plan de apartado.
 */
@Composable
fun EnvelopeDetailDialog(
    detail: EnvelopeDetailUi,
    accentColor: Color,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Detalle: ${detail.blockName}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Encabezado con el resultado clave
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = (if (detail.headlinePositive) FinancialGreen else FinancialRed).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = detail.headline,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (detail.headlinePositive) FinancialGreen else FinancialRed,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Filas de cálculo
                detail.rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = row.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = row.value,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (row.highlight) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = when {
                                row.isNegative -> FinancialRed
                                row.highlight -> accentColor
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }

                // Desglose itemizado: qué compone el total y qué ya está pagado
                if (detail.items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detail.itemsTitle,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.outline
                    )
                    detail.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (item.isSettled) Icons.Default.CheckCircle else Icons.Default.Event,
                                contentDescription = null,
                                tint = if (item.isSettled) FinancialGreen else FinancialAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${item.subtitle} · ${if (item.isSettled) "Pagado" else "Pendiente"}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (item.isSettled) FinancialGreen else MaterialTheme.colorScheme.outline
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = CurrencyFormatter.formatCentsToCop(item.amountCents),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (item.isSettled) FinancialGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Sección de fechas / plan
                if (detail.steps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detail.stepsTitle,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.outline
                    )
                    detail.steps.forEach { step ->
                        val stepColor = when (step.status) {
                            EnvelopeStepStatus.INCOME -> FinancialGreen
                            EnvelopeStepStatus.OK -> MaterialTheme.colorScheme.onSurface
                            EnvelopeStepStatus.SHORT -> FinancialRed
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = step.dateLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = stepColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (step.note.isNotBlank()) {
                                    Text(
                                        text = step.note,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (step.status == EnvelopeStepStatus.SHORT) FinancialRed else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step.amountLabel,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = stepColor
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No hay fechas ni ingresos programados para calcular.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun CreditCardObligationRow(
    card: CreditCardObligationUi
) {
    val today = java.time.LocalDate.now()
    val dueDay = card.paymentDueDay
    val dueDate = if (dueDay >= today.dayOfMonth) {
        today.withDayOfMonth(minOf(dueDay, today.lengthOfMonth()))
    } else {
        val next = today.plusMonths(1)
        next.withDayOfMonth(minOf(dueDay, next.lengthOfMonth()))
    }
    val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate).toInt()

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(card.nextPaymentCents),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (card.countsInTotal)
                            "Vence en ${daysUntil}d (${dueDate.dayOfMonth} ${dueDate.month.name.lowercase().take(3)})"
                        else
                            "Próx. pago (${dueDate.dayOfMonth} ${dueDate.month.name.lowercase().take(3)}) · no suma este mes",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (card.countsInTotal) MaterialTheme.colorScheme.outline else FinancialAmber,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Donut Chart dibujado directamente sobre Canvas.
 */
@Composable
fun ModernDonutChart(
    blocks: List<BlockAllocationUiModel>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 18.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
        val arcSize = Size(diameter, diameter)

        var startAngle = -90f
        val totalPercentage = blocks.sumOf { it.block.percentage }.toFloat().coerceAtLeast(1f)

        for (b in blocks) {
            val sweep = (b.block.percentage / totalPercentage) * 360f
            drawArc(
                color = b.color,
                startAngle = startAngle,
                sweepAngle = sweep - 2f, // Pequeño espacio visual entre arcos
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += sweep
        }
    }
}

/**
 * Gráfico Sparkline Vectorial de Flujo de Caja Proyectado a 30, 60 y 90 días.
 */
@Composable
fun CashFlowChartCard(
    projections: List<CashFlowDayProjection>,
    selectedHorizon: Int,
    onHorizonSelected: (Int) -> Unit,
    isProjectedView: Boolean = true,
    onViewModeChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = if (isProjectedView) "Proyección de tu dinero" else "Evolución de saldo actual",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isProjectedView) "Con ingresos programados y compromisos" else "Con dinero disponible hoy y pagos fijos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                if (onViewModeChange != null) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(2.dp)
                    ) {
                        Surface(
                            onClick = { onViewModeChange(false) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isProjectedView) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Text(
                                text = "Actual",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (!isProjectedView) FontWeight.Bold else FontWeight.Normal),
                                color = if (!isProjectedView) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            onClick = { onViewModeChange(true) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isProjectedView) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Text(
                                text = "Proyectado",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isProjectedView) FontWeight.Bold else FontWeight.Normal),
                                color = if (isProjectedView) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector horizontal de días ocupando el ancho completo en proporción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    30 to "30 Días",
                    60 to "60 Días",
                    90 to "90 Días"
                ).forEach { (days, label) ->
                    FilterChip(
                        selected = selectedHorizon == days,
                        onClick = { onHorizonSelected(days) },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedHorizon == days) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val currentHorizonList = projections.take(selectedHorizon)
            val balancePoints = currentHorizonList.map { it.projectedNetBalanceCents }
            val savingsPoints = currentHorizonList.map { it.projectedSavingsCents }

            // Escala compartida para que las dos líneas (saldo y ahorro) sean comparables.
            val combinedMin = minOf(balancePoints.minOrNull() ?: 0L, savingsPoints.minOrNull() ?: 0L)
            val combinedMax = maxOf(balancePoints.maxOrNull() ?: 0L, savingsPoints.maxOrNull() ?: 0L)

            val finalBalance = balancePoints.lastOrNull() ?: 0L
            val finalSavings = savingsPoints.lastOrNull() ?: 0L
            val chartColor = if (finalBalance >= 0L) FinancialBlue else FinancialRed

            // Gráfico Vectorial Lineal en Canvas (saldo disponible + ahorro acumulado)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (currentHorizonList.size < 2) return@Canvas

                    val range = maxOf(1L, combinedMax - combinedMin).toFloat()
                    val widthStep = size.width / (currentHorizonList.size - 1)

                    fun yFor(value: Long): Float {
                        val normalized = (value - combinedMin).toFloat() / range
                        return size.height - (normalized * (size.height - 20.dp.toPx())) - 10.dp.toPx()
                    }

                    // Construye el trazo de la línea y su relleno degradado para una serie.
                    fun buildPaths(values: List<Long>): Pair<Path, Path> {
                        val line = Path()
                        val fill = Path()
                        values.forEachIndexed { index, value ->
                            val x = index * widthStep
                            val y = yFor(value)
                            if (index == 0) {
                                line.moveTo(x, y)
                                fill.moveTo(x, size.height)
                                fill.lineTo(x, y)
                            } else {
                                line.lineTo(x, y)
                                fill.lineTo(x, y)
                            }
                            if (index == values.lastIndex) {
                                fill.lineTo(x, size.height)
                                fill.close()
                            }
                        }
                        return line to fill
                    }

                    // 1) Saldo disponible proyectado
                    val (balanceLine, balanceFill) = buildPaths(balancePoints)
                    drawPath(
                        path = balanceFill,
                        brush = Brush.verticalGradient(
                            colors = listOf(chartColor.copy(alpha = 0.22f), Color.Transparent)
                        )
                    )
                    drawPath(
                        path = balanceLine,
                        color = chartColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 2) Ahorro acumulado si solo se gasta lo obligatorio
                    val (savingsLine, savingsFill) = buildPaths(savingsPoints)
                    drawPath(
                        path = savingsFill,
                        brush = Brush.verticalGradient(
                            colors = listOf(FinancialGreen.copy(alpha = 0.16f), Color.Transparent)
                        )
                    )
                    drawPath(
                        path = savingsLine,
                        color = FinancialGreen,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Leyenda de las dos líneas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendEntry(color = chartColor, label = "Saldo disponible")
                LegendEntry(color = FinancialGreen, label = "Ahorro acumulado")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saldo estimado en ${selectedHorizon} días:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = CurrencyFormatter.formatCentsToCop(finalBalance),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (finalBalance >= 0L) FinancialGreen else FinancialRed,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ahorro acumulado en ${selectedHorizon} días:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = CurrencyFormatter.formatCentsToCop(finalSavings),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = FinancialGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Punto de color con etiqueta para la leyenda de la gráfica de flujo de caja.
 */
@Composable
private fun LegendEntry(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Fila unificada de la tarjeta de Obligaciones Fijas, para ordenarlas por día de pago.
 */
private data class ObligationRowUi(
    val dueDay: Int,
    val typeOrder: Int,
    val debt: com.personalfinance.cuentas.data.local.entities.DebtEntity? = null,
    val card: CreditCardObligationUi? = null,
    val item: RecurringItemEntity? = null,
    val isThisMonth: Boolean = true
)

/**
 * Sección de Obligaciones Fijas Mensuales (Plan de datos, parqueadero, arriendo, etc.)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecurringItemsSection(
    items: List<RecurringItemEntity>,
    debts: List<com.personalfinance.cuentas.data.local.entities.DebtEntity>,
    cards: List<CreditCardObligationUi> = emptyList(),
    totalMonthlyCents: Long,
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>,
    onAddClick: (title: String, amountCents: Long, accountId: Long, categoryId: Long, dayOfMonth: Int) -> Unit,
    onPayClick: (RecurringItemEntity) -> Unit,
    onEditClick: (RecurringItemEntity) -> Unit,
    onDeleteClick: (RecurringItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Obligaciones Fijas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Gastos que pagas cada mes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar obligación",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (items.isEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Agrega tus gastos fijos mensuales",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Plan de datos, parqueadero, arriendo, suscripciones...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))

                // Resumen rápido
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total mensual fijo:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = CurrencyFormatter.formatCentsToCop(totalMonthlyCents),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Obligaciones separadas: este mes primero, próximo mes debajo del divisor.
                val today = java.time.LocalDate.now()
                val endOfThisMonth = today.withDayOfMonth(today.lengthOfMonth())

                val orderedObligations = remember(debts, cards, items) {
                    buildList {
                        debts.forEach { debt ->
                            val due = com.personalfinance.cuentas.domain.schedule.DueDates
                                .effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today)
                            add(ObligationRowUi(dueDay = debt.dueDayOfMonth, typeOrder = 0, debt = debt, isThisMonth = !due.isAfter(endOfThisMonth)))
                        }
                        cards.forEach { card ->
                            add(ObligationRowUi(dueDay = card.paymentDueDay, typeOrder = 1, card = card, isThisMonth = card.isThisMonth))
                        }
                        items.forEach { item ->
                            val day = runCatching {
                                Instant.ofEpochMilli(item.nextDueDateMillis)
                                    .atZone(ZoneId.systemDefault()).dayOfMonth
                            }.getOrDefault(31)
                            val itemDate = runCatching {
                                Instant.ofEpochMilli(item.nextDueDateMillis)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()
                            }.getOrElse { endOfThisMonth }
                            add(ObligationRowUi(dueDay = day, typeOrder = 2, item = item, isThisMonth = !itemDate.isAfter(endOfThisMonth)))
                        }
                    }.sortedWith(compareBy({ !it.isThisMonth }, { it.dueDay }, { it.typeOrder }))
                }

                orderedObligations.filter { it.isThisMonth }.forEach { row ->
                    when {
                        row.debt != null -> DebtObligationRow(row.debt)
                        row.card != null -> CreditCardObligationRow(card = row.card)
                        row.item != null -> RecurringItemRow(
                            item = row.item,
                            onPayClick = { onPayClick(row.item) },
                            onEditClick = { onEditClick(row.item) },
                            onDeleteClick = { onDeleteClick(row.item) }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                val nextMonthObl = orderedObligations.filter { !it.isThisMonth }
                if (nextMonthObl.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material3.HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Próximo mes",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    nextMonthObl.forEach { row ->
                        when {
                            row.debt != null -> DebtObligationRow(row.debt)
                            row.card != null -> CreditCardObligationRow(card = row.card)
                            row.item != null -> RecurringItemRow(
                                item = row.item,
                                onPayClick = { onPayClick(row.item) },
                                onEditClick = { onEditClick(row.item) },
                                onDeleteClick = { onDeleteClick(row.item) }
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddRecurringDialog(
            accounts = accounts,
            categories = categories,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, accountId, categoryId, day ->
                onAddClick(title, amount, accountId, categoryId, day)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DebtObligationRow(
    debt: com.personalfinance.cuentas.data.local.entities.DebtEntity
) {
    val today = java.time.LocalDate.now()
    val dueDay = debt.dueDayOfMonth
    val dueDate = if (dueDay >= today.dayOfMonth) {
        today.withDayOfMonth(minOf(dueDay, today.lengthOfMonth()))
    } else {
        val next = today.plusMonths(1)
        next.withDayOfMonth(minOf(dueDay, next.lengthOfMonth()))
    }
    val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate).toInt()

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = debt.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(debt.monthlyInstallmentCents),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vence en ${daysUntil}d (${dueDate.dayOfMonth} ${dueDate.month.name.lowercase().take(3)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RecurringItemRow(
    item: RecurringItemEntity,
    onPayClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dueDate = Instant.ofEpochMilli(item.nextDueDateMillis)
        .atZone(ZoneId.systemDefault()).toLocalDate()
    val today = java.time.LocalDate.now()
    val isPastDue = dueDate.isBefore(today) || dueDate.isEqual(today)
    val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate).toInt()

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isPastDue)
            FinancialAmber.copy(alpha = 0.1f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(item.amountCents),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPastDue) "Vence hoy" else "Vence en ${daysUntil}d (${dueDate.dayOfMonth} ${dueDate.month.name.lowercase().take(3)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPastDue) FinancialAmber else MaterialTheme.colorScheme.outline
                    )
                }
            }

            var menuExpanded by remember { mutableStateOf(false) }

            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones de obligación",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Registrar pago") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FinancialGreen
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onPayClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Editar obligación") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onEditClick()
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
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddRecurringDialog(
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, amountCents: Long, accountId: Long, categoryId: Long, dayOfMonth: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountRaw by remember { mutableStateOf("") }
    var dueDayRaw by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0L) }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: 0L) }

    val amountCents = remember(amountRaw) {
        CurrencyFormatter.parseCopToCents(amountRaw)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Nueva Obligación Fija",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Registra un gasto que pagas todos los meses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Ej: Plan Claro, Parqueadero, Netflix") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountRaw,
                    onValueChange = { newVal ->
                        val filtered = newVal.filter { it.isDigit() || it == ',' }
                        if (filtered.count { it == ',' } <= 1) {
                            val parts = filtered.split(",")
                            if (parts.size == 2 && parts[1].length > 2) return@OutlinedTextField
                            amountRaw = filtered
                        }
                    },
                    label = { Text("Valor mensual (COP)") },
                    placeholder = { Text("Ej: 45000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueDayRaw,
                    onValueChange = { newVal ->
                        val filtered = newVal.filter { it.isDigit() }
                        if (filtered.length <= 2) dueDayRaw = filtered
                    },
                    label = { Text("Día de vencimiento (1-31)") },
                    placeholder = { Text("Ej: 15") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Cuenta de pago
                Text(
                    "Cuenta de pago:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(accounts) { account ->
                        FilterChip(
                            selected = selectedAccountId == account.id,
                            onClick = { selectedAccountId = account.id },
                            label = { Text(account.name, maxLines = 1, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Categoría
                Text(
                    "Categoría:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = { selectedCategoryId = cat.id },
                            label = { Text(cat.name, maxLines = 1, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dueDay = dueDayRaw.toIntOrNull()?.coerceIn(1, 31) ?: 1
                    if (title.isNotBlank() && amountCents > 0L) {
                        onConfirm(title, amountCents, selectedAccountId, selectedCategoryId, dueDay)
                    }
                },
                enabled = title.isNotBlank() && amountCents > 0L && dueDayRaw.isNotBlank()
            ) {
                Text("Agregar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun EditRecurringDialog(
    item: RecurringItemEntity,
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, amountCents: Long, accountId: Long, categoryId: Long, dayOfMonth: Int) -> Unit
) {
    var title by remember { mutableStateOf(item.title) }
    var amountRaw by remember {
        mutableStateOf(CurrencyFormatter.formatCentsForInput(item.amountCents))
    }
    val currentDay = remember(item.nextDueDateMillis) {
        Instant.ofEpochMilli(item.nextDueDateMillis)
            .atZone(ZoneId.systemDefault()).toLocalDate().dayOfMonth.toString()
    }
    var dueDayRaw by remember { mutableStateOf(currentDay) }
    var selectedAccountId by remember { mutableStateOf(item.accountId) }
    var selectedCategoryId by remember { mutableStateOf(item.categoryId) }

    val amountCents = remember(amountRaw) {
        CurrencyFormatter.parseCopToCents(amountRaw)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Editar Obligación Fija",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Ej: Plan Claro, Parqueadero") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountRaw,
                    onValueChange = { newVal ->
                        val filtered = newVal.filter { it.isDigit() || it == ',' }
                        if (filtered.count { it == ',' } <= 1) {
                            val parts = filtered.split(",")
                            if (parts.size == 2 && parts[1].length > 2) return@OutlinedTextField
                            amountRaw = filtered
                        }
                    },
                    label = { Text("Valor mensual (COP)") },
                    placeholder = { Text("Ej: 45000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueDayRaw,
                    onValueChange = { newVal ->
                        val filtered = newVal.filter { it.isDigit() }
                        if (filtered.length <= 2) dueDayRaw = filtered
                    },
                    label = { Text("Día de vencimiento (1-31)") },
                    placeholder = { Text("Ej: 15") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Cuenta de pago
                Text(
                    "Cuenta de pago:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(accounts) { account ->
                        FilterChip(
                            selected = selectedAccountId == account.id,
                            onClick = { selectedAccountId = account.id },
                            label = { Text(account.name, maxLines = 1, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Categoría
                Text(
                    "Categoría:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = { selectedCategoryId = cat.id },
                            label = { Text(cat.name, maxLines = 1, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dueDay = dueDayRaw.toIntOrNull()?.coerceIn(1, 31) ?: 1
                    if (title.isNotBlank() && amountCents > 0L) {
                        onConfirm(title, amountCents, selectedAccountId, selectedCategoryId, dueDay)
                    }
                },
                enabled = title.isNotBlank() && amountCents > 0L && dueDayRaw.isNotBlank()
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

/**
 * Tarjeta compacta de resumen financiero para el Hero del Dashboard.
 * Versión simplificada: muestra saldo real, obligaciones y permite ver la línea de tiempo.
 */
@Composable
fun CompactFinancialHeroCard(
    consolidatedCents: Long,
    staticSavingsCents: Long,
    currentMonthPendingCents: Long = 0L,
    currentMonthNetCents: Long = 0L,
    onShowTimeline: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val today = java.time.LocalDate.now()
    val daysRemaining = maxOf(1, today.lengthOfMonth() - today.dayOfMonth + 1)

    // "Libre hoy" = saldo + ingresos pendientes este mes - obligaciones este mes
    val freeToday    = currentMonthNetCents
    val isNegative   = freeToday < 0L
    val dailyCents   = if (freeToday > 0L) freeToday / daysRemaining else 0L

    val statusColor = when {
        isNegative || consolidatedCents < 0L -> FinancialRed
        freeToday < consolidatedCents * 0.10 -> FinancialAmber
        else -> FinancialGreen
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {

            // Encabezado: etiqueta + botón Planeado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SALDO ACTUAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp, fontWeight = FontWeight.Bold, fontSize = 9.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    onClick = onShowTimeline,
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.06f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Planeado",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold, fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Saldo actual en cuenta (número grande)
            Text(
                text = CurrencyFormatter.formatCentsToCop(consolidatedCents),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = if (consolidatedCents < 0L) FinancialRed
                        else MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Fila: Libre hoy (neto del mes) + Por día
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Libre este mes",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(freeToday),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        maxLines = 1
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Por día (${daysRemaining}d)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${CurrencyFormatter.formatCentsToCop(dailyCents)}/d",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fila inferior: Obligaciones pendientes este mes
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (currentMonthPendingCents > 0L)
                    FinancialRed.copy(alpha = 0.08f)
                else
                    FinancialGreen.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Obligaciones este mes",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            maxLines = 1
                        )
                        Text(
                            text = if (currentMonthPendingCents == 0L) "Al día ✓"
                                   else CurrencyFormatter.formatCentsToCop(currentMonthPendingCents),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (currentMonthPendingCents == 0L) FinancialGreen else FinancialRed,
                            maxLines = 1
                        )
                    }
                    Icon(
                        imageVector = if (currentMonthPendingCents == 0L)
                            Icons.Default.CheckCircle else Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = if (currentMonthPendingCents == 0L)
                            FinancialGreen.copy(alpha = 0.6f)
                        else
                            FinancialRed.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Ahorro protegido (si aplica)
            if (staticSavingsCents > 0L) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.05f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Ahorro protegido",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                                maxLines = 1
                            )
                            Text(
                                text = CurrencyFormatter.formatCentsToCop(staticSavingsCents),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = FinancialBlue,
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = FinancialBlue.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de posición neta: muestra lo que terceros deben ("por cobrar") y la deuda neta
 * de tarjetas (deuda total de tarjetas − lo que me deben). Solo se muestra si hay cobros.
 */
@Composable
fun NetPositionCard(
    receivablesCents: Long,
    totalCardDebtCents: Long
) {
    if (receivablesCents <= 0L) return
    val netCardDebt = (totalCardDebtCents - receivablesCents).coerceAtLeast(0L)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = FinancialGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Por cobrar (me deben)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = CurrencyFormatter.formatCentsToCop(receivablesCents),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = FinancialGreen,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Deuda neta de tarjetas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = CurrencyFormatter.formatCentsToCop(netCardDebt),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Tarjeta dinámica: cuánto dinero debe quedar disponible para cubrir las obligaciones que
 * vencen antes de que entre el próximo ingreso. Toca para ver el detalle.
 */
@Composable
fun NextIncomeReserveCard(
    reserve: NextIncomeReserveUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                tint = FinancialAmber,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reserva para el próximo mes",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Vence antes de tu ingreso del ${reserve.nextIncomeLabel}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = CurrencyFormatter.formatCentsToCop(reserve.reserveCents),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = if (reserve.reserveCents > 0L) FinancialAmber
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun NextIncomeReserveDialog(
    reserve: NextIncomeReserveUi,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reserva para el próximo mes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Conserva este dinero para cubrir lo que vence antes de tu próximo " +
                        "ingreso (${reserve.nextIncomeLabel}).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                reserve.items.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = row.title,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = row.dateLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = CurrencyFormatter.formatCentsToCop(row.amountCents),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total a reservar",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = CurrencyFormatter.formatCentsToCop(reserve.reserveCents),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = FinancialAmber
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}

@Composable
fun PlannedMovementsDialog(
    movements: List<PlannedMovementUi>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Movimientos Planeados") },
        text = {
            if (movements.isEmpty()) {
                Text("No hay movimientos programados para los próximos 30 días.")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    movements.forEach { mv ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mv.dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = mv.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (mv.isIncome) "+${CurrencyFormatter.formatCentsToCop(mv.amountCents)}" 
                                           else "-${CurrencyFormatter.formatCentsToCop(mv.amountCents)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (mv.isIncome) FinancialGreen else FinancialRed,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Saldo: ${CurrencyFormatter.formatCentsToCop(mv.balanceAfterCents)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
